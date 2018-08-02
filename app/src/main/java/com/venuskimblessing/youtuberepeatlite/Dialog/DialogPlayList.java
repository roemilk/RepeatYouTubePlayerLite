package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.venuskimblessing.youtuberepeatlite.Adapter.PlayListRecyclerViewAdapter;
import com.venuskimblessing.youtuberepeatlite.Adapter.SearchDecoration;
import com.venuskimblessing.youtuberepeatlite.Interface.PlayListItemTouchHelperCallback;
import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListDataManager;
import com.venuskimblessing.youtuberepeatlite.R;

import java.util.ArrayList;

public class DialogPlayList extends Dialog implements View.OnClickListener, PlayListRecyclerViewAdapter.OnStartDragListener {
    private static final String TAG = "DialogPlayList";

    public interface OnClickDialogPlayListListener{
        public void onPlay(PlayListData data);
    }

    private Context mContext;
    private TextView mEmptyTextView;
    private Button mClosebutton;
    private RecyclerView mPlayListRecyclerView;
    private PlayListRecyclerViewAdapter mPlayListRecyclerViewAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<PlayListData> mList;
    private PlayListDataManager mPlayListDataManager = null;
    private OnClickDialogPlayListListener listener = null;

    public DialogPlayList(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogPlayList(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogPlayList(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_playlist);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mEmptyTextView = (TextView) findViewById(R.id.playlist_empty_textView);
        mPlayListRecyclerView = (RecyclerView) findViewById(R.id.playlist_recyclerview);
        mClosebutton = (Button) findViewById(R.id.playlist_close_button);
        mClosebutton.setOnClickListener(this);

        loadPlayListData();
        setAdapter();
    }

    public void setOnClickListener(OnClickDialogPlayListListener listener){
        if(listener != null){
            this.listener = listener;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playlist_close_button:
                dismiss();
                break;
        }
    }

    private void loadPlayListData(){
        mPlayListDataManager = new PlayListDataManager(mContext);
        mPlayListDataManager.initTable();
        mList = mPlayListDataManager.loadPlayList();

        if(mList == null || mList.size() == 0){
            mEmptyTextView.setVisibility(View.VISIBLE);
        }else{
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    private void setAdapter() {
        mPlayListRecyclerViewAdapter = new PlayListRecyclerViewAdapter(mContext);
        mPlayListRecyclerViewAdapter.setOnStartDragListener(this);
        mPlayListRecyclerViewAdapter.setOnClickPlayListItemListener(onClickPlayListItemListener);
        mPlayListRecyclerViewAdapter.setData(mList);
        mPlayListRecyclerView.setHasFixedSize(true);
        mPlayListRecyclerView.addItemDecoration(new SearchDecoration(mContext));
        mPlayListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        PlayListItemTouchHelperCallback mCallback = new PlayListItemTouchHelperCallback(mPlayListRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mPlayListRecyclerView);

        mPlayListRecyclerView.setAdapter(mPlayListRecyclerViewAdapter);
    }


    @Override
    public void onStartDrag(PlayListRecyclerViewAdapter.ItemViewHolder itemViewHolder) {
        mItemTouchHelper.startDrag(itemViewHolder);
    }

    private View.OnClickListener onClickPlayListItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.view_playlist_parent_lay:
                    PlayListData data = (PlayListData)v.getTag();
                    listener.onPlay(data);
                    break;
                case R.id.view_playlist_more_lay:
                    Toast.makeText(mContext, "플레이리스트 More 뷰 클릭 이벤트", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };
}
