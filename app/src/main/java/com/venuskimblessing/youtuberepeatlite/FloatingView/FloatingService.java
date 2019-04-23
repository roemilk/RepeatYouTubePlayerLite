package com.venuskimblessing.youtuberepeatlite.FloatingView;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatlite.R;
import com.venuskimblessing.youtuberepeatlite.SearchActivity;

import java.util.ArrayList;

public class FloatingService extends Service {
    private final String TAG = "FloatingService";

    private float mTouchX, mTouchY;
    private int mViewX, mViewY;

    private WindowManager.LayoutParams windowViewLayoutParams;
    private WindowManager mWindowManager;
    private FloatingView floatingView;

    //PlayListData
    private PlayListData mPlayListData;
    private ArrayList<PlayListData> mPlayListDataArray;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFloatingWindow();
        int type = 0;
        try{
            type = intent.getIntExtra("type", 0);
        }catch(Exception e){
            Toast.makeText(this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            Intent exceptionIntent = new Intent(this, SearchActivity.class);
            exceptionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exceptionIntent);
        }
        switch (type) {
            case FloatingManager.TYPE_INTENT_DATA:
                Log.d(TAG, "TYPE_INTENT_DATA...");

                mPlayListData = (PlayListData)intent.getSerializableExtra("data");
                floatingView.setPlayData(mPlayListData);

                int repeatCount = intent.getIntExtra("repeatcount", 0);
                floatingView.setRepeatCount(repeatCount);
                break;
            case FloatingManager.TYPE_INTENT_LIST:
                Log.d(TAG, "TYPE_INTENT_LIST...");

                mPlayListDataArray = (ArrayList<PlayListData>) intent.getSerializableExtra("list");
                int index = intent.getIntExtra("index", 0);
                floatingView.setPlayListData(mPlayListDataArray, index);
                break;

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(floatingView);
            floatingView = null;
        }
    }

    private void initFloatingWindow() {
        floatingView = new FloatingView(this, mViewTouchListener);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        windowViewLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(floatingView, windowViewLayoutParams);
    }

    /**
     * Tourch Listener
     */
    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG, "onTouch...");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ACTION_DOWN...");
                    mTouchX = event.getRawX();
                    mTouchY = event.getRawY();
                    mViewX = windowViewLayoutParams.x;
                    mViewY = windowViewLayoutParams.y;
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "ACTION_UP...");
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ACTION_MOVE...");

                    int x = (int) (event.getRawX() - mTouchX);
                    int y = (int) (event.getRawY() - mTouchY);

                    windowViewLayoutParams.x = mViewX + x;
                    windowViewLayoutParams.y = mViewY + y;

                    mWindowManager.updateViewLayout(floatingView, windowViewLayoutParams);
                    break;
            }
            return false;
        }
    };
}
