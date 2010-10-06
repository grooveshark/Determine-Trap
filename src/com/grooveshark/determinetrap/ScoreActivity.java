package com.grooveshark.determinetrap;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ScoreActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        Intent i = getIntent();
        int numTrapsPlayed = i.getIntExtra("numTrapsPlayed", 0);
        int currentRound = i.getIntExtra("currentRound", 0);
        int numCorrect = i.getIntExtra("numCorrect", 0);

        boolean won = numCorrect > GameActivity.NUM_ROUNDS_TO_WIN * GameActivity.NUM_TRAPS_PER_ROUND;
        Log.d("ScoreApplication", "user won? " + won);

        SharedPreferences prefs = getSharedPreferences(TrapApplication.PREFS_NAME,
                Context.MODE_PRIVATE);
        int numGamesWon = prefs.getInt(TrapApplication.PREFS_NUM_GAMES_WON, 0);
        if (won) {
            numGamesWon++;
        }
        int numGamesPlayed = prefs.getInt(TrapApplication.PREFS_NUM_GAMES_PLAYED, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TrapApplication.PREFS_NUM_GAMES_WON, numGamesWon);
        editor.putInt(TrapApplication.PREFS_NUM_GAMES_PLAYED, ++numGamesPlayed);
        editor.commit();
    }
}
