package com.grooveshark.determinetrap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

public class StartActivity extends Activity
{
    private TextView gamesPlayed;
    private TextView gamesWon;
    private Button startButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        gamesPlayed = (TextView) findViewById(R.id.games_played);
        gamesWon = (TextView) findViewById(R.id.games_won);
        startButton = (Button) findViewById(R.id.start_button);

        showStats();

        startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(StartActivity.this, ScoreActivity.class);
                    startActivity(i);
                    finish();
                }
            });
    }

    private void showStats()
    {
        SharedPreferences prefs = getSharedPreferences(TrapApplication.PREFS_NAME,
                Context.MODE_PRIVATE);

        gamesWon.setText(getString(R.string.games_won,
                prefs.getInt(TrapApplication.PREFS_NUM_GAMES_WON, 0)));

        gamesPlayed.setText(getString(R.string.games_played,
                prefs.getInt(TrapApplication.PREFS_NUM_GAMES_PLAYED, 0)));
    }
}
