package com.grooveshark.determinetrap;

import com.grooveshark.determinetrap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;


public class GameActivity extends Activity
{
    private static final int TRAP_DIALOG_ID = 0;
    private static final int SUCCESS_DIALOG_ID = 1;

    private Button startButton;
    private List<Trap> traps;
    private Trap currentTrap;
    private MediaPlayer trapSound;
    private MediaPlayer successSound;
    private int currentRound;
    private int numCorrect;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        trapSound = MediaPlayer.create(this, R.raw.itsatrap);
        successSound = MediaPlayer.create(this, R.raw.temple);

        currentRound = 1;
        numCorrect = 0;

        prepareTraps();
        chooseNextTrap();
    }

    private void chooseNextTrap()
    {
        currentTrap = traps.remove(0);
        Log.d("GameActivity", currentTrap.statement + " isTrap? " + currentTrap.isTrap);
    }

    @Override
    public Dialog onCreateDialog(int id, Bundle args)
    {
        if (id == TRAP_DIALOG_ID) {
            return new AlertDialog.Builder(this).setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.its_a_trap)
                    .setMessage(R.string.you_guessed_wrong)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                    .create();
        } else if (id == SUCCESS_DIALOG_ID) {
            return new AlertDialog.Builder(this).setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.guess_correct)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
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
