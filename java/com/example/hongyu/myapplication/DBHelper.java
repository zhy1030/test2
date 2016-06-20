package com.example.hongyu.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hzhan72 on 2016/5/10.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Baccarat.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BaccaratDB.CREATE_BaccaraGameTB);
        db.execSQL(BaccaratDB.CREATE_BaccaraSetTB);
        db.execSQL(BaccaratDB.CREATE_BaccaraPlayBetTB);
        db.execSQL(BaccaratDB.CREATE_BaccaraSetHistoryTB);
        db.execSQL(BaccaratDB.CREATE_BaccaraGameHistoryTB);
        db.execSQL(BaccaratDB.CREATE_BaccaraPlayerHistoryTB);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}