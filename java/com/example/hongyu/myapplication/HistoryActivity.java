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
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by hongyu on 16-6-6.
 */
public class HistoryActivity extends AppCompatActivity {

    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // init db
        mDbHelper = new DBHelper(getApplicationContext());
        mDb = mDbHelper.getWritableDatabase();



        // load set history in this set
        String[] setHistoryProjection = {
                BaccaratDB.BaccaraSetHistoryTBColumns._ID,
                BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_TABLE,
                BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_NUMBER,
                BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_TIME,
                BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_RECORDER,
                BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_GAIN,
        };
        String setHistoyOrder = BaccaratDB.BaccaraSetHistoryTBColumns._ID + " DESC";
        String setHistorySelection = BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_DATE + "";
        Cursor setHistoryCursor = mDb.query(
                BaccaratDB.BaccaraSetHistoryTBColumns.TABLE_NAME,  // The table to query
                setHistoryProjection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                setHistoyOrder                                 // The sort order
        );

        Integer maxItems = 100;
        if (setHistoryCursor != null) {
            Integer setID;
            Integer setTable;
            Integer setNumber;
            String setTime;
            String setRecorder;
            Integer setGain;
            if (setHistoryCursor.getCount() == 0) {
                Log.d("hongyu", "game count is 0");
                return;
            }
            while (setHistoryCursor.moveToNext() && maxItems-- > 0) {
                setID = setHistoryCursor.getInt(setHistoryCursor.getColumnIndex(BaccaratDB.BaccaraSetHistoryTBColumns._ID));
                setTable = setHistoryCursor.getInt(setHistoryCursor.getColumnIndex(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_TABLE));
                setNumber = setHistoryCursor.getInt(setHistoryCursor.getColumnIndex(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_NUMBER));
                setTime = setHistoryCursor.getString(setHistoryCursor.getColumnIndex(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_TIME));
                setRecorder = setHistoryCursor.getString(setHistoryCursor.getColumnIndex(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_RECORDER));
                setGain = setHistoryCursor.getInt(setHistoryCursor.getColumnIndex(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_GAIN));
                addHistoryRow(setID, setTable, setNumber, setTime, setRecorder, setGain);
            }
        }
        setHistoryCursor.close();
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
    }


    private void addHistoryRow(final Integer _setID, Integer _setTable, Integer _setNumber, String _setTime, String _setRecorder, Integer _setGain) {
        TableRow tableRow = new TableRow(this);
        RelativeLayout.LayoutParams row_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(row_params);

        TextView setTable = new TextView(this);
        setTable.setText(_setTable.toString());
        setTable.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setTable.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        setTable.setGravity(Gravity.CENTER);
        setTable.setBackgroundResource(R.drawable.cell);
        tableRow.addView(setTable);

        TextView setNumber = new TextView(this);
        setNumber.setText(_setNumber.toString());
        setNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setNumber.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        setNumber.setGravity(Gravity.CENTER);
        setNumber.setBackgroundResource(R.drawable.cell);
        tableRow.addView(setNumber);

        TextView setTime = new TextView(this);
        setTime.setText(_setTime);
        setTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setTime.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        setTime.setGravity(Gravity.CENTER);
        setTime.setBackgroundResource(R.drawable.cell);
        tableRow.addView(setTime);

        TextView setRecorder = new TextView(this);
        setRecorder.setText(_setRecorder);
        setRecorder.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setRecorder.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        setRecorder.setGravity(Gravity.CENTER);
        setRecorder.setBackgroundResource(R.drawable.cell);
        tableRow.addView(setRecorder);

        TextView setGain = new TextView(this);
        setGain.setText(_setGain.toString());
        setGain.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setGain.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        setGain.setGravity(Gravity.CENTER);
        setGain.setBackgroundResource(R.drawable.cell);
        tableRow.addView(setGain);

        TextView detail = new TextView(this);
        detail.setText("详情");
        detail.setClickable(true);
        detail.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          Log.d("hongyu", "clicked " + _setID.toString());
                                          Intent intent = new Intent();
                                          intent.setClass(HistoryActivity.this, GameHistoryActivity.class);
                                          intent.putExtra("setID", _setID);
                                          startActivity(intent);
                                      }
                                  });
        detail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        detail.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
        detail.setGravity(Gravity.CENTER);
        detail.setBackgroundResource(R.drawable.cell);
        tableRow.addView(detail);


        TableLayout historySet = (TableLayout) findViewById(R.id.history_set);
        historySet.addView(tableRow, new TableLayout.LayoutParams(WC, MP));
    }
}
