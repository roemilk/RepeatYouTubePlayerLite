package com.venuskimblessing.tuberepeatfree.PlayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayListDbHelper extends SQLiteOpenHelper {
    private final String TAG = "PlayListDbHelper";

    public static final int DB_VERSION = 1;
    public static final String DBFILE_PLAYLIST = "playlist01.db";

    public PlayListDbHelper(Context context){
        super(context, DBFILE_PLAYLIST, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DB Create Success...");
        db.execSQL(PlayListDBCtrct.SQL_CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}