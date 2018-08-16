package com.venuskimblessing.youtuberepeatlite;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.venuskimblessing.youtuberepeatlite.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatlite.Guide.GuideData;
import com.venuskimblessing.youtuberepeatlite.Guide.GuideFragmentStatePagerAdapter;
import com.venuskimblessing.youtuberepeatlite.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatlite.Utils.UIConvertUtils;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public static final String TAG = "GuideActivity";

    private ViewPager mViewPager = null;
    private GuideFragmentStatePagerAdapter mGuideFragmentStatePagerAdapter = null;
    private PageIndicatorView mPageIndicatorView = null;
    private ArrayList<GuideData> mResourceList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        setResourceData();

        mViewPager = (ViewPager)findViewById(R.id.guide_viewpager);
        mGuideFragmentStatePagerAdapter = new GuideFragmentStatePagerAdapter(getSupportFragmentManager());
        mGuideFragmentStatePagerAdapter.setGuideResourceList(mResourceList);
        mViewPager.setAdapter(mGuideFragmentStatePagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mPageIndicatorView = findViewById(R.id.pageIndicatorView);
        mPageIndicatorView.setAnimationType(AnimationType.DROP);
        mPageIndicatorView.setCount(mResourceList.size()); // specify total count of indicators
        mPageIndicatorView.setSelection(0);
    }

    private void setResourceData(){
        GuideData guideIntro = new GuideData();
        guideIntro.setResourceImage(R.drawable.guideintro);
        guideIntro.setDescription(getResources().getString(R.string.intro));

        GuideData guideSearch = new GuideData();
        guideSearch.setResourceImage(R.drawable.guidesearch);
        guideSearch.setDescription(getResources().getString(R.string.search));

        GuideData guideData01 = new GuideData();
        guideData01.setResourceImage(R.drawable.guide01);
        guideData01.setDescription(getResources().getString(R.string.guide1));

        GuideData guideData02 = new GuideData();
        guideData02.setResourceImage(R.drawable.guide02);
        guideData02.setDescription(getResources().getString(R.string.guide2));

        GuideData guideData03 = new GuideData();
        guideData03.setResourceImage(R.drawable.guide03);
        guideData03.setDescription(getResources().getString(R.string.guide3));

        GuideData guideData04 = new GuideData();
        guideData04.setResourceImage(R.drawable.guide04);
        guideData04.setDescription(getResources().getString(R.string.guide4));

        GuideData guideData05 = new GuideData();
        guideData05.setResourceImage(R.drawable.guide05);
        guideData05.setDescription(getResources().getString(R.string.guide5));

        GuideData guideData06 = new GuideData();
        guideData06.setResourceImage(R.drawable.guide06);
        guideData06.setDescription(getResources().getString(R.string.guide6));

        GuideData guideLast = new GuideData();
        guideLast.setResourceImage(R.drawable.bg_loading);
        guideLast.setDescription(getResources().getString(R.string.guidelast));

        mResourceList.add(guideIntro);
        mResourceList.add(guideSearch);
        mResourceList.add(guideData01);
        mResourceList.add(guideData02);
        mResourceList.add(guideData03);
        mResourceList.add(guideData04);
        mResourceList.add(guideData05);
        mResourceList.add(guideData06);
        mResourceList.add(guideLast);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "onPageScrolled... " + position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected... " + position);
        mPageIndicatorView.setSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged... " + state);

    }

    @Override
    public void onClick(View v) {
        MorphingButton morphingButton = (MorphingButton)v;
        int dp = (int) UIConvertUtils.convertDpToPixel(36, GuideActivity.this);
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(300)
                .cornerRadius(dp) // 56 dp
                .width(dp) // 56 dp
                .height(dp) // 56 dp
                .color(Color.parseColor("#FFFFFF")) // normal state color
                .icon(R.drawable.ic_done_black_24dp); // icon
        morphingButton.morph(circle);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GuideActivity.this, SearchActivity.class);
                SharedPreferencesUtils.setBoolean(GuideActivity.this, CommonSharedPreferencesKey.KEY_GUIDE, true);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
}
