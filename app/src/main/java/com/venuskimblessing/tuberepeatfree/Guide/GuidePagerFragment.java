package com.venuskimblessing.tuberepeatfree.Guide;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dd.morphingbutton.MorphingButton;
import com.venuskimblessing.tuberepeatfree.GuideActivity;
import com.venuskimblessing.tuberepeatfree.R;

public class GuidePagerFragment extends Fragment {
    public static final String TAG = "GuidePagerFragment";

    private boolean lastPageState = false;
    private int res = 0;
    private String description = "";
    private GuideActivity mActivity = null;

    public static GuidePagerFragment newInstance(boolean lastPageState, GuideData data) {
        GuidePagerFragment fragment = new GuidePagerFragment();
        Bundle args = new Bundle();
        args.putBoolean("last", lastPageState);
        args.putInt("res", data.getResourceImage());
        args.putString("description", data.getDescription());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.lastPageState = getArguments().getBoolean("last");
        this.res = getArguments().getInt("res");
        this.description = getArguments().getString("description");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (GuideActivity)context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_pager_fragment, container, false);
        RelativeLayout lastLay = (RelativeLayout) rootView.findViewById(R.id.guide_last_lay);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.guide_pager_imageView);
        TextView textView = (TextView) rootView.findViewById(R.id.guide_pager_textView);

        MorphingButton button = (MorphingButton) rootView.findViewById(R.id.guide_pager_button);
        button.setOnClickListener(mActivity);

        if(lastPageState){
            lastLay.setVisibility(View.VISIBLE);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity()).load(res)
//                    .apply(bitmapTransform(new BlurTransformation(3, 3)))
                    .into(imageView);
        }else{
            lastLay.setVisibility(View.GONE);
            Glide.with(getActivity()).load(res).into(imageView);
        }
        textView.setText(description);
        return rootView;
    }
}
