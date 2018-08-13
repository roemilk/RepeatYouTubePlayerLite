package com.venuskimblessing.youtuberepeatlite.PlayList;

public class PlayListDBCtrct {

    public PlayListDBCtrct() {
    }

    public static final String TBL_CONTACT = "PLAYLIST_T";
    public static final String COL_ID = "ID";
    public static final String COL_IMAGEURL = "IMGURL";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_DURATION = "DURATION";
    public static final String COL_VIDEOID = "VIDEOID";
    public static final String COL_STARTTIME = "STARTTIME";
    public static final String COL_ENDTIME = "ENDTIME";
    public static final String COL_REPEAT = "REPEAT";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT + " " + "(" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_IMAGEURL + " TEXT" + ", "+
            COL_TITLE + " TEXT" + ", " +
            COL_DURATION + " TEXT" + ", " +
            COL_VIDEOID + " TEXT" + ", " +
            COL_STARTTIME + " TEXT" + ", " +
            COL_ENDTIME + " TEXT" + ", " +
            COL_REPEAT + " TEXT" +
            ")";
    public static final String SQL_DROP_TBL = "drop table PLAYLIST_T";

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT;

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_CONTACT + " " +
            "(" + COL_IMAGEURL + ", " + COL_TITLE + ", " + COL_DURATION + ", " +  COL_VIDEOID + ", " +  COL_STARTTIME + ", " + COL_ENDTIME + ", " + COL_REPEAT + ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT;

    public static final String SQL_UPDATE_WHERE = "UPDATE " + TBL_CONTACT + " SET";

    public static final String SQL_DELETE_WHERE = "DELETE FROM " + TBL_CONTACT + " WHERE ID=";

    public static final String SQL_RESET_AUTOINCREMENT = "UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = " + "'" + TBL_CONTACT + "'";
}
