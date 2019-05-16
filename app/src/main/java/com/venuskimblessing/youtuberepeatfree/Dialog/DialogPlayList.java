package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.suke.widget.SwitchButton;
import com.venuskimblessing.youtuberepeatfree.Adapter.PlayListRecyclerViewAdapter;
import com.venuskimblessing.youtuberepeatfree.Adapter.SearchDecoration;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Interface.PlayListItemTouchHelperCallback;
import com.venuskimblessing.youtuberepeatfree.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatfree.PlayList.PlayListDataManager;
import com.venuskimblessing.youtuberepeatfree.Player.PlayerActivity;
import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DialogPlayList extends Dialog implements View.OnClickListener, PlayListRecyclerViewAdapter.OnStartDragListener {
    private static final String TAG = "DialogPlayList";

    public interface OnClickDialogPlayListListener{
        public void onPlay(PlayListData data);
    }

    public interface OnClickDialogControllerListener{
        public void play(View v);
    }

    private Activity activity;
    private Context mContext;
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

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    private void init(){
        setContentView(R.layout.layout_dialog_playlist);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

        boolean autoPlayState = SharedPreferencesUtils.getBoolean(mContext, CommonSharedPreferencesKey.KEY_AUTOPLAY);
        mAutoplayButton.setChecked(autoPlayState);
        mAutoplayButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferencesUtils.setBoolean(mContext, CommonSharedPreferencesKey.KEY_AUTOPLAY, isChecked);
            }
        });

        boolean allRepeatState = SharedPreferencesUtils.getBoolean(mContext, CommonSharedPreferencesKey.KEY_ALLREPEAT);
        mRepeatSwitchIconView.setIconEnabled(allRepeatState, true);

        loadPlayListData();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = ((PlayerActivity)mContext).getMenuInflater();
        menuInflater.inflate(R.menu.playlist_menu, menu);
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
        mPlayListDataManager = new PlayListDataManager(mContext);
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

    /**
     * 컨트롤러 정보를 새로고침 한다.
     */
    public void refreshController(String thumbUrl, String title, boolean playing){
        Glide.with(mContext).load(thumbUrl)
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

    private void shareLink(String dynamicLinkUrl){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUrl);
        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
        mContext.startActivity(chooser);
    }

//    private void createDynamicLink(){
//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse("https://youtubeplayerfree.com/a=abcd&b=123"))
//                .setDomainUriPrefix("https://youtuberepeatfree.page.link/")
//                // Open links with this app on Android
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                // Open links with com.example.ios on iOS
//                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
//                .buildDynamicLink();
//
//        Uri dynamicLinkUri = dynamicLink.getUri();
//        Log.d(TAG, "url : " + dynamicLinkUri.toString());
//    }

    private void createShortDynamicLink(){
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://youtubeplayerfree.com"))
                .setDomainUriPrefix("https://youtuberepeatfree.page.link/")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.d(TAG, "short url : " + shortLink.toString());
                            Log.d(TAG, "flowchartLink url : " + flowchartLink.toString());
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playlist_close_button:
                dismiss();
                break;

            case R.id.playlist_repeat_switchIconView:
                boolean allRepeatState = !mRepeatSwitchIconView.isIconEnabled();

                Resources resources = mContext.getResources();
                if(allRepeatState){
                    Toast.makeText(mContext, resources.getString(R.string.playlist_all_repeat_enable), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, resources.getString(R.string.playlist_all_repeat_disable), Toast.LENGTH_SHORT).show();
                }

                mRepeatSwitchIconView.setIconEnabled(allRepeatState, true);
                SharedPreferencesUtils.setBoolean(mContext, CommonSharedPreferencesKey.KEY_ALLREPEAT, allRepeatState);
                break;

            case R.id.playlist_deleteAll_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("PLAYLIST");
                builder.setMessage(mContext.getResources().getString(R.string.playlist_delete));
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
            switch (v.getId()){
                case R.id.view_playlist_parent_lay:
                    PlayListData data = (PlayListData)v.getTag();
                    listener.onPlay(data);
                    break;
                case R.id.view_playlist_share_lay:
                    Toast.makeText(mContext, "플레이리스트 More 뷰 클릭 이벤트", Toast.LENGTH_SHORT).show();
//                    shareLink();
//                    createDynamicLink();
                    createShortDynamicLink();
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
