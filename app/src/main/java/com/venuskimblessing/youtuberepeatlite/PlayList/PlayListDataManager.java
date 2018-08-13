package com.venuskimblessing.youtuberepeatlite.PlayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * 플레이리스트를 관리하는 클래스
 */
public class PlayListDataManager {
    private static final String TAG = "PlayListDataManager";
    private Context mContext;
    private PlayListDbHelper mPlayListDbHelper = null;

    public PlayListDataManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * DB를 초기화하고 테이블을 생성한다.
     */
    public void initTable(){
        mPlayListDbHelper = new PlayListDbHelper(mContext);
    }

    /**
     * 테이블의 데이터를 불러온다.
     */
    public ArrayList<PlayListData> loadPlayList(){
        SQLiteDatabase db = mPlayListDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(PlayListDBCtrct.SQL_SELECT, null);

        ArrayList<PlayListData> list = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String img_url = cursor.getString(1);
            String title = cursor.getString(2);
            String duration = cursor.getString(3);
            String videoid = cursor.getString(4);
            String startTime = cursor.getString(5);
            String endTime = cursor.getString(6);
            String repeat = cursor.getString(7);

            Log.d(TAG, "id : " + id + "  img_url : " + img_url + "  title : " + title + "  duration : " + duration + " videoId : " + videoid + " startTime : " + startTime + " endTime : " + endTime + " repeat : " + repeat);

            //Data Mapping
            PlayListData data = new PlayListData();
            data.setId(id);
            data.setImg_url(img_url);
            data.setTitle(title);
            data.setDuration(duration);
            data.setVideoId(videoid);
            data.setStartTime(startTime);
            data.setEndTime(endTime);
            data.setRepeat(repeat);
            list.add(data);
        }
        return list;
    }

//    /**
//     * 리스트의 모든 데이터를 insert한다.
//     * @param list
//     */
//    public void insertAllList(ArrayList<PlayListData> list){
//        deleteAll();
//        initAutoIncrement();
//        for(PlayListData data : list){
//            insert(data.getImg_url(), data.getTitle(), data.getDuration(), data.getVideoId(), data.getStartTime(), data.getEndTime());
//        }
//    }
    /**
     * 테이블에 데이터를 삽입한다.
     * @param imgUrl
     * @param title
     * @param duration
     * @param videoId
     */
    public void insert(String imgUrl, String title, String duration, String videoId, String startTime, String endTime, String repeat){
        title = title.replace("\'", "\''").replace("\"", "\\\"");
        SQLiteDatabase db = mPlayListDbHelper.getWritableDatabase();
        String sqlInsert = PlayListDBCtrct.SQL_INSERT +
                "(" +
                "'" + imgUrl + "', "+
                "'" + title + "', " +
                "'" + duration + "', "+
                "'" + videoId + "', "+
                "'" + startTime + "', "+
                "'" + endTime + "', "+
                "'" + repeat + "')";

        db.execSQL(sqlInsert);
    }

    public void updateWhere(int id, String imageUrl, String title, String duration, String videoId, String startTime, String endTime, String repeat){
        SQLiteDatabase db = mPlayListDbHelper.getWritableDatabase();
        String sqlUpdateWhere = PlayListDBCtrct.SQL_UPDATE_WHERE + " " + PlayListDBCtrct.COL_IMAGEURL + "=" +"'" + imageUrl + "'," +
                " " + PlayListDBCtrct.COL_TITLE + "=" + "'" + title + "'," +
                " " + PlayListDBCtrct.COL_DURATION + "=" + "'" + duration + "'," +
                " " + PlayListDBCtrct.COL_VIDEOID + "=" + "'" + videoId + "'," +
                " " + PlayListDBCtrct.COL_STARTTIME + "=" + "'" + startTime + "'," +
                " " + PlayListDBCtrct.COL_ENDTIME + "=" + "'" + endTime + "'," +
                " " + PlayListDBCtrct.COL_REPEAT + "=" + "'" + repeat + "'" +
                " WHERE " + PlayListDBCtrct.COL_ID  + "=" + id;

        Log.d(TAG, "sqlUpdateWhere : " + sqlUpdateWhere);
        db.execSQL(sqlUpdateWhere);
    }

    /**
     * 특정 레코드를 삭제한다.
     */
    public void deleteWhere(int id){
        SQLiteDatabase db = mPlayListDbHelper.getWritableDatabase();
        String sqlDeleteWhere = PlayListDBCtrct.SQL_DELETE_WHERE + id;
        db.execSQL(sqlDeleteWhere);
    }

    /**
     * 테이블의 모든 데이터를 삭제한다.
     */
    public void deleteAll(){
        SQLiteDatabase db = mPlayListDbHelper.getWritableDatabase();
        db.execSQL(PlayListDBCtrct.SQL_DELETE);
    }

    /**
     * 테이블을 제거한다.
     */
    public void dropTable(){
        SQLiteDatabase db = mPlayListDbHelper.getWritableDatabase();
        db.execSQL(PlayListDBCtrct.SQL_DROP_TBL);
    }

    /**
     * AutoIncrement를 0으로 초기화한다.
     */
    public void initAutoIncrement(){
        SQLiteDatabase db = mPlayListDbHelper.getWritableDatabase();
        db.execSQL(PlayListDBCtrct.SQL_RESET_AUTOINCREMENT);
    }

    public void testInsertDb(){
        for(int i=0; i<100; i++){
            String imgUrl = "url" + i;
            String title = "title" + i;
            String duration = "duration" + i;
            String videoId = "videoId" + i;

//            insert(imgUrl, title, duration, videoId);
        }
    }
}
