package com.venuskimblessing.youtuberepeatlite.Guide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class GuideFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = "GuideFragmentStatePagerAdapter";

    private ArrayList<GuideData> mResourceList = null;

    public GuideFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setGuideResourceList(ArrayList<GuideData> resourceList){
        this.mResourceList = resourceList;
    }

    @Override
    public Fragment getItem(int position) {
        boolean lastPageState = false;
        if(position == mResourceList.size()-1){
            lastPageState = true;
        }else{
            lastPageState = false;
        }

        Fragment fragment = GuidePagerFragment.newInstance(lastPageState, mResourceList.get(position));

        return fragment;
    }

    @Override
    public int getCount() {
        return mResourceList.size();
    }
}
