package com.venuskimblessing.youtuberepeatlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingClientStateListener;
//import com.android.billingclient.api.ConsumeResponseListener;
//import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchaseHistoryResponseListener;
//import com.android.billingclient.api.PurchasesUpdatedListener;
//import com.android.vending.billing.IInAppBillingService;
import com.github.florent37.materialtextfield.MaterialTextField;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.gson.Gson;
import com.venuskimblessing.youtuberepeatlite.Adapter.SearchDecoration;
import com.venuskimblessing.youtuberepeatlite.Adapter.SearchRecyclerViewAdapter;
import com.venuskimblessing.youtuberepeatlite.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogInfo;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogInvitation;
import com.venuskimblessing.youtuberepeatlite.Json.PlayingData;
import com.venuskimblessing.youtuberepeatlite.Json.SearchList;
import com.venuskimblessing.youtuberepeatlite.Json.Videos;
import com.venuskimblessing.youtuberepeatlite.Player.PlayerActivity;
import com.venuskimblessing.youtuberepeatlite.Retrofit.RetrofitCommons;
import com.venuskimblessing.youtuberepeatlite.Retrofit.RetrofitManager;
import com.venuskimblessing.youtuberepeatlite.Retrofit.RetrofitService;
import com.venuskimblessing.youtuberepeatlite.Utils.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SearchActivity extends AppCompatActivity implements SearchRecyclerViewAdapter.OnClickRecyclerViewItemListener, View.OnClickListener {
    public static final String TAG = "SearchActivity";

    public static final String DEFAULT_WORD = "cinematic trailer";

    //Request Code
    public static final int REQUEST_INVITE = 1;
    public static final int REQUEST_PLAYER_INVITE = 2;

    //Search Order
    public static final String ORDER_DATE = "date";
    public static final String ORDER_RATING = "rating";
    public static final String ORDER_RELEVANCE = "relevance";
    public static final String ORDER_TITLE = "title";
    public static final String ORDER_VIDEOCOUNT = "videoCount";
    public static final String ORDER_VIEWCOUNT = "viewCount";

    private Button mInviteButton = null;
    private MaterialTextField mMaterialTextField = null;
    private EditText mEditTextSearchWord = null;
    private RecyclerView mRecyclerView = null;
    private SearchRecyclerViewAdapter mSearchRecyclerViewAdapter = null;
    private boolean mLoading = false;

    private Retrofit mRetrofit = null;
    private RetrofitService mService = null;
    private Call<String> mCallYoutubeSearch = null;
    private Call<String> mCallVideos = null;
    private Gson mGson = new Gson();

    private SearchList mSearchList = new SearchList();
    public ArrayList<SearchList.ItemsItem> mItems = null;
    private String mNextPageToken = "";
    private SearchList.ItemsItem mSelectedItem = null;

    //TYPE
    private final String TYPE_MIME = "text/plain";

//    //전면광고
//    private InterstitialAd mInterstitialAd = null;
//
//    //배너광고
//    private LinearLayout mBannerLay;
//    private AdView mAdView;
//
//    //인앱결제
//    private BillingClient mBillingClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getShareIntentData();

        mInviteButton = (Button) findViewById(R.id.search_invite_button);
        mInviteButton.setOnClickListener(this);

        mMaterialTextField = (MaterialTextField)findViewById(R.id.search_top_materialTextField);
        mMaterialTextField.getEditText().setBackgroundColor(Color.WHITE);
        mMaterialTextField.getCard().setBackgroundColor(Color.WHITE);

        mEditTextSearchWord = (EditText)findViewById(R.id.search_top_searchWord_editText);
        mEditTextSearchWord.setOnEditorActionListener(onEditorActionListener);

        mRecyclerView = (RecyclerView)findViewById(R.id.search_recyclerview);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 6;
                if (lastVisibleItemPosition == itemTotalCount) {
                    Log.d(TAG, "last Position...");
                    if(mLoading){
                        Log.d(TAG, "loading... return");
                        return;
                    }else{
                        Log.d(TAG, "not loading loadcontentslist...");
                        mLoading = true;
                        loadContentsList();
                    }
                }
            }
        });
        initRetrofit();
        loadContentsList();
    }

    private void initRetrofit() {
        mService = RetrofitManager.getRetrofitService(RetrofitCommons.BASE_URL);
    }

    private void loadContentsList(){
        String searchWord = mEditTextSearchWord.getText().toString().trim();
        if(searchWord.equals("")){
            searchWord = DEFAULT_WORD;
        }

        mCallYoutubeSearch = mService.getYoutubeSearch("snippet",searchWord, "50", ORDER_RELEVANCE, mNextPageToken, CommonApiKey.KEY_API_YOUTUBE);
        mCallYoutubeSearch.enqueue(callback);
    }

    /**
     * 비디오의 상세 정보를 가져온다.
     */
    private void loadVideos(String id) {
        mCallVideos = mService.getYoutubeVideos("snippet,contentDetails,statistics", id, CommonApiKey.KEY_API_YOUTUBE);
        mCallVideos.enqueue(callback);
    }

    private void setAdapter(){
        mSearchRecyclerViewAdapter = new SearchRecyclerViewAdapter(this);
        mSearchRecyclerViewAdapter.setOnClickRecyclerViewItemListener(this);
        mSearchRecyclerViewAdapter.setData(mSearchList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SearchDecoration(this));
        mRecyclerView.setAdapter(mSearchRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void refreshAdapter(){
        mSearchRecyclerViewAdapter.setData(mSearchList);
        mSearchRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * 유튜브 플레이어 호출
     */
    private void startPlayer(String videoId){
        Intent intent = new Intent(this, LoadingActivity.class);
        if(videoId != null){
            intent.putExtra("videoId", videoId);
            startActivityForResult(intent, REQUEST_PLAYER_INVITE);
        }
    }

    private void parseJsonStringData(String json){
        Log.d(TAG, "result data : " + json);

        try {
            JSONObject jsonObject = new JSONObject(json);
            mNextPageToken = jsonObject.getString("nextPageToken");

            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for(int i=0; i<jsonArray.length(); i++){
                SearchList.ItemsItem itemsItem = new SearchList().new ItemsItem();

                JSONObject itemsObject = jsonArray.getJSONObject(i);

                JSONObject idObject = itemsObject.getJSONObject("id");
                String kind = idObject.optString("kind", "null");
                String videoId = idObject.optString("videoId", "null");

                itemsItem.setKind(kind);
                itemsItem.setVideoId(videoId);

                JSONObject snippetObject = itemsObject.getJSONObject("snippet");
                String title = snippetObject.optString("title", "null");
                String description = snippetObject.optString("description", "null");
                    itemsItem.setTitle(title);
                    itemsItem.setDescription(description);

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
                mItems.add(itemsItem);
            }
        } catch (JSONException e) {
            Log.d(TAG, "e : " + e.toString());
            e.printStackTrace();
        }

        if(mSearchRecyclerViewAdapter != null){
            refreshAdapter();
        }else{
            setAdapter();
        }

        mLoading = false;
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextSearchWord.getWindowToken(), 0);
    }

        /**
     * 유튜브로 부터 공유 메타데이터 수신
     */
    private void getShareIntentData() {
        Log.d(TAG, "유튜브 공유로부터 비디오 메타 데이터를 넘겨받습니다.");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (TYPE_MIME.equals(type)) {
                String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "ShareText : " + shareText);

                String videoId = shareText.substring(17);
                Log.d(TAG, "videoId : " + videoId);

                startPlayer(videoId);
            }
        }
    }

    /**
     * 초대를 보냅니다.
     */
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();

        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                SharedPreferencesUtils.setBoolean(this, CommonSharedPreferencesKey.KEY_INVITATION, true); //친구초대 성공으로 반복횟수99개증가
                CommonUserData.sMaxRepeatCount = CommonUserData.COUNT_MAX;

                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }else if(requestCode == REQUEST_PLAYER_INVITE){
            if(resultCode == RESULT_OK){
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
                    if(mItems != null){
                        mItems.clear();
                    }
                    loadContentsList();
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
           String result = response.body().toString();
           Log.d(TAG, "onResponse...");

           if (call == mCallYoutubeSearch) {
               parseJsonStringData(result);
           } else if (call == mCallVideos) {
               Videos videos = (Videos) mGson.fromJson(result, Videos.class);
                Videos.PageInfo pageInfo = videos.pageInfo;
                String totalResults = pageInfo.totalResults;
                if(totalResults.equals("0")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    DialogInfo dialogInfo = new DialogInfo(SearchActivity.this, R.style.custom_dialog_fullScreen);
                    dialogInfo.setData(videos, mSelectedItem.getThumbnails_url());
                    dialogInfo.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String videoId = (String)v.getTag();
                            startPlayer(videoId);
                        }
                    });
                    dialogInfo.show();
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
        switch (v.getId()){
            case R.id.search_invite_button:
                DialogInvitation dialogInvitation = new DialogInvitation(this, R.style.custom_dialog_fullScreen);
                dialogInvitation.setOnClickListener(this);
                dialogInvitation.show();
                break;
            case R.id.dialog_invitation_button:
                onInviteClicked();
                break;
        }
    }
}
