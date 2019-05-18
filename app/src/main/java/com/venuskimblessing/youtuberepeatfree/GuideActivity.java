package com.venuskimblessing.youtuberepeatfree;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dd.morphingbutton.MorphingButton;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Guide.GuideData;
import com.venuskimblessing.youtuberepeatfree.Guide.GuideFragmentStatePagerAdapter;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.UIConvertUtils;

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
        GuideData introduce1 = new GuideData();
        introduce1.setResourceImage(R.drawable.introduce1);
        introduce1.setDescription(getResources().getString(R.string.introduce1));

        GuideData introduce2 = new GuideData();
        introduce2.setResourceImage(R.drawable.introduce2);
        introduce2.setDescription(getResources().getString(R.string.introduce2));

        GuideData introduce3 = new GuideData();
        introduce3.setResourceImage(R.drawable.introduce3);
        introduce3.setDescription(getResources().getString(R.string.introduce3));

        GuideData introduce4 = new GuideData();
        introduce4.setResourceImage(R.drawable.introduce4);
        introduce4.setDescription(getResources().getString(R.string.introduce4));

        GuideData introduce5 = new GuideData();
        introduce5.setResourceImage(R.drawable.introduce5);
        introduce5.setDescription(getResources().getString(R.string.introduce5));

        GuideData guideLast = new GuideData();
        guideLast.setResourceImage(R.drawable.bg_loading);
        guideLast.setDescription(getResources().getString(R.string.guidelast));

        mResourceList.add(introduce1);
        mResourceList.add(introduce2);
        mResourceList.add(introduce3);
        mResourceList.add(introduce4);
        mResourceList.add(introduce5);
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
