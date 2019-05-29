package com.venuskimblessing.youtuberepeatfree;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.gson.Gson;
import com.kobakei.ratethisapp.RateThisApp;
import com.venuskimblessing.youtuberepeatfree.Adapter.SearchDecoration;
import com.venuskimblessing.youtuberepeatfree.Adapter.SearchRecyclerViewAdapter;
import com.venuskimblessing.youtuberepeatfree.Billing.BillingManager;
import com.venuskimblessing.youtuberepeatfree.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatfree.Common.IntentAction;
import com.venuskimblessing.youtuberepeatfree.Common.IntentKey;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogChat;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogCoffee;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogEnding;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogInfo;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogPlayList;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogRecommend;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogSort;
import com.venuskimblessing.youtuberepeatfree.Json.SearchList;
import com.venuskimblessing.youtuberepeatfree.Json.Videos;
import com.venuskimblessing.youtuberepeatfree.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatfree.Player.PlayerActivity;
import com.venuskimblessing.youtuberepeatfree.Retrofit.RetrofitCommons;
import com.venuskimblessing.youtuberepeatfree.Retrofit.RetrofitManager;
import com.venuskimblessing.youtuberepeatfree.Retrofit.RetrofitService;
import com.venuskimblessing.youtuberepeatfree.Utils.MediaUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity implements SearchRecyclerViewAdapter.OnClickRecyclerViewItemListener, View.OnClickListener {
    public static final String TAG = "SearchActivity";

    public static final String DEFAULT_WORD = "";

    //Search Type
    private final String TYPE_POPULAR = "popular"; //인기동영상
    private final String TYPE_SEARCH = "search"; //일반검색

    //Request Code
    public static final int REQUEST_INVITE = 1;
    public static final int REQUEST_PLAYER_INVITE = 2;

    //Firebase
    public static final String EVENT_INVITATION = "Invitation Dialog";
    public static final String EVENT_INVITATION_COMPLETE = "Invitation Complete";
    public static final String EVENT_SORT = "sort";
    private FirebaseAnalytics mFirebaseAnalytics;

    //Search Order
    public static final String ORDER_DATE = "date";
    public static final String ORDER_RATING = "rating";
    public static final String ORDER_RELEVANCE = "relevance";
    public static final String ORDER_TITLE = "title";
    public static final String ORDER_VIDEOCOUNT = "videoCount";
    public static final String ORDER_VIEWCOUNT = "viewCount";
    public String mOrder = ORDER_RELEVANCE;

    //Search Type
    public static final String TYPE_VIDEO = "video";

    private TextView mEmptyTextView = null;
    private Button mInviteButton, mSortButton, mRecommentButton, mCoffeeButton, mPlaylistButton;
    private MaterialTextField mMaterialTextField = null;
    private EditText mEditTextSearchWord = null;
    private RecyclerView mRecyclerView = null;
    private SearchRecyclerViewAdapter mSearchRecyclerViewAdapter = null;
    private boolean mLoading = false;

    private Retrofit mRetrofit = null;
    private RetrofitService mService = null;
    private Call<String> mCallPopularSearch = null;
    private Call<String> mCallYoutubeSearch = null;
    private Call<String> mCallVideoDetail = null;
    private Call<String> mCallVideosContentsDetail = null;
    private Gson mGson = new Gson();

    private SearchList mSearchList = new SearchList();
    public ArrayList<SearchList.ItemsItem> mItems = null;
    public ArrayList<SearchList.ItemsItem> mDurationItems = new ArrayList<>();
    private String mNextPageToken = "";
    private SearchList.ItemsItem mSelectedItem = null;

    //TYPE
    private final String TYPE_MIME = "text/plain";

    //전면광고
    private InterstitialAd mInterstitialAd = null;
    private boolean mShareYouTubeFlag = false;

    //Video
    private String mVideoId = "";

    //PlayList
    private DialogPlayList mDialogPlayList = null;
    private PlayListData mPlayListData = null;

    //SoftKeyboard
    private SoftKeybordManager mSoftKeybordManager;

    //Inapp
    private BillingManager mBillingManager;

//
//    //배너광고
//    private LinearLayout mBannerLay;
//    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBillingManager = new BillingManager(this);
        mBillingManager.initBilling();

        initRateThisApp();
        getShareIntentData(getIntent());
        MobileAds.initialize(this, CommonApiKey.KEY_ADMOB_APP_ID);

        mEmptyTextView = (TextView) findViewById(R.id.search_empty_textView);

        mInviteButton = (Button) findViewById(R.id.search_invite_button);
        mInviteButton.setOnClickListener(this);

        mMaterialTextField = (MaterialTextField) findViewById(R.id.search_top_materialTextField);
        mMaterialTextField.getEditText().setBackgroundColor(Color.WHITE);
        mMaterialTextField.getCard().setBackgroundColor(Color.WHITE);

        mEditTextSearchWord = (EditText) findViewById(R.id.search_top_searchWord_editText);
        mEditTextSearchWord.setOnEditorActionListener(onEditorActionListener);

        mSortButton = (Button) findViewById(R.id.search_sort_button);
        mSortButton.setOnClickListener(this);

        mRecommentButton = (Button) findViewById(R.id.search_recomment_button);
        mRecommentButton.setOnClickListener(this);

        mCoffeeButton = (Button) findViewById(R.id.search_coffee_button);
        mCoffeeButton.setOnClickListener(this);

        mPlaylistButton = (Button) findViewById(R.id.search_playlist_button);
        mPlaylistButton.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 6;
                if (lastVisibleItemPosition == itemTotalCount) {
                    Log.d(TAG, "last Position...");
                    if (mLoading) {
                        Log.d(TAG, "loading... return");
                        return;
                    } else {
                        Log.d(TAG, "not loading loadcontentslist...");
                        mLoading = true;

                        String word = mEditTextSearchWord.getText().toString().trim();
                        if (word.equals("")) {
                            loadPopularContentsList();
                        } else {
                            loadSearchContentsList();
                        }
                    }
                }
            }
        });

        initRetrofit();
        loadPopularContentsList();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            mSoftKeybordManager = new SoftKeybordManager(getWindow());
            mSoftKeybordManager.hideSystemUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDynamicLink();
        loadFullAd();
    }

    /**
     * Invite DynamicLink를 수신합니다.
     */
    private void getDynamicLink() {
        //Invite 수신 기록
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {

                        if (data == null) {
                            Log.d(TAG, "dynamiclink : no data");
                            return;
                        }

                        // Get the deep link
                        Uri deepLink = data.getLink();
                        Log.d(TAG, "deeplink : " + deepLink);

                        String id = deepLink.getQueryParameter("id");
                        String startTime = deepLink.getQueryParameter("starttime");
                        String endTime = deepLink.getQueryParameter("endtime");

                        Log.d(TAG, "deeplink value >> " + id + "   " + startTime + "   " + endTime);

                        startSharePlayerActivity(id, startTime, endTime);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    /**
     * 리뷰 요청 팝업
     */
    private void initRateThisApp() {
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    private void initRetrofit() {
        mService = RetrofitManager.getRetrofitService(RetrofitCommons.BASE_URL);
    }

    /**
     * 인기 동영상을 불러옵니다.
     */
    private void loadPopularContentsList() {
        String regionCode = Locale.getDefault().getCountry();
        mCallPopularSearch = mService.getPopularYoutubeVideos("snippet", "mostPopular", regionCode, "50", mNextPageToken, CommonApiKey.KEY_API_YOUTUBE);
        mCallPopularSearch.enqueue(callback);
    }

    private void loadSearchContentsList() {
        String searchWord = mEditTextSearchWord.getText().toString().trim();
        if (searchWord.equals("")) {
            searchWord = DEFAULT_WORD;
        }

        mCallYoutubeSearch = mService.getYoutubeSearch("snippet", searchWord, "50", TYPE_VIDEO, mOrder, mNextPageToken, CommonApiKey.KEY_API_YOUTUBE);
        mCallYoutubeSearch.enqueue(callback);
    }

    /**
     * 비디오의 상세 정보를 가져온다.
     */
    private void loadVideos(String id) {
        mCallVideoDetail = mService.getYoutubeVideos("snippet,contentDetails,statistics", id, CommonApiKey.KEY_API_YOUTUBE);
        mCallVideoDetail.enqueue(callback);
    }

    /**
     * 다수의 비디오 리스트에 대한 상세 정보를 얻어온다.
     *
     * @param ids
     */
    private void loadVideosContentsDetail(String ids) {
        mCallVideosContentsDetail = mService.getYoutubeVideos("contentDetails, statistics", ids, CommonApiKey.KEY_API_YOUTUBE);
        mCallVideosContentsDetail.enqueue(callback);
    }

    /**
     * 통신을 위한 VideoId를 조합한다.
     *
     * @return
     */
    private String createVideoIdList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mDurationItems.size(); i++) {
            SearchList.ItemsItem item = mDurationItems.get(i);
            String videoId = item.getVideoId();
            stringBuilder.append(videoId);
            if (i == mDurationItems.size() - 1) {
                break;
            }
            stringBuilder.append(",");
        }
        return stringBuilder.toString().trim();
    }

    /**
     * durationItems에 duration을 세팅하는 작업을 한 후 원본 mItems와 통합하는 작업을 한다.
     *
     * @param videos
     */
    private void mergeVideosList(Videos videos) {
        ArrayList<Videos.Item> items = videos.items;
        for (int i = 0; i < items.size(); i++) {
            Videos.Item item = items.get(i);

            Videos.ContentDetails contentDetails = item.contentDetails;
            SearchList.ItemsItem durationItem = mDurationItems.get(i);

            //Duration set
            String durationString = null;
            if (contentDetails != null) {
                durationString = contentDetails.duration;
            }

            int duration = 0;
            if (durationString != null) {
                duration = (int) MediaUtils.getDuration(durationString);
            }

            String durationResult = MediaUtils.getMillSecToHMS(duration);
            if (durationResult != null) {
                durationItem.setDuration(durationResult);
            }

            //ViewCount set
            Videos.statistics statistics = item.statistics;

            String viewCount = null;
            if (statistics != null) {
                viewCount = statistics.viewCount;
            }

            if (viewCount != null) {
                durationItem.setViewCount(viewCount);
            }
        }
        mItems.addAll(mDurationItems);
        mDurationItems.clear();
    }

    private void setAdapter() {
        mSearchRecyclerViewAdapter = new SearchRecyclerViewAdapter(this);
        mSearchRecyclerViewAdapter.setOnClickRecyclerViewItemListener(this);
        mSearchRecyclerViewAdapter.setData(mSearchList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SearchDecoration(this));
        mRecyclerView.setAdapter(mSearchRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void refreshAdapter() {
        mSearchRecyclerViewAdapter.setData(mSearchList);
        mSearchRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * 유튜브 플레이어 호출
     */
    private void startPlayerActivity(String videoId) {
        Log.d(TAG, "startPlayerActivity...");
        Intent intent = new Intent(this, PlayerActivity.class);
        if (videoId != null) {
            intent.setAction(IntentAction.INTENT_ACTION_SEARCH_PLAY);
            intent.putExtra("videoId", videoId);
        }

        if(mPlayListData != null){
            intent.setAction(IntentAction.INTENT_ACTION_SEARCH_PLAYLIST);
            intent.putExtra("data", mPlayListData);
        }
        startActivity(intent);
    }

    /**
     * 공유 받은 데이터에 대한 영상 재생
     *
     * @param id           video id
     * @param startTime    시작시간
     * @param startEndTime 끝시간
     */
    private void startSharePlayerActivity(String id, String startTime, String startEndTime) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.setAction(IntentAction.INTENT_ACTION_SHARE_VIDEO);
        intent.putExtra(IntentKey.INTENT_KEY_ID, id);
        intent.putExtra(IntentKey.INTENT_KEY_START, startTime);
        intent.putExtra(IntentKey.INTENT_KEY_END, startEndTime);
        startActivity(intent);
    }

    private void parseJsonStringData(String json, String type) {
        Log.d(TAG, "result data : " + json);

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray.length() == 0) {
                Log.d(TAG, "jsonArray is Size 0!");

                if (mItems.size() != 0) {
                    return;
                } else {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
            } else {
                mEmptyTextView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            mNextPageToken = jsonObject.getString("nextPageToken");

            for (int i = 0; i < jsonArray.length(); i++) {
                SearchList.ItemsItem itemsItem = new SearchList().new ItemsItem();
                JSONObject itemsObject = jsonArray.getJSONObject(i);

                String kind = null;
                String videoId = null;
                if (type.equals(TYPE_SEARCH)) {
                    JSONObject idObject = itemsObject.getJSONObject("id");
                    kind = idObject.optString("kind", "null");
                    videoId = idObject.optString("videoId", "null");
                } else {
                    kind = itemsObject.optString("kind", "null");
                    videoId = itemsObject.optString("id", "null");
                }

                itemsItem.setKind(kind);
                itemsItem.setVideoId(videoId);

                JSONObject snippetObject = itemsObject.getJSONObject("snippet");
                String title = snippetObject.optString("title", "null");
                String description = snippetObject.optString("description", "null");
                String channelTitle = snippetObject.optString("channelTitle", "null");
                itemsItem.setTitle(title);
                itemsItem.setDescription(description);
                itemsItem.setChannelTitle(channelTitle);

                JSONObject thumbnailsObject = snippetObject.getJSONObject("thumbnails");
                JSONObject defaultObject = thumbnailsObject.getJSONObject("default");
                JSONObject mediumObject = thumbnailsObject.getJSONObject("medium");
                JSONObject highObject = thumbnailsObject.getJSONObject("high");

                String url = highObject.optString("url", "null");

//                            String width = highObject.getString("width");
//                            String height = highObject.getString("height");
                itemsItem.setThumbnails_url(url);
//                            itemsItem.setThumbnails_width(width);
//                            itemsItem.setThumbnails_height(height);

                mItems = mSearchList.getItems();

                Log.d(TAG, "mItems size : " + mItems.size());
//                mItems.add(itemsItem);
                mDurationItems.add(itemsItem);
            }
        } catch (JSONException e) {
            Log.d(TAG, "e : " + e.toString());
            e.printStackTrace();
        }

        //Duration 정보 가져오기
        String queryVideosId = createVideoIdList();
        loadVideosContentsDetail(queryVideosId);

//        if (mSearchRecyclerViewAdapter != null) {
//            refreshAdapter();
//        } else {
//            setAdapter();
//        }
//
//        mLoading = false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextSearchWord.getWindowToken(), 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        getShareIntentData(intent);
    }

    /**
     * 유튜브로 부터 공유 메타데이터 수신
     */
    private void getShareIntentData(Intent intent) {
        Log.d(TAG, "유튜브 공유로부터 비디오 메타 데이터를 넘겨받습니다.");

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (TYPE_MIME.equals(type)) {
                String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "ShareText : " + shareText);

                mVideoId = shareText.substring(17);
                Log.d(TAG, "videoId : " + mVideoId);

                mShareYouTubeFlag = true;
                CommonUserData.sAdCount = 0;
            }
        }
    }

    /**
     * 초대를 보냅니다. Deprecated...
     */
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();

        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void inviteFriends() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_message) + "\n\n" + "https://youtuberepeatfree.page.link/main");
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,
                getString(R.string.share_title)));
    }


    /**
     * 구글 애널리틱스에 로그를 남깁니다.
     *
     * @param eventName
     */
    private void setEventLog(String eventName) {
        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT, eventName);

            if (mFirebaseAnalytics != null) {
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            } else {
                Log.d(TAG, "FirebaseAnalytics is Null...");
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception : " + e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                SharedPreferencesUtils.setBoolean(this, CommonSharedPreferencesKey.KEY_INVITATION, true);
                CommonUserData.sMaxRepeatCount = CommonUserData.COUNT_MAX;

                int invitationCount = SharedPreferencesUtils.getInt(SearchActivity.this, CommonSharedPreferencesKey.KEY_INVITATION_COUTN);
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                    invitationCount++;
                }
                SharedPreferencesUtils.setInt(SearchActivity.this, CommonSharedPreferencesKey.KEY_INVITATION_COUTN, invitationCount);

                if (invitationCount >= CommonUserData.INVITE_COUNT_COMPLETE) {
                    setEventLog(EVENT_INVITATION_COMPLETE);
                    SharedPreferencesUtils.setBoolean(SearchActivity.this, CommonSharedPreferencesKey.KEY_INVITATION, true);
                }

            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        } else if (requestCode == REQUEST_PLAYER_INVITE) {
            if (resultCode == RESULT_OK) {
                mInviteButton.performClick();
            }
        }
    }

    @Override
    public void onClickItem(SearchList.ItemsItem itemsItem) {
        this.mSelectedItem = itemsItem;
        loadVideos(itemsItem.getVideoId());
    }

    //키보드 이벤트
    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    if (mItems != null) {
                        mNextPageToken = "";
                        mItems.clear();
                    }
                    loadSearchContentsList();
                    hideKeyboard();
                    break;
                default:
                    // 기본 엔터키 동작
                    return false;
            }
            return true;
        }
    };

    //통신처리
    private Callback<String> callback = new Callback<String>() {
        @Override
        public void onResponse(Call<String> call, Response<String> response) {
            String result = null;
            try {
                result = response.body().toString();
            } catch (Exception e) {
                Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_LONG).show();
            }
            if (call == mCallYoutubeSearch) {
                parseJsonStringData(result, TYPE_SEARCH);
            } else if (call == mCallPopularSearch) {
                parseJsonStringData(result, TYPE_POPULAR);
            } else if (call == mCallVideoDetail) {
                Videos videos = (Videos) mGson.fromJson(result, Videos.class);
                Videos.PageInfo pageInfo = videos.pageInfo;
                String totalResults = pageInfo.totalResults;
                if (totalResults.equals("0")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    DialogInfo dialogInfo = new DialogInfo(SearchActivity.this, R.style.custom_dialog_fullScreen);
                    dialogInfo.setData(videos, mSelectedItem.getThumbnails_url());
                    dialogInfo.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mVideoId = (String) v.getTag();
                            checkShowFullAd();
                        }
                    });
                    dialogInfo.show();
                }
            } else if (call == mCallVideosContentsDetail) {
                Videos videos = (Videos) mGson.fromJson(result, Videos.class);
                if (videos != null) {
                    Videos.PageInfo pageInfo = videos.pageInfo;
                    String totalResults = pageInfo.totalResults;
                    if (totalResults.equals("0")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        mergeVideosList(videos);
                        if (mSearchRecyclerViewAdapter != null) {
                            refreshAdapter();
                        } else {
                            setAdapter();
                        }
                        mLoading = false;
                    }
                } else { // 네트워크 오류
                    Toast.makeText(SearchActivity.this, getString(R.string.error_videos_emptyInfo), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
            Log.d(TAG, "t " + t.toString());
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_invite_button:
//                setEventLog(EVENT_INVITATION);
//                DialogCommon dialogInvitation = new DialogCommon(this, R.style.custom_dialog_fullScreen);
//                dialogInvitation.setOnClickListener(this);
//                dialogInvitation.show();
                inviteFriends();
                break;
            case R.id.dialog_common_one_button:
//                onInviteClicked();
//                inviteFriends();
                break;

            case R.id.search_sort_button:
                setEventLog(EVENT_SORT);
                final DialogSort dialogSort = new DialogSort(this, R.style.custom_dialog_fullScreen);
                dialogSort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.sort_date_button:
                                mOrder = ORDER_DATE;
                                mSortButton.setBackgroundResource(R.drawable.ic_sort_date_wite_24dp);
                                break;
                            case R.id.sort_rating_button:
                                mOrder = ORDER_RATING;
                                mSortButton.setBackgroundResource(R.drawable.ic_sort_rating_white_24dp);
                                break;
                            case R.id.sort_relevance_button:
                                mOrder = ORDER_RELEVANCE;
                                mSortButton.setBackgroundResource(R.drawable.ic_sort_compare_arrows_white_24dp);
                                break;
                            case R.id.sort_count_button:
                                mOrder = ORDER_VIEWCOUNT;
                                mSortButton.setBackgroundResource(R.drawable.ic_sort_streetview_white_24dp);
                                break;
                        }
                        dialogSort.dismiss();
                        mRecyclerView.scrollToPosition(0);
                        mNextPageToken = "";
                        if (mItems != null) {
                            mItems.clear();
                            mSearchRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        loadSearchContentsList();
                    }
                });
                dialogSort.show();
                break;

            case R.id.search_recomment_button:
                setEventLog(EVENT_SORT);
                final DialogRecommend dialogRecommend = new DialogRecommend(this, R.style.custom_dialog_fullScreen);
                dialogRecommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.recommend_most_button:
                                dialogRecommend.dismiss();
                                mNextPageToken = "";
                                if (mItems != null) {
                                    mRecyclerView.scrollToPosition(0);
                                    mItems.clear();
                                    loadPopularContentsList();
                                    return;
                                }
                                break;
                            case R.id.recommend_home_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_hometraining));
                                break;
                            case R.id.recommend_dance_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_dance));
                                break;
                            case R.id.recommend_asmr_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_asmr));
                                break;
                            case R.id.recommend_makeup_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_makeup));
                                break;
                            case R.id.recommend_magic_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_magic));
                                break;

                            case R.id.recommend_cook_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_cook));
                                break;
                            case R.id.recommend_billbordMusic_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_music));
                                break;
                            case R.id.recommend_sleep_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_sleep));
                                break;
                            case R.id.recommend_relaxing_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_relaxing));
                                break;
                            case R.id.recommend_kids_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_kids));
                                break;
                            case R.id.recommend_sexy_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_sexy));
                                break;
                            case R.id.recommend_sexyAni_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_sexyani));
                                break;
                            case R.id.recommend_sexySound_button:
                                mEditTextSearchWord.setText(getResources().getString(R.string.dialog_recommend_sexysound));
                                break;
                        }
                        dialogRecommend.dismiss();
                        mNextPageToken = "";
                        if (mItems != null) {
                            mRecyclerView.scrollToPosition(0);
                            mItems.clear();
                            mSearchRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        loadSearchContentsList();
                    }
                });
                dialogRecommend.show();
                break;

            case R.id.search_coffee_button:
                Intent intent = new Intent(SearchActivity.this, BuyPremiumActivity.class);
                startActivity(intent);
                break;

            case R.id.search_playlist_button:
                mDialogPlayList = new DialogPlayList(this, R.style.custom_dialog_fullScreen);
                mDialogPlayList.setOnClickListener(new DialogPlayList.OnClickDialogPlayListListener() {
                    @Override
                    public void onPlay(PlayListData data) {
                        mVideoId = null;
                        mPlayListData = data;
                        checkShowFullAd();
                    }
                });
                mDialogPlayList.show();
                mDialogPlayList.hideController();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DialogEnding dialogEnding = new DialogEnding(SearchActivity.this, R.style.custom_dialog_fullScreen);
        dialogEnding.show();
    }

    //전면 광고

    /**
     * 전면 광고 노출
     */
    private void loadFullAd() {
        Log.d(TAG, "전면 광고 로드..");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(CommonApiKey.KEY_ADMOB_FULL_UNIT);
        mInterstitialAd.setAdListener(adListener);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void checkShowFullAd() {
        int adCount = CommonUserData.sAdCount;
        int delayCount = CommonUserData.AD_DEALY_COUNT;
        if (adCount == 0 || adCount >= delayCount) {
            Log.d(TAG, "show full ad...");
            showFullAd();
        } else {
            Log.d(TAG, "not show full ad...");
            startPlayerActivity(mVideoId);
        }
        CommonUserData.sAdCount++;
    }

    private void showFullAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            CommonUserData.sAdCount = 0;
        } else {
            Log.d(TAG, "showFullAd 광고 로드 실패 재호출");
            startPlayerActivity(mVideoId);
        }
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
            startPlayerActivity(mVideoId);
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            Log.d(TAG, "onAdFailedToLoad");
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            if (mShareYouTubeFlag) {
                Log.d(TAG, "유튜브 전용 로직");
                showFullAd();
                mShareYouTubeFlag = false;
            }
            Log.d(TAG, "onAdLoaded...");
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
        }
    };

    private void showChat() {
        DialogChat dialogChat = new DialogChat(this, R.style.custom_dialog_fullScreen);
        dialogChat.show();
    }
}
