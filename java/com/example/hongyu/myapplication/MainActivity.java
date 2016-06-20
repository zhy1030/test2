package com.example.hongyu.myapplication;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private DBHelper mDbHelper;
    private SQLiteDatabase mDb = null;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private Button mBtnSetEdit;
    private Button mBtnSetClean;
    private Button mBtnSetEnd;
    private Button mBtnSetHistory;
    private Button mBtnPlayerEdit;
    private Button mBtnGameEnd;
    private Context mContext;
    private View mContentView;

    private class PlayerBet {
        Integer table_number;
        String name;
        String bet;
        //String amount;
    }

    private ArrayList<PlayerBet> mPlayerBetList = new ArrayList();
    private String mGameResult;
    private String mGameResultDui;
    private String mGameStat;
    private String mGameStatShort;
    private Integer mGameGain;
    private String mGameTime;
    private Integer mGameNo = 1;

    private Integer mSetTable = 1;
    private Integer mSetNumber = 1;
    private String mSetDate;
    private String mSetTime;
    private String mSetRecorder = "";
    private Integer mSetGain = 0;

    private final String[] mDeviceList = {
      "c6fe903235b06716", // 虚拟机
      "115ee591b7e505ac", // Samsung tab E
      "8a9c713404bc3c2e", // Samsung tab E
      "1a2318349d2b2e53", // x16
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mContentView = findViewById(R.id.fullscreen_content);
        // read device id
        String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        boolean match = false;
        Log.d("hongyu", "device_id is " + device);
        for (int i = 0; i < mDeviceList.length; i++) {
            if (mDeviceList[i].equals(device)) {
                match = true;
            }
        }
        if (!match) {
            Toast.makeText(mContext, "软件未注册！", Toast.LENGTH_LONG).show();
            try {Thread.sleep(2000);} catch (Exception e) {}
            this.finish();
            return;
        }

        // init db
        mDbHelper = new DBHelper(getApplicationContext());
        mDb = mDbHelper.getWritableDatabase();

        // init buttons
        mBtnSetEdit = (Button) findViewById(R.id.set_edit);
        mBtnSetClean = (Button) findViewById(R.id.set_clean);
        mBtnSetEnd = (Button) findViewById(R.id.set_end);
        mBtnSetHistory = (Button) findViewById(R.id.set_history);
        mBtnPlayerEdit = (Button) findViewById(R.id.player_edit);
        mBtnGameEnd = (Button) findViewById(R.id.game_end);


        // init button listener.
        mBtnSetEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetEditDialog();
            }
        });
        mBtnSetClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetCleanDialog();

            }
        });
        mBtnSetEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetEndDialog();
            }
        });
        mBtnSetHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        mBtnPlayerEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerEditDialog();
            }
        });
        mBtnGameEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGameResult();
            }
        });

        // init bet amount click listener
        TextView textView;
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr3c1 + i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPlayerBetDialog(view);
                }
            });
        }
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr7c1 + i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPlayerBetDialog(view);
                }
            });
        }

        // init player name click listener
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr2c1 + i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPlayerEditDialog();
                }
            });
        }
        // init player name click listener
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr6c1 + i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPlayerEditDialog();
                }
            });
        }

        // init the spinners
        Spinner spinner;
        spinner =  (Spinner) findViewById(R.id.game_result);
        String[] gameResutArray = new String[]{
                "",
                "庄",
                "闲",
                "和",
                "庄6点"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, gameResutArray
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner =  (Spinner) findViewById(R.id.game_result_dui);
        String[] gameDuiResutArray = new String[]{
                "",
                "庄对",
                "闲对",
                "庄闲对"
        };
        ArrayAdapter<String> spinnerDuiArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, gameDuiResutArray
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerDuiArrayAdapter);

        loadCurrent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Delayed removal of status and navigation bar

        /*
        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        */
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCurrent();
        if (mDb != null)
            mDb.close();
    }

    // show set edit dialog
    private void showSetEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器

        LayoutInflater inflater = getLayoutInflater();
        final View mSetEditView = inflater.inflate(R.layout.set_edit_dialog,
                (ViewGroup) findViewById(R.id.set_edit_dialog));

        EditText tmpEditText;
        TextView tmpTextView;
        // set dlg table number
        tmpTextView = (TextView) findViewById(R.id.cur_set_table);
        tmpEditText = (EditText) mSetEditView.findViewById(R.id.cur_set_table_d);
        tmpEditText.setText(tmpTextView.getText());
        // set current set
        tmpTextView = (TextView) findViewById(R.id.cur_set_number);
        tmpEditText = (EditText) mSetEditView.findViewById(R.id.cur_set_number_d);
        tmpEditText.setText(tmpTextView.getText());
        // set current time
        // tmpTextView = (TextView) findViewById(R.id.cur_set_time);
        // tmpEditText = (EditText) mSetEditView.findViewById(R.id.cur_set_time_d);
        // tmpEditText.setText(tmpTextView.getText());
        // set current recorder
        tmpTextView = (TextView) findViewById(R.id.cur_set_recorder);
        tmpEditText = (EditText) mSetEditView.findViewById(R.id.cur_set_recorder_d);
        tmpEditText.setText(tmpTextView.getText());

        builder.setTitle("本局信息修改"); //设置标题
        builder.setView(mSetEditView);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText;
                TextView textView;

                // set table number
                editText = (EditText) mSetEditView.findViewById(R.id.cur_set_table_d);
                textView = (TextView) findViewById(R.id.cur_set_table);
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(mContext, "请输入桌号", Toast.LENGTH_SHORT).show();
                    return;
                }
                textView.setText(editText.getText());
                mSetTable = Integer.parseInt(editText.getText().toString());
                // set current set
                editText = (EditText) mSetEditView.findViewById(R.id.cur_set_number_d);
                textView = (TextView) findViewById(R.id.cur_set_number);
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(mContext, "请输入局数", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSetNumber = Integer.parseInt(editText.getText().toString());
                textView.setText(editText.getText());
                // set current time
                // editText = (EditText) mSetEditView.findViewById(R.id.cur_set_time_d);
                // textView = (TextView) findViewById(R.id.cur_set_time);
                // if (editText.getText().toString().equals("")) {
                //     Toast.makeText(mContext, "请输入时间", Toast.LENGTH_SHORT).show();
                //     return;
                // }
                textView.setText(editText.getText());
                // set current recorder
                editText = (EditText) mSetEditView.findViewById(R.id.cur_set_recorder_d);
                textView = (TextView) findViewById(R.id.cur_set_recorder);
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(mContext, "请输入记录人", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSetRecorder = editText.getText().toString();
                textView.setText(editText.getText());

                if (mGameNo == 1) {
                    Calendar c = Calendar.getInstance();
                    DecimalFormat df = new DecimalFormat("#00");
                    textView = (TextView) findViewById(R.id.game_time);
                    mGameTime = df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE));
                    textView.setText(mGameTime);
                }

                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // show the dialog
        builder.create().show();
    }

    private  void showSetCleanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认清空本局所有信息?"); //设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // clean all set data
                resetSet();
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    // show player edit dialog
    private void showPlayerEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器

        LayoutInflater inflater = getLayoutInflater();
        final View mPlayerEditView = inflater.inflate(R.layout.player_edit_dialog,
                (ViewGroup) findViewById(R.id.player_edit_dialog));

        EditText tmpEditText;
        TextView tmpTextView;
        // set dlg players
        for (int i = 0; i < 9; i++) {
            tmpTextView = (TextView) findViewById(R.id.tbr2c1 + i);
            tmpEditText = (EditText) mPlayerEditView.findViewById(R.id.tbr2c1 + i);
            tmpEditText.setText(tmpTextView.getText());
        }
        for (int i = 0; i < 9; i++) {
            tmpTextView = (TextView) findViewById(R.id.tbr6c1 + i);
            tmpEditText = (EditText) mPlayerEditView.findViewById(R.id.tbr6c1 + i);
            tmpEditText.setText(tmpTextView.getText());
        }

        builder.setTitle("玩家设置"); //设置标题
        builder.setView(mPlayerEditView);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText;
                TextView textView;
                // set players
                for (int i = 0; i < 9; i++) {
                    textView = (TextView) findViewById(R.id.tbr2c1 + i);
                    editText = (EditText) mPlayerEditView.findViewById(R.id.tbr2c1 + i);
                    textView.setText(editText.getText());
                }
                for (int i = 0; i < 9; i++) {
                    textView = (TextView) findViewById(R.id.tbr6c1 + i);
                    editText = (EditText) mPlayerEditView.findViewById(R.id.tbr6c1 + i);
                    textView.setText(editText.getText());
                }
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // show the dialog
        builder.create().show();
    }

    private void showPlayerBetDialog(final View view) {
        final TextView amountTextView = (TextView) view;
        Log.d("hongyu", "clicked, view content is " + amountTextView.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View mPlayerBetView = inflater.inflate(R.layout.player_bet_dialog,
                (ViewGroup) findViewById(R.id.player_bet_dialog));
        builder.setView(mPlayerBetView);

        String string = amountTextView.getText().toString();
        PlayerBetResult playerBetResult = new PlayerBetResult(string);

        final EditText editTextZhuang = (EditText) mPlayerBetView.findViewById(R.id.player_bet_zhuang);
        final EditText editTextXian = (EditText) mPlayerBetView.findViewById(R.id.player_bet_xian);
        final EditText editTextHe = (EditText) mPlayerBetView.findViewById(R.id.player_bet_he);
        final EditText editTextZhuangDui = (EditText) mPlayerBetView.findViewById(R.id.player_bet_zhuangdui);
        final EditText editTextXiandui = (EditText) mPlayerBetView.findViewById(R.id.player_bet_xiandui);
        final Button cleanBtn = (Button) mPlayerBetView.findViewById(R.id.clean_btn);

        cleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextZhuang.setText("");
                editTextXian.setText("");
                editTextHe.setText("");
                editTextZhuangDui.setText("");
                editTextXiandui.setText("");
            }
        });

        editTextZhuang.setText(playerBetResult.mZhuang.toString());
        editTextXian.setText(playerBetResult.mXian.toString());
        editTextHe.setText(playerBetResult.mHe.toString());
        editTextZhuangDui.setText(playerBetResult.mZhuangDui.toString());
        editTextXiandui.setText(playerBetResult.mXianDui.toString());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String resultString = "";
                String string;

                string = editTextZhuang.getText().toString();
                if (!string.equals("") && !string.equals("0")) {
                    resultString += "庄:" + string + ";";
                }
                string = editTextXian.getText().toString();
                if (!string.equals("") && !string.equals("0")) {
                    resultString += "闲:" + string + ";";
                }
                string = editTextHe.getText().toString();
                if (!string.equals("") && !string.equals("0")) {
                    resultString += "和:" + string + ";";
                }
                string = editTextZhuangDui.getText().toString();
                if (!string.equals("") && !string.equals("0")) {
                    resultString += "庄对:" + string + ";";
                }
                string = editTextXiandui.getText().toString();
                if (!string.equals("") && !string.equals("0")) {
                    resultString += "闲对:" + string + ";";
                }
                amountTextView.setText(resultString);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void showSetEndDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("游戏结束");
        builder.setMessage("本局盘面盈亏" + mSetGain + ", 点击“确认”按钮保存本局数据。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // clean all set data
                if (mGameNo < 2) {
                    Toast.makeText(mContext, "本局没有数据", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); //关闭dialog
                    return;
                }
                nextSet();
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // record game result
    private void saveGameResult() {
        TextView textView;
        Spinner spinner;

        spinner = (Spinner) findViewById(R.id.game_result);
        mGameResult = spinner.getSelectedItem().toString();
        if (mGameResult.equals("")) {
            Toast.makeText(mContext, "请输入结果", Toast.LENGTH_SHORT).show();
            return;
        }
        spinner = (Spinner) findViewById(R.id.game_result_dui);
        mGameResultDui = spinner.getSelectedItem().toString();

        updatePlayerBetList();
        // Log.d("hongyu", "Total player is " + mPlayerBetList.size());
        if (mPlayerBetList.size() == 0) {
            Toast.makeText(mContext, "没有玩家出价！", Toast.LENGTH_SHORT).show();
            return;
        }
        Iterator it = mPlayerBetList.iterator();
        String gameStat = "";
        String gameStatShort = "";
        while (it.hasNext()) {
            PlayerBet playerBet = (PlayerBet) it.next();
            Integer playerGain = 0;
            // Log.d("hongyu", "current player is " + playerBet.table_number);
            PlayerBetResult playerBetResult = new PlayerBetResult(playerBet.bet);

            switch (mGameResult) {
                case "庄":
                    if (playerBetResult.mZhuang != 0) {
                        playerGain += playerBetResult.mZhuang;
                    }
                    playerGain -= playerBetResult.mXian;
                    playerGain -= playerBetResult.mHe;
                    break;
                case "闲":
                    if (playerBetResult.mXian != 0) {
                        playerGain += playerBetResult.mXian;
                    }
                    playerGain -= playerBetResult.mZhuang;
                    playerGain -= playerBetResult.mHe;
                    break;
                case "和":
                    if (playerBetResult.mHe != 0) {
                        playerGain += playerBetResult.mHe*8;
                    }
                    playerGain -= playerBetResult.mZhuang;
                    playerGain -= playerBetResult.mXian;
                    break;
                case "庄6点":
                    if (playerBetResult.mZhuang != 0) {
                        playerGain += playerBetResult.mZhuang / 2;
                    }
                    playerGain -= playerBetResult.mXian;
                    playerGain -= playerBetResult.mHe;
                    break;
                default:
            }

            switch (mGameResultDui) {
                case "庄对":
                    if (playerBetResult.mZhuangDui != 0) {
                        playerGain += playerBetResult.mZhuangDui * 11;
                    }
                    playerGain -= playerBetResult.mXianDui;
                    break;
                case "闲对":
                    if (playerBetResult.mXianDui != 0) {
                        playerGain += playerBetResult.mXianDui * 11;
                    }
                    playerGain -= playerBetResult.mZhuangDui;
                    break;
                case "庄闲对":
                    if (playerBetResult.mZhuangDui != 0) {
                        playerGain += playerBetResult.mZhuangDui * 11;
                    }
                    if (playerBetResult.mXianDui != 0) {
                        playerGain += playerBetResult.mXianDui * 11;
                    }
                    break;
                default:
                    playerGain -= playerBetResult.mZhuangDui;
                    playerGain -= playerBetResult.mXianDui;
            }

            gameStatShort = gameStatShort + playerBet.name + ":" + playerGain + "; ";
            if (it.hasNext()) {
                gameStat = gameStat + "座位号:" + playerBet.table_number + ", 玩家:" + playerBet.name + ", 下注数量:" + playerBet.bet + ", 输赢: " + playerGain + ";\n";
            } else {
                gameStat = gameStat + "座位号:" + playerBet.table_number + ", 玩家:" + playerBet.name + ", 下注数量:" + playerBet.bet + ", 输赢: " + playerGain + ";";
            }
            mGameGain -= playerGain;
        }
        mGameStat = gameStat;
        mGameStatShort = gameStatShort;

        // setup dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        LayoutInflater inflater = getLayoutInflater();
        final View mGameEndView = inflater.inflate(R.layout.game_end_dialog,
                (ViewGroup) findViewById(R.id.game_end_dialog));
        textView = (TextView) mGameEndView.findViewById(R.id.game_result);
        textView.setText(mGameResult + " " + mGameResultDui);
        textView = (TextView) mGameEndView.findViewById(R.id.game_stat);
        textView.setText(mGameStat);
        textView = (TextView) mGameEndView.findViewById(R.id.game_gain);
        textView.setText(mGameGain.toString());

        builder.setTitle("确认结果并进入下一轮"); //设置标题
        builder.setView(mGameEndView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // put data into data base
                recordGameData();
                addResultRow(mGameNo, mGameTime, mGameResult, mGameResultDui, mGameGain, mGameStatShort);
                updateSetData();

                resetGame();
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // show the dialog
        builder.create().show();

    }

    // record the game data into database
    private void recordGameData() {
        ContentValues values = new ContentValues();
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER, mGameNo);
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_TIME, mGameTime);
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT, mGameResult);
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT_DUI, mGameResultDui);
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_GAIN, mGameGain);
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT_SHORT, mGameStatShort);
        values.put(BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT, mGameStat);
        // Insert the new row, returning the primary key value of the new row
        mDb.insert(
                BaccaratDB.BaccaraGameTBColumns.TABLE_NAME, null, values);
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
        gameResult.setText(_gameResult + " " + _gameResultDui);
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

        TableLayout recordTB = (TableLayout) findViewById(R.id.rcd_tb);
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

        final ScrollView scrollingView = (ScrollView) findViewById(R.id.result_scrollview);
        scrollingView.post(new Runnable() {
            public void run() {
                scrollingView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void cleanResultRows() {
        TableLayout recordTB = (TableLayout) findViewById(R.id.rcd_tb);
        int j = recordTB.getChildCount();
        if (j < 1)
            return;
        for(int i=j;i>0;i--){
            recordTB.removeView(recordTB.getChildAt(i));//必须从后面减去子元素
        }
    }

    // update set data
    private void updateSetData() {
        mSetGain += mGameGain;
        TextView textView = (TextView) findViewById(R.id.cur_set_gain);
        textView.setText(mSetGain.toString());
    }

    // set game time
    private void resetGame() {
        Calendar c = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("#00");
        TextView textView = (TextView) findViewById(R.id.game_time);
        mGameTime = df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE));
        textView.setText(mGameTime);
        mGameNo++;
        textView = (TextView) findViewById(R.id.game_number);
        textView.setText(mGameNo.toString());
        mGameResult = "";
        Spinner spinner = (Spinner) findViewById(R.id.game_result);
        spinner.setSelection(0);
        mGameResultDui = "";
        spinner = (Spinner) findViewById(R.id.game_result_dui);
        spinner.setSelection(0);
        mGameGain = 0;
        mGameStat = "";
        mGameStatShort = "";
    }

    private void resetSet() {
        TextView textView;
        EditText editText;
        Spinner spinner;
        // init set info
        textView = (TextView) findViewById(R.id.cur_set_table);
        textView.setText(mSetTable.toString());
        textView = (TextView) findViewById(R.id.cur_set_number);
        textView.setText(mSetNumber.toString());

        Calendar c = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("#00");
        mSetDate = c.get(Calendar.YEAR) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH);
        mSetTime = c.get(Calendar.YEAR) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + " " + df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE));
        textView = (TextView) findViewById(R.id.cur_set_time);
        textView.setText(mSetTime);
        textView = (TextView) findViewById(R.id.cur_set_recorder);
        textView.setText(mSetRecorder);
        textView = (TextView) findViewById(R.id.cur_set_gain);
        textView.setText(mSetGain.toString());

        // clean player table
        textView = (TextView) findViewById(R.id.game_time);
        mGameTime = df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE));
        textView.setText(mGameTime);
        mGameNo = 1;
        textView = (TextView) findViewById(R.id.game_number);
        textView.setText(mGameNo.toString());
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr2c1 + i);
            textView.setText("");
        }
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr6c1 + i);
            textView.setText("");
        }
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr3c1 + i);
            textView.setText("");
        }
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr7c1 + i);
            textView.setText("");
        }

        // clean game data base
        mDb.delete(BaccaratDB.BaccaraGameTBColumns.TABLE_NAME, null, null);
        cleanResultRows();
    }

    private void recordSetData() {

        ContentValues values = new ContentValues();
        values.put(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_TABLE, mSetTable);
        values.put(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_NUMBER, mSetNumber);
        values.put(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_DATE, mSetDate);
        values.put(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_TIME, mSetTime);
        values.put(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_RECORDER, mSetRecorder);
        values.put(BaccaratDB.BaccaraSetHistoryTBColumns.COLUMN_SET_GAIN, mSetGain);
        //Log.d("hongyu", "recorder is " + mSetRecorder);
        // Insert the new row.
        mDb.insert(BaccaratDB.BaccaraSetHistoryTBColumns.TABLE_NAME, null, values);
        Cursor cursor = mDb.rawQuery("select last_insert_rowid() from baccara_set_history",null);
        Integer setID = 0;
        if(cursor.moveToFirst()) {
            setID = cursor.getInt(0);
            //Log.d("hongyu", "setID is " + setID);
        } else {
            Log.e("hongyu", "insert failed!");
            Toast.makeText(mContext, "插入失败！", Toast.LENGTH_LONG).show();
            return;
        }

        // record each game data;

        // load game history in this set
        String[] gameProjection = {
                BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_TIME,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_GAIN,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT_SHORT,
        };
        String gameOrder = BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER + " ASC";
        Cursor gameCursor = mDb.query(
                BaccaratDB.BaccaraGameTBColumns.TABLE_NAME,  // The table to query
                gameProjection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                gameOrder                                 // The sort order
        );

        if (gameCursor != null) {
            Integer gameNo;
            String gameTime;
            String gameResult;
            Integer gameGain;
            String gameStat;
            String gameStatShort;
            if (gameCursor.getCount() == 0) {
                Log.d("hongyu", "game count is 0");
                return;
            }

            // calculate player gain in the set
            Map<String, Integer> playerSetGain = new HashMap<String, Integer>();

            while (gameCursor.moveToNext()) {
                gameNo = gameCursor.getInt(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER));
                gameTime = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_TIME));
                gameResult = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT));
                gameGain = gameCursor.getInt(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_GAIN));
                gameStat = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT));
                gameStatShort = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT_SHORT));

                ContentValues gameValues = new ContentValues();
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_SET_ID, setID);
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAME_NUMBER, gameNo);
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_TIME, gameTime);
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_RESULT, gameResult);
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_GAIN, gameGain);
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_STAT, gameStat);
                gameValues.put(BaccaratDB.BaccaraGameHistoryTBColumns.COLUMN_STAT_SHORT, gameStatShort);
                // Insert the new row, returning the primary key value of the new row
                mDb.insert(BaccaratDB.BaccaraGameHistoryTBColumns.TABLE_NAME, null, gameValues);

                // parse the data
                String[] firstSplit = gameStatShort.replace(" ", "").split(";");
                for (int i = 0; i < firstSplit.length; i++) {
                    Log.d("hongyu", "split i " + i + " is " + firstSplit[i]);
                    String[] sencondSplit = firstSplit[i].split(":");
                    if (playerSetGain.containsKey(sencondSplit[0])) {
                        playerSetGain.put(sencondSplit[0], playerSetGain.get(sencondSplit[0]) + Integer.parseInt(sencondSplit[1]));
                    } else {
                        playerSetGain.put(sencondSplit[0], Integer.parseInt(sencondSplit[1]));
                    }
                }
            }

            // record the player gain
            String player;
            Integer playerGain;
            Iterator iterator = playerSetGain.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                player = (String) entry.getKey();
                playerGain = (Integer) entry.getValue();

                Log.d("hongyu", "player gain parsed " + player + ":" + playerGain);
                // insert into database
                ContentValues gameValues = new ContentValues();
                gameValues.put(BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_SET_ID, setID);
                gameValues.put(BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_NAME, player);
                gameValues.put(BaccaratDB.BaccaraPlayerHistoryTBColumns.COLUMN_PLAYER_GAIN, playerGain);
                mDb.insert(BaccaratDB.BaccaraPlayerHistoryTBColumns.TABLE_NAME, null, gameValues);
            }
        }
        gameCursor.close();
    }

    private void nextSet() {
        // 保存数据
        recordSetData();


        TextView textView;
        EditText editText;
        Spinner spinner;
        // init set info
        mSetNumber++;
        textView = (TextView) findViewById(R.id.cur_set_number);
        textView.setText(mSetNumber.toString());
        Calendar c = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("#00");
        mSetDate = c.get(Calendar.YEAR) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH);
        mSetTime = c.get(Calendar.YEAR) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + " " + df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE));
        textView = (TextView) findViewById(R.id.cur_set_time);
        textView.setText(mSetTime);
        mSetGain = 0;
        textView = (TextView) findViewById(R.id.cur_set_gain);
        textView.setText(mSetGain.toString());
        // clean player table
        textView = (TextView) findViewById(R.id.game_time);
        mGameTime = df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE));
        textView.setText(mGameTime);
        mGameNo = 1;
        textView = (TextView) findViewById(R.id.game_number);
        textView.setText(mGameNo.toString());
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr3c1 + i);
            textView.setText("");
        }
        for (int i = 0; i < 9; i++) {
            textView = (TextView) findViewById(R.id.tbr7c1 + i);
            textView.setText("");
        }
        // clean game data base
        mDb.delete(BaccaratDB.BaccaraGameTBColumns.TABLE_NAME, null, null);
        cleanResultRows();
    }

    private void saveCurrent() {
        TextView textView;
        // backup set and game information
        textView = (TextView) findViewById(R.id.cur_set_table);
        mSetTable = Integer.parseInt(textView.getText().toString());
        textView = (TextView) findViewById(R.id.cur_set_number);
        mSetNumber = Integer.parseInt(textView.getText().toString());
        textView = (TextView) findViewById(R.id.cur_set_time);
        mSetTime = textView.getText().toString();
        textView = (TextView) findViewById(R.id.cur_set_recorder);
        mSetRecorder = textView.getText().toString();
        textView = (TextView) findViewById(R.id.cur_set_gain);
        mSetGain = Integer.parseInt(textView.getText().toString());
        textView = (TextView) findViewById(R.id.game_number);
        mGameNo = Integer.parseInt(textView.getText().toString());
        textView = (TextView) findViewById(R.id.game_time);
        mGameTime = textView.getText().toString();

        ContentValues gameValues = new ContentValues();
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TABLE, mSetTable);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_NUMBER, mSetNumber);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TIME, mSetDate);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TIME, mSetTime);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_RECORDER, mSetRecorder);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_GAIN, mSetGain);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_GAME_NUMBER, mGameNo);
        gameValues.put(BaccaratDB.BaccaraSetTBColumns.COLUMN_GAME_TIME, mGameTime);
        mDb.delete(BaccaratDB.BaccaraSetTBColumns.TABLE_NAME, null, null);
        mDb.insert(BaccaratDB.BaccaraSetTBColumns.TABLE_NAME, null, gameValues);

        // save players
        updatePlayerBetList();
        // Log.d("hongyu", "Total player is " + mPlayerBetList.size());
        Iterator it = mPlayerBetList.iterator();
        mDb.delete(BaccaratDB.BaccaraPlayerBetTBColumns.TABLE_NAME, null, null);
        while (it.hasNext()) {
            PlayerBet playerBet = (PlayerBet) it.next();
            ContentValues values = new ContentValues();
            values.put(BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_ID, playerBet.table_number);
            values.put(BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_NAME, playerBet.name);
            values.put(BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_BET, playerBet.bet);
            mDb.insert(BaccaratDB.BaccaraPlayerBetTBColumns.TABLE_NAME, null, values);
        }
        // backup game history, already backuped in recordGameData;
    }

    private void loadCurrent() {
        TextView textView;
        // load set and game info
        String[] setProjection = {
                BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TABLE,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_NUMBER,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_DATE,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TIME,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_RECORDER,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_GAIN,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_GAME_NUMBER,
                BaccaratDB.BaccaraSetTBColumns.COLUMN_GAME_TIME,
        };
        Cursor set_c = null;
        try {
            set_c = mDb.query(
                    BaccaratDB.BaccaraSetTBColumns.TABLE_NAME,  // The table to query
                    setProjection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );
        } catch (Exception e) {
            return;
        }

        if (set_c == null || set_c.getCount() == 0) {
            resetSet();
            return;
        }
        set_c.moveToNext();
        mSetTable = set_c.getInt(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TABLE));
        textView = (TextView) findViewById(R.id.cur_set_table);
        textView.setText(mSetTable.toString());
        mSetNumber = set_c.getInt(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_NUMBER));
        textView = (TextView) findViewById(R.id.cur_set_number);
        textView.setText(mSetNumber.toString());
        mSetDate = set_c.getString(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_DATE));
        mSetTime = set_c.getString(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_TIME));
        textView = (TextView) findViewById(R.id.cur_set_time);
        textView.setText(mSetTime);
        mSetRecorder = set_c.getString(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_RECORDER));
        textView = (TextView) findViewById(R.id.cur_set_recorder);
        textView.setText(mSetRecorder);
        mSetGain = set_c.getInt(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_SET_GAIN));
        textView = (TextView) findViewById(R.id.cur_set_gain);
        textView.setText(mSetGain.toString());
        mGameNo = set_c.getInt(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_GAME_NUMBER));
        textView = (TextView) findViewById(R.id.game_number);
        textView.setText(mGameNo.toString());
        mGameTime = set_c.getString(set_c.getColumnIndex(BaccaratDB.BaccaraSetTBColumns.COLUMN_GAME_TIME));
        textView = (TextView) findViewById(R.id.game_time);
        textView.setText(mGameTime);
        set_c.close();

        // load players
        String[] projection = {
                BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_ID,
                BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_NAME,
                BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_BET,
        };
        String sortOrder =
                BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_ID + " ASC";

        Cursor c = mDb.query(
                BaccaratDB.BaccaraPlayerBetTBColumns.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        Integer playerId;
        String name;
        String bet;
        if (c != null) {
            while (c.moveToNext()) {
                playerId = c.getInt(c.getColumnIndex(BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_ID));
                name = c.getString(c.getColumnIndex(BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_PLAYER_NAME));
                bet = c.getString(c.getColumnIndex(BaccaratDB.BaccaraPlayerBetTBColumns.COLUMN_BET));

                TextView tmpTextView;
                EditText tmpEditText;
                //Log.d("hongyu", "player is " + playerId);
                playerId--;
                if (playerId < 9) {
                    tmpTextView = (TextView) findViewById(R.id.tbr2c1 + playerId);
                    tmpTextView.setText(name);
                    tmpTextView = (TextView) findViewById(R.id.tbr3c1 + playerId);
                    tmpTextView.setText(bet);
                } else {
                    playerId -= 9;
                    tmpTextView = (TextView) findViewById(R.id.tbr6c1 + playerId);
                    tmpTextView.setText(name);
                    tmpTextView = (TextView) findViewById(R.id.tbr7c1 + playerId);
                    tmpTextView.setText(bet);
                }
            }
        } else
            resetSet();
        c.close();

        // load game history in this set
        String[] gameProjection = {
                BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_TIME,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT_DUI,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_GAIN,
                BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT_SHORT,
        };
        String gameOrder = BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER + " ASC";
        Cursor gameCursor = mDb.query(
                BaccaratDB.BaccaraGameTBColumns.TABLE_NAME,  // The table to query
                gameProjection,                               // The columns to return
                null,                                // The columns for the WHERE clause
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
                Log.e("hongyu", "game count is 0");
                return;
            }
            while (gameCursor.moveToNext()) {
                gameNo = gameCursor.getInt(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_GAME_NUMBER));
                gameTime = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_TIME));
                gameResult = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT));
                gameResultDui = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_RESULT_DUI));
                gameGain = gameCursor.getInt(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_GAIN));
                gameStat = gameCursor.getString(gameCursor.getColumnIndex(BaccaratDB.BaccaraGameTBColumns.COLUMN_STAT_SHORT));
                addResultRow(gameNo, gameTime, gameResult, gameResultDui, gameGain, gameStat);
            }
        }
        gameCursor.close();

        // 刷新game历史标题
        textView = (TextView) findViewById(R.id.pankoushu);
        textView.setText("盘口数");
        textView = (TextView) findViewById(R.id.shijian);
        textView.setText("时间");
        textView = (TextView) findViewById(R.id.jieguo);
        textView.setText("结果");
        textView = (TextView) findViewById(R.id.yingkui);
        textView.setText("盈亏");
        textView = (TextView) findViewById(R.id.jilu);
        textView.setText("记录");
    }

    private void updatePlayerBetList() {
        TextView tableNumberView;
        TextView nameView;
        TextView betView;

        mPlayerBetList.clear();
        mGameGain = 0;

        for (int i = 0; i < 9; i++) {
            tableNumberView = (TextView) findViewById(R.id.tbr1c1 + i);
            nameView = (TextView) findViewById(R.id.tbr2c1 + i);
            betView = (TextView) findViewById(R.id.tbr3c1 + i);
            if (nameView.getText().toString().equals("")) {
                continue;
            }
            PlayerBet playerBet = new PlayerBet();
            playerBet.table_number = Integer.valueOf(tableNumberView.getText().toString()).intValue();
            playerBet.name = nameView.getText().toString();
            playerBet.bet = betView.getText().toString();
            mPlayerBetList.add(playerBet);
        }

        for (int i = 0; i < 9; i++) {
            tableNumberView = (TextView) findViewById(R.id.tbr5c1 + i);
            nameView = (TextView) findViewById(R.id.tbr6c1 + i);
            betView = (TextView) findViewById(R.id.tbr7c1 + i);
            if (nameView.getText().toString().equals("")) {
                continue;
            }
            PlayerBet playerBet = new PlayerBet();
            Log.d("hongyu", "error i " + i+tableNumberView.getText().toString());
            playerBet.table_number = Integer.parseInt(tableNumberView.getText().toString());
            playerBet.name = nameView.getText().toString();
            playerBet.bet = betView.getText().toString();
            mPlayerBetList.add(playerBet);
        }
    }
}