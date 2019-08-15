package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.zagum.switchicon.SwitchIconView;
import com.suke.widget.SwitchButton;
import com.venuskimblessing.tuberepeatfree.Adapter.PlayListRecyclerViewAdapter;
import com.venuskimblessing.tuberepeatfree.Adapter.SearchDecoration;
import com.venuskimblessing.tuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.tuberepeatfree.DynamicLink.DynamicLinkManager;
import com.venuskimblessing.tuberepeatfree.Interface.PlayListItemTouchHelperCallback;
import com.venuskimblessing.tuberepeatfree.PlayList.PlayListData;
import com.venuskimblessing.tuberepeatfree.PlayList.PlayListDataManager;
import com.venuskimblessing.tuberepeatfree.Player.PlayerActivity;
import com.venuskimblessing.tuberepeatfree.R;
import com.venuskimblessing.tuberepeatfree.Utils.SharedPreferencesUtils;

import java.util.ArrayList;

public class DialogPlayList extends Dialog implements View.OnClickListener, PlayListRecyclerViewAdapter.OnStartDragListener {
    private static final String TAG = "DialogPlayList";

    public interface OnClickDialogPlayListListener{
        public void onPlay(PlayListData data);
    }

    public interface OnClickDialogControllerListener{
        public void play(View v);
    }

    private Activity mActivity;
    private TextView mEmptyTextView;
    private Button mClosebutton,mDeleteAllButton;
    private RecyclerView mPlayListRecyclerView;
    private PlayListRecyclerViewAdapter mPlayListRecyclerViewAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<PlayListData> mList;
    private PlayListDataManager mPlayListDataManager = null;
    private OnClickDialogPlayListListener listener = null;

    //Controller
    private CardView mControllerCardView;
    private ImageView mControllerImageView;
    private TextView mControllerTitleTextView;
    private Button mControllerPlayButton, mControllerCloseButton;
    private SwitchButton mAutoplayButton;
    private SwitchIconView mRepeatSwitchIconView;
    private OnClickDialogControllerListener mControllerListener = null;

    //DynamicLink
    private DynamicLinkManager mDynamicLinkManager = null;

    public DialogPlayList(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        init();
    }

    public DialogPlayList(@NonNull Activity activity, int themeResId) {
        super(activity, themeResId);
        this.mActivity = activity;
        init();
    }

    protected DialogPlayList(@NonNull Activity activity, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(activity, cancelable, cancelListener);
        this.mActivity = activity;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_playlist);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mDynamicLinkManager = new DynamicLinkManager(mActivity);

        mEmptyTextView = (TextView) findViewById(R.id.playlist_empty_textView);
        mPlayListRecyclerView = (RecyclerView) findViewById(R.id.playlist_recyclerview);

        mClosebutton = (Button) findViewById(R.id.playlist_close_button);
        mClosebutton.setOnClickListener(this);

        mDeleteAllButton = (Button) findViewById(R.id.playlist_deleteAll_button);
        mDeleteAllButton.setOnClickListener(this);

        //Play Controller
        mControllerCardView = (CardView)findViewById(R.id.playlist_controller_cardview);
        mControllerImageView = (ImageView)findViewById(R.id.playlist_controller_imageView);
        mControllerTitleTextView = (TextView) findViewById(R.id.playlist_controller_title_textView);
        mControllerPlayButton = (Button) findViewById(R.id.playlist_controller_play_button);
        mControllerCloseButton = (Button) findViewById(R.id.playlist_controller_close_button);
        mAutoplayButton = (SwitchButton)findViewById(R.id.playlist_switch_button);
        mRepeatSwitchIconView = (SwitchIconView)findViewById(R.id.playlist_repeat_switchIconView);

        mControllerPlayButton.setOnClickListener(onClickPlayControllerListener);
        mControllerCloseButton.setOnClickListener(onClickPlayControllerListener);
        mRepeatSwitchIconView.setOnClickListener(this);

        boolean autoPlayState = SharedPreferencesUtils.getBoolean(mActivity, CommonSharedPreferencesKey.KEY_AUTOPLAY);
        mAutoplayButton.setChecked(autoPlayState);
        mAutoplayButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferencesUtils.setBoolean(mActivity, CommonSharedPreferencesKey.KEY_AUTOPLAY, isChecked);
            }
        });

        boolean allRepeatState = SharedPreferencesUtils.getBoolean(mActivity, CommonSharedPreferencesKey.KEY_ALLREPEAT);
        mRepeatSwitchIconView.setIconEnabled(allRepeatState, true);

        loadPlayListData();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = ((PlayerActivity) mActivity).getMenuInflater();
        return true;
    }

    public void setOnClickListener(OnClickDialogPlayListListener listener){
        if(listener != null){
            this.listener = listener;
        }
    }

    public void setOnClickControllerListener(OnClickDialogControllerListener listener){
        if(listener != null){
            this.mControllerListener = listener;
        }
    }

    private void loadPlayListData(){
        mPlayListDataManager = new PlayListDataManager(mActivity);
        mPlayListDataManager.initTable();
        mList = mPlayListDataManager.loadPlayList();

        if(mList == null || mList.size() == 0){
            mEmptyTextView.setVisibility(View.VISIBLE);
            mControllerCardView.setVisibility(View.GONE);
        }else{
            mEmptyTextView.setVisibility(View.GONE);
            mControllerCardView.setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter() {
        mPlayListRecyclerViewAdapter = new PlayListRecyclerViewAdapter(mActivity);
        mPlayListRecyclerViewAdapter.setOnStartDragListener(this);
        mPlayListRecyclerViewAdapter.setOnClickPlayListItemListener(onClickPlayListItemListener);
        mPlayListRecyclerViewAdapter.setData(mList);
        mPlayListRecyclerView.setHasFixedSize(true);
        mPlayListRecyclerView.addItemDecoration(new SearchDecoration(mActivity));
        mPlayListRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        PlayListItemTouchHelperCallback mCallback = new PlayListItemTouchHelperCallback(mPlayListRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mPlayListRecyclerView);

        mPlayListRecyclerView.setAdapter(mPlayListRecyclerViewAdapter);
    }


    @Override
    public void onStartDrag(PlayListRecyclerViewAdapter.ItemViewHolder itemViewHolder) {
        mItemTouchHelper.startDrag(itemViewHolder);
    }

    /**
     * 컨트롤러 정보를 새로고침 한다.
     */
    public void refreshController(String thumbUrl, String title, boolean playing){
        Glide.with(mActivity).load(thumbUrl)
                .thumbnail(0.1f)
                .into(mControllerImageView);

        mControllerTitleTextView.setText(title);

        if(playing){
            mControllerPlayButton.setBackgroundResource(R.drawable.ic_pause_gray_24dp);
        }else{
            mControllerPlayButton.setBackgroundResource(R.drawable.ic_play_arrow_gray_24dp);
        }
    }

    /**
     * 콘트롤러를 숨긴다.
     */
    public void hideController(){
        mControllerCardView.setVisibility(View.GONE);
    }

    private void deleteAll(){
        mPlayListDataManager.deleteAll();
        mList.clear();
        mPlayListRecyclerViewAdapter.notifyDataSetChanged();
        mEmptyTextView.setVisibility(View.VISIBLE);
        mControllerCardView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playlist_close_button:
                dismiss();
                break;

            case R.id.playlist_repeat_switchIconView:
                boolean allRepeatState = !mRepeatSwitchIconView.isIconEnabled();

                Resources resources = mActivity.getResources();
                if(allRepeatState){
                    Toast.makeText(mActivity, resources.getString(R.string.playlist_all_repeat_enable), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mActivity, resources.getString(R.string.playlist_all_repeat_disable), Toast.LENGTH_SHORT).show();
                }

                mRepeatSwitchIconView.setIconEnabled(allRepeatState, true);
                SharedPreferencesUtils.setBoolean(mActivity, CommonSharedPreferencesKey.KEY_ALLREPEAT, allRepeatState);
                break;

            case R.id.playlist_deleteAll_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("PLAYLIST");
                builder.setMessage(mActivity.getResources().getString(R.string.playlist_delete));
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAll();
                            }
                        });
                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                builder.show();
                break;
        }
    }

    private View.OnClickListener onClickPlayListItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlayListData data = (PlayListData)v.getTag();
            switch (v.getId()){
                case R.id.view_playlist_parent_lay:
                    listener.onPlay(data);
                    break;
                case R.id.view_playlist_share_lay:
                    String title = data.getTitle();
                    String id = data.getVideoId();
                    String startTime = data.getStartTime();
                    String endTime = data.getEndTime();
                    mDynamicLinkManager.createShortDynamicLink(title, id, startTime, endTime);
                    break;
            }
        }
    };

    /**
     * PlayController Listener
     */
    private View.OnClickListener onClickPlayControllerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.playlist_controller_play_button:
                    if(mControllerListener != null){
                        mControllerListener.play(v);
                    }
                    break;
                case R.id.playlist_controller_close_button:
                    mControllerCardView.setVisibility(View.GONE);
                    break;
            }
        }
    };
}
