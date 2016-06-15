package com.example.hongyu.myapplication;

import android.provider.BaseColumns;

/**
 * Created by hzhan72 on 2016/5/10.
 */
public class BaccaratDB {
    public BaccaratDB() {}

    // game: 一次出价，一小局
    public static abstract class BaccaraGameTBColumns implements BaseColumns {
        public static final String TABLE_NAME = "baccara_game";
        public static final String COLUMN_GAME_NUMBER = "game_number";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_RESULT = "result";
        public static final String COLUMN_GAIN = "gain";
        public static final String COLUMN_STAT_SHORT = "stat_short";
        public static final String COLUMN_STAT = "stat";
    }

    // set: 一条牌，一盘，一大局
    public static abstract class BaccaraSetTBColumns implements BaseColumns {
        public static final String TABLE_NAME = "baccara_set";
        public static final String COLUMN_SET_TABLE = "set_table";
        public static final String COLUMN_SET_NUMBER = "set_number";
        public static final String COLUMN_SET_DATE = "set_date";
        public static final String COLUMN_SET_TIME = "set_time";
        public static final String COLUMN_SET_RECORDER = "set_recorder";
        public static final String COLUMN_SET_GAIN = "set_gain";
        public static final String COLUMN_GAME_NUMBER = "game_number";
        public static final String COLUMN_GAME_TIME = "game_time";
    }

    // 玩家信息
    public static abstract class BaccaraPlayerBetTBColumns implements BaseColumns {
        public static final String TABLE_NAME = "baccara_player_bet";
        public static final String COLUMN_PLAYER_ID = "player_id";
        public static final String COLUMN_PLAYER_NAME = "player_name";
        public static final String COLUMN_AMOUNT = "amount";  //出价数量
        public static final String COLUMN_BET = "bet"; // 出价结果
    }

    // history: set历史数据
    public static abstract class BaccaraSetHistoryTBColumns implements BaseColumns {
        public static final String TABLE_NAME = "baccara_set_history";
        public static final String COLUMN_SET_TABLE = "set_table";
        public static final String COLUMN_SET_NUMBER = "set_number";
        public static final String COLUMN_SET_DATE = "set_date";
        public static final String COLUMN_SET_TIME = "set_time";
        public static final String COLUMN_SET_RECORDER = "set_recorder";
        public static final String COLUMN_SET_GAIN = "set_gain";
    }

    // history: game历史数据
    public static abstract class BaccaraGameHistoryTBColumns implements BaseColumns {
        public static final String TABLE_NAME = "baccara_game_history";
        public static final String COLUMN_SET_ID = "game_set_id";
        public static final String COLUMN_GAME_NUMBER = "game_number";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_RESULT = "result";
        public static final String COLUMN_GAIN = "gain";
        public static final String COLUMN_STAT_SHORT = "stat_short";
        public static final String COLUMN_STAT = "stat";
    }

    public static final String CREATE_BaccaraGameTB =
            "CREATE TABLE " + BaccaraGameTBColumns.TABLE_NAME + " (" +
                    BaccaraGameTBColumns._ID + " INTEGER PRIMARY KEY, " +
                    BaccaraGameTBColumns.COLUMN_GAME_NUMBER + " INTEGER, " +
                    BaccaraGameTBColumns.COLUMN_TIME + " TEXT, " +
                    BaccaraGameTBColumns.COLUMN_RESULT + " TEXT, " +
                    BaccaraGameTBColumns.COLUMN_GAIN + " INTEGER, " +
                    BaccaraGameTBColumns.COLUMN_STAT_SHORT + " TEXT, " +
                    BaccaraGameTBColumns.COLUMN_STAT + " TEXT)";

    public static final String CREATE_BaccaraSetTB =
            "CREATE TABLE " + BaccaraSetTBColumns.TABLE_NAME + " (" +
                    BaccaraSetTBColumns._ID + " INTEGER PRIMARY KEY, " +
                    BaccaraSetTBColumns.COLUMN_SET_TABLE + " INTEGER, " +
                    BaccaraSetTBColumns.COLUMN_SET_NUMBER + " INTEGER, " +
                    BaccaraSetTBColumns.COLUMN_SET_TIME + " TEXT, " +
                    BaccaraSetTBColumns.COLUMN_SET_DATE + " TEXT, " +
                    BaccaraSetTBColumns.COLUMN_SET_RECORDER + " TEXT, " +
                    BaccaraSetTBColumns.COLUMN_SET_GAIN + " INTEGER, " +
                    BaccaraSetTBColumns.COLUMN_GAME_NUMBER + " INTEGER, " +
                    BaccaraSetTBColumns.COLUMN_GAME_TIME + " TEXT)";

    public static final String CREATE_BaccaraPlayBetTB =
            "CREATE TABLE " + BaccaraPlayerBetTBColumns.TABLE_NAME + " (" +
                    BaccaraPlayerBetTBColumns._ID + " INTEGER PRIMARY KEY, " +
                    BaccaraPlayerBetTBColumns.COLUMN_PLAYER_ID + " INTEGER, " +
                    BaccaraPlayerBetTBColumns.COLUMN_PLAYER_NAME + " TEXT, " +
                    BaccaraPlayerBetTBColumns.COLUMN_AMOUNT + " TEXT, " +
                    BaccaraPlayerBetTBColumns.COLUMN_BET + " TEXT)";


    public static final String CREATE_BaccaraSetHistoryTB =
            "CREATE TABLE " + BaccaraSetHistoryTBColumns.TABLE_NAME + " (" +
                    BaccaraSetHistoryTBColumns._ID + " INTEGER PRIMARY KEY, " +
                    BaccaraSetHistoryTBColumns.COLUMN_SET_TABLE + " INTEGER, " +
                    BaccaraSetHistoryTBColumns.COLUMN_SET_NUMBER + " INTEGER, " +
                    BaccaraSetHistoryTBColumns.COLUMN_SET_DATE + " TEXT, " +
                    BaccaraSetHistoryTBColumns.COLUMN_SET_TIME + " TEXT, " +
                    BaccaraSetHistoryTBColumns.COLUMN_SET_RECORDER + " TEXT, " +
                    BaccaraSetHistoryTBColumns.COLUMN_SET_GAIN + " INTEGER)";

    public static final String CREATE_BaccaraGameHistoryTB =
            "CREATE TABLE " + BaccaraGameHistoryTBColumns.TABLE_NAME + " (" +
                    BaccaraGameHistoryTBColumns._ID + " INTEGER PRIMARY KEY, " +
                    BaccaraGameHistoryTBColumns.COLUMN_SET_ID + " INTEGER, " +
                    BaccaraGameHistoryTBColumns.COLUMN_GAME_NUMBER + " INTEGER, " +
                    BaccaraGameHistoryTBColumns.COLUMN_TIME + " TEXT, " +
                    BaccaraGameHistoryTBColumns.COLUMN_RESULT + " TEXT, " +
                    BaccaraGameHistoryTBColumns.COLUMN_GAIN + " INTEGER, " +
                    BaccaraGameHistoryTBColumns.COLUMN_STAT_SHORT + " TEXT, " +
                    BaccaraGameHistoryTBColumns.COLUMN_STAT + " TEXT)";
}
