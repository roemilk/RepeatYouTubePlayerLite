package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.venuskimblessing.youtuberepeatlite.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatlite.GuideActivity;
import com.venuskimblessing.youtuberepeatlite.R;
import com.venuskimblessing.youtuberepeatlite.Utils.OSUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

public class DialogHelp extends Dialog implements View.OnClickListener {
    public static final String TAG = "DialogHelp";

    private Context mContext = null;
    private LinearLayout mHelpMenuLay, mLicenseLay;
    private Button mProButton, mGuideButton, mLisenceButton;
    private TextView mVersionTextView, mVersionItemTextView;
    private TextView mExpandableButton0, mExpandableButton1, mExpandableButton2, mExpandableVersionButton;
    private ExpandableLayout mExpandableLay0, mExpandableLay1, mExpandableLay2, mExpandableVersionLay;

    public DialogHelp(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogHelp(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogHelp(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_help);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mHelpMenuLay = (LinearLayout)findViewById(R.id.help_menu_parent_lay);
        mLicenseLay = (LinearLayout)findViewById(R.id.help_license_parent_lay);
        mProButton = (Button)findViewById(R.id.help_pro_button);
        mGuideButton = (Button)findViewById(R.id.help_guide_button);
        mLisenceButton = (Button)findViewById(R.id.help_license_button);
        mVersionTextView = (TextView)findViewById(R.id.help_version_textView);
        mVersionItemTextView = (TextView)findViewById(R.id.help_version_item_textView);

        mExpandableButton0 = (TextView)findViewById(R.id.dialog_license_expand_button_0);
        mExpandableLay0 = (ExpandableLayout)findViewById(R.id.dialog_license_expandable_layout_0);
        mExpandableButton0.setOnClickListener(onClickListenerExpandableLayout);

        mExpandableButton1 = (TextView)findViewById(R.id.dialog_license_expand_button_1);
        mExpandableLay1 = (ExpandableLayout)findViewById(R.id.dialog_license_expandable_layout_1);
        mExpandableButton1.setOnClickListener(onClickListenerExpandableLayout);

        mExpandableButton2 = (TextView)findViewById(R.id.dialog_license_expand_button_2);
        mExpandableLay2 = (ExpandableLayout)findViewById(R.id.dialog_license_expandable_layout_2);
        mExpandableButton2.setOnClickListener(onClickListenerExpandableLayout);

        mExpandableVersionButton = (TextView)findViewById(R.id.dialog_help_version_button);
        mExpandableVersionLay = (ExpandableLayout)findViewById(R.id.dialog_help_version_expandable_lay);
        mExpandableVersionButton.setOnClickListener(onClickListenerExpandableLayout);

        mProButton.setOnClickListener(this);
        mGuideButton.setOnClickListener(this);
        mLisenceButton.setOnClickListener(this);

        PackageInfo packageInfo = OSUtils.getPackageInfo(mContext);

//        String itemString = null;
//        if(CommonUserData.sUserItemState.equals(CommonUserData.ITEM_FREE)){
//            itemString = mContext.getString(R.string.dialog_help_version_no_item);
//        }else{
//            itemString = mContext.getString(R.string.dialog_help_version_yes_item);
//        }
//        mVersionItemTextView.setText(itemString);
        mVersionTextView.setText("Ver " + packageInfo.versionName + "-" + packageInfo.versionCode);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.help_guide_button:
                intent = new Intent(mContext, GuideActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.help_license_button:
                mHelpMenuLay.setVisibility(View.GONE);
                mLicenseLay.setVisibility(View.VISIBLE);
                break;
            case R.id.help_pro_button:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.venuskimblessing.youtuberepeat"));
                mContext.startActivity(intent);
                break;
        }
    }

    View.OnClickListener onClickListenerExpandableLayout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.dialog_license_expand_button_0:
                    if (mExpandableLay0.isExpanded()) {
                        mExpandableLay0.collapse();
                    } else {
                        mExpandableLay0.expand(true);
                    }
                    break;

                case R.id.dialog_license_expand_button_1:
                    if (mExpandableLay1.isExpanded()) {
                        mExpandableLay1.collapse();
                    } else {
                        mExpandableLay1.expand(true);
                    }
                    break;

                case R.id.dialog_license_expand_button_2:
                    if (mExpandableLay2.isExpanded()) {
                        mExpandableLay2.collapse();
                    } else {
                        mExpandableLay2.expand(true);
                    }
                    break;

                case R.id.dialog_help_version_button:
                    if (mExpandableVersionLay.isExpanded()) {
                        mExpandableVersionLay.collapse();
                    } else {
                        mExpandableVersionLay.expand(true);
                    }
                    break;
            }
        }
    };
}
