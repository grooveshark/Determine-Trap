package com.grooveshark.determinetrap;

import com.grooveshark.determinetrap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.view.View;
import android.util.Log;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;


public class GameActivity extends Activity implements DialogInterface.OnClickListener
{
    private static final int FAIL_DIALOG_ID = 0;
    private static final int SUCCESS_DIALOG_ID = 1;
    private static final int GAME_OVER_ID = 2;
    
    private static final int NUM_ROUNDS = 3;
    private static final int NUM_TRAPS_PER_ROUND = 3;
    private static final int NUM_ROUNDS_TO_WIN = 2;

    private Button trapButton;
    private Button notTrapButton;
    private TextView statementTextView;
    private TextView scoreTextView;
    private TextView roundTextView;

    private List<Trap> traps;
    private Trap currentTrap;
    private MediaPlayer trapSound;
    private MediaPlayer successSound;
    private int numTrapsPlayed;
    private int currentRound;
    private int numCorrect;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        trapButton = (Button) findViewById(R.id.trap_button);
        notTrapButton = (Button) findViewById(R.id.not_trap_button);
        statementTextView  = (TextView) findViewById(R.id.statement);
        scoreTextView = (TextView) findViewById(R.id.score);
        roundTextView = (TextView) findViewById(R.id.round);

        trapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (currentTrap.isTrap) {
                        // Correct!
                        numCorrect++;
                        successSound.start();
                        showDialog(SUCCESS_DIALOG_ID);
                    } else {
                        // Incorrect
                        trapSound.start();
                        showDialog(FAIL_DIALOG_ID);
                    }
                }
            });

        notTrapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (!currentTrap.isTrap) {
                        // Correct
                        numCorrect++;
                        successSound.start();
                        showDialog(SUCCESS_DIALOG_ID);
                    } else {
                        // Incorrect
                        trapSound.start();
                        showDialog(FAIL_DIALOG_ID);
                    }
                }
            });

        trapSound = MediaPlayer.create(this, R.raw.itsatrap);
        successSound = MediaPlayer.create(this, R.raw.temple);

        prepareTraps();
        chooseNextTrap();
        statementTextView.setText(currentTrap.statement);
        updateScore();
    }

    private void chooseNextTrap()
    {
        boolean gameOver = false;
        if (traps.size() > 0) {
            // If user has lost so many that it's impossible to win
            if (currentRound * (numTrapsPlayed - numCorrect) > NUM_ROUNDS_TO_WIN * NUM_TRAPS_PER_ROUND) {
                gameOver = true;
            } else {
                if (numTrapsPlayed % NUM_TRAPS_PER_ROUND == 0) {
                    currentRound++;
                }
                if (currentRound <= NUM_ROUNDS) {
                    currentTrap = traps.remove(0);
                } else {
                    // Game over!
                    gameOver = true;
                }
            }
            Log.d("GameActivity", currentTrap.statement + " isTrap? " + currentTrap.isTrap);
        } else {
            Log.e("GameActivity", "no traps remaining");
            gameOver = true;
        }
        if (gameOver) {
            Log.d("GameActivity", "Game over!");
            showDialog(GAME_OVER_ID);
        }
    }

    private void updateScore()
    {
        scoreTextView.setText(getString(R.string.score, numCorrect, numTrapsPlayed));
        roundTextView.setText(getString(R.string.round, currentRound));
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        dialog.dismiss();
        numTrapsPlayed++;
        updateScore();

        chooseNextTrap();
        statementTextView.setText(currentTrap.statement);
    }

    @Override
    public Dialog onCreateDialog(int id, Bundle args)
    {
        if (id == FAIL_DIALOG_ID) {
            return new AlertDialog.Builder(this).setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.its_a_trap)
                    .setMessage(R.string.you_guessed_wrong)
                    .setNeutralButton(android.R.string.ok, this)
                    .create();
        } else if (id == SUCCESS_DIALOG_ID) {
            return new AlertDialog.Builder(this).setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.guess_correct)
                    .setNeutralButton(android.R.string.ok, this)
                    .create();
        } else if (id == GAME_OVER_ID) {
            return new AlertDialog.Builder(this).setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.game_over)
                    .setMessage(R.string.won_see_score)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                Intent i = new Intent(GameActivity.this, ScoreActivity.class);
                                i.putExtra("numTrapsPlayed", numTrapsPlayed);
                                i.putExtra("currentRound", currentRound);
                                i.putExtra("numCorrect", numCorrect);
                                startActivity(i);
                                finish();
                            }
                        })
                    .create();
        } else {
            return null;
        }
    }

    @Override
    public void onPrepareDialog(int id, Dialog dialog, Bundle args)
    {
        if (id == SUCCESS_DIALOG_ID) {

        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        trapSound.release();
        successSound.release();
    }

    private void prepareTraps()
    {
        // Load traps from XML and randomize them
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            InputStream xmlStream = getResources().openRawResource(R.raw.traps);
            TrapXMLHandler xmlHandler = new TrapXMLHandler();
            saxParser.parse(xmlStream, xmlHandler);

            traps = xmlHandler.getTraps();
            Collections.shuffle(traps);
        } catch (Exception e) {
            Log.e("GameActivity", e.getMessage(), e);
        }
    }
}
