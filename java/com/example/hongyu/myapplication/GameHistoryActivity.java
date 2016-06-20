package com.example.hongyu.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by hongyu on 16-6-7.
 */
public class GameHistoryActivity extends AppCompatActivity {
    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private Integer mSetID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        Intent intent=getIntent();
        intent.getExtras();
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        mSetID=bundle.getInt("setID");
        Log.d("hongyu", "setID is " + mSetID);
        // init db
        mDbHelper = new DBHelper(getApplicationContext());
        mDb = mDbHelper.getWritableDatabase();


        // load player data
        String[] playerProjection = {
                BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_NAME,
                BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_PLAYER_GAIN,
        };
        String playerOrder = BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_PLAYER_GAIN + " DESC";
        String playerSelection = BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_SET_ID + "=" + mSetID;
        Cursor playerCursor = mDb.query(
                BaccaratDB.BaccaraPlayerHistoryTBColumns.TABLE_NAME,
                playerProjection,
                playerSelection,
                null,
                null,
                null,
                playerOrder
        );
        if (playerCursor != null) {
            String player;
            Integer playerGain;
            TextView playerView;
            TextView gainView;
            Integer pos = 0;

            while (playerCursor.moveToNext()) {
                player = playerCursor.getString(playerCursor.getColumnIndex(BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_NAME));
                playerGain = playerCursor.getInt(playerCursor.getColumnIndex(BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_PLAYER_GAIN));
                if (pos < 11) {
                    playerView = (TextView) findViewById(R.id.tbr1c1 + pos);
                    gainView = (TextView) findViewById(R.id.tbr2c1 + pos);
                } else if (pos < 22) {
                    playerView = (TextView) findViewById(R.id.tbr3c1 + pos - 11);
                    gainView = (TextView) findViewById(R.id.tbr4c1 + pos - 11);
                } else {
                    // Todo: add more table
                    playerView = (TextView) findViewById(R.id.tbr3c1 + 10);
                    gainView = (TextView) findViewById(R.id.tbr4c1 + 10);
                }
                playerView.setText(player);
                gainView.setText(playerGain.toString());
                pos++;
            }
        }


        // load game history in this set
        String[] gameProjection = {
                BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAME_NUMBER,
                BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_TIME,
                BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_RESULT,
                BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_RESULT_DUI,
                BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAIN,
                BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_STAT_SHORT,
        };
        String gameOrder = BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAME_NUMBER + " ASC";
        String gameSelection = BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_SET_ID + "=" + mSetID;
        Cursor gameCursor = mDb.query(
                BaccaratDB.BaccaraGameHistoryTBColumns.TABLE_NAME,  // The table to query
                gameProjection,                               // The columns to return
                gameSelection,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                gameOrder                                 // The sort order
        );

        if (gameCursor != null) {
            Integer gameNo;
            String gameTime;
            String gameResult;
            String gameResultDui;
            Integer gameGain;
            String gameStat;
            if (gameCursor.getCount() == 0) {
                Log.d("hongyu", "game count is 0");
            }
            while (gameCursor.moveToNext()) {
                gameNo = gameCursor.getInt(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAME_NUMBER));
                gameTime = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_TIME));
                gameResult = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_RESULT));
                gameResultDui = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_RESULT_DUI));
                gameGain = gameCursor.getInt(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAIN));
                gameStat = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_STAT_SHORT));
                addResultRow(gameNo, gameTime, gameResult, gameResultDui, gameGain, gameStat);
            }
        }
        gameCursor.close();

        mDb.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    private void addResultRow(Integer _gameNo, String _gameTime, String _gameResult, String _gameResultDui, Integer _gameGain, String _gameStat) {
        TableRow tableRow = new TableRow(this);
        RelativeLayout.LayoutParams row_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(row_params);

        final TextView gameNo = new TextView(this);
        gameNo.setText(_gameNo.toString());
        gameNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        gameNo.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        gameNo.setGravity(Gravity.CENTER_HORIZONTAL);
        gameNo.setBackgroundResource(R.drawable.cell);
        tableRow.addView(gameNo);

        final TextView gameTime = new TextView(this);
        gameTime.setText(_gameTime);
        gameTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        gameTime.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        gameTime.setGravity(Gravity.CENTER_HORIZONTAL);
        gameTime.setBackgroundResource(R.drawable.cell);
        tableRow.addView(gameTime);

        final TextView gameResult = new TextView(this);
        gameResult.setText(_gameResult + " " +_gameResultDui);
        gameResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        gameResult.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        gameResult.setGravity(Gravity.CENTER_HORIZONTAL);
        gameResult.setBackgroundResource(R.drawable.cell);
        tableRow.addView(gameResult);

        final TextView gameGain = new TextView(this);
        gameGain.setText(_gameGain.toString());
        gameGain.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        gameGain.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        gameGain.setGravity(Gravity.CENTER_HORIZONTAL);
        gameGain.setBackgroundResource(R.drawable.cell);
        tableRow.addView(gameGain);

        final TextView gameStat = new TextView(this);
        gameStat.setText(_gameStat);
        gameStat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        gameStat.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        gameStat.setGravity(Gravity.NO_GRAVITY);
        gameStat.setBackgroundResource(R.drawable.cell);
        TextView refView = (TextView) findViewById(R.id.stat_width_ref);
        gameStat.setMaxWidth(refView.getWidth());
        tableRow.addView(gameStat);

        TableLayout recordTB = (TableLayout) findViewById(R.id.game_history_tb);
        recordTB.addView(tableRow, new TableLayout.LayoutParams(WC, MP));

        gameStat.post(new Runnable() {
            public void run() {
                Integer height = gameStat.getHeight();
                Log.d("hongyu", "height is " + height);
                gameGain.setHeight(height);
                gameResult.setHeight(height);
                gameTime.setHeight(height);
                gameNo.setHeight(height);
            }
        });
    }
}
