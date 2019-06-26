package com.venuskimblessing.youtuberepeatfree;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.andexert.library.RippleView;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.venuskimblessing.youtuberepeatfree.Billing.BillingManager;
import com.venuskimblessing.youtuberepeatfree.Common.CommonConfig;
import com.venuskimblessing.youtuberepeatfree.Common.CommonInApp;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatfree.FirebaseUtils.LogUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;

import java.util.List;

public class BuyPremiumActivity extends Activity implements View.OnClickListener, BillingManager.OnBuyCompleteListener, BillingManager.OnQueryInventoryItemListener {
    private final String TAG = "BuyPremiumActivity";

//    private TextView mPriceTextView;
    private RippleView mBuyRippleView;
    private Button mBuyButton;
    private BillingManager mBillingManager;
    private SoftKeybordManager mSoftKeybordManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_buy_premium);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, "PlayerActivity");
        LogUtils.logEvent(this, FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mBillingManager = new BillingManager(this);
        mBillingManager.setOnBuyCompleteListener(this);
        mBillingManager.setOnQueryInventoryItemListener(this);
        mBillingManager.initBillingQueryInventoryItem();

//        mPriceTextView = (TextView)findViewById(R.id.premium_buy_price_textView);
//        mPriceTextView.setText(CommonInApp.sPremiumPrice);
//        mPriceTextView.setText("1개월 무료 체험하기");

        mBuyRippleView = (RippleView)findViewById(R.id.premium_buyRippleView_buy_button);
        mBuyRippleView.setOnClickListener(this);

        mBuyButton = (Button)findViewById(R.id.premium_buy_button);
        mBuyButton.setOnClickListener(this);

        if(CommonConfig.sConfigEventShow){
            mBuyButton.setText(getString(R.string.premium_upgrade_sale));
        }else{
            mBuyButton.setText(getString(R.string.premium_upgrade));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mSoftKeybordManager = new SoftKeybordManager(getWindow());
            mSoftKeybordManager.hideSystemUI();
        }

    }

    @Override
    public void onClick(View v) {
        LogUtils.logEvent(this, "buy_premium", null);
        if(CommonUserData.sPremiumState){
            Toast.makeText(this, getString(R.string.buy_already_have_premium), Toast.LENGTH_SHORT).show();

//            //테스트 소모 코드
//            mBillingManager.consumeItem(mBillingManager.getPremiumPurchase().getPurchaseToken());

            return;
        }else{
//            mBillingManager.buyInapp();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.venuskimblessing.youtuberepeat"));
            startActivity(intent);
        }
    }

    @Override
    public void onBuySuccess(List<Purchase> purchases) {
        for(Purchase purchase : purchases){
            String sku = purchase.getSku();
            if(sku != null){
                if(sku.equals(BillingManager.SKU_PREMIUM)){
                    Toast.makeText(this, getString(R.string.buy_success), Toast.LENGTH_SHORT).show();
                    CommonUserData.sPremiumState = true;
                    SharedPreferencesUtils.setBoolean(this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION, true);
                    finish();
                }
            }
        }
    }

    @Override
    public void onBuyCancel(BillingResult billingResult) {
        Toast.makeText(this, getString(R.string.buy_cancel), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBuyError(BillingResult billingResult) {
        if(billingResult != null){
            int errCode = billingResult.getResponseCode();
            Toast.makeText(this, getString(R.string.buy_error) + errCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPremiumVersionUser() {
        CommonUserData.sPremiumState = true;
        SharedPreferencesUtils.setBoolean(this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION, true);
    }

    @Override
    public void onFreeVersionUser() {
        CommonUserData.sPremiumState = false;
        SharedPreferencesUtils.setBoolean(this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION, false);
    }
}
