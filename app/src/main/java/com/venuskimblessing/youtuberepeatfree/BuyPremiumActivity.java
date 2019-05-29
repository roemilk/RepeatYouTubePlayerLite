package com.venuskimblessing.youtuberepeatfree;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.andexert.library.RippleView;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.venuskimblessing.youtuberepeatfree.Billing.BillingManager;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;

import java.util.List;

public class BuyPremiumActivity extends Activity implements View.OnClickListener, BillingManager.OnBuyCompleteListener {
    private final String TAG = "BuyPremiumActivity";

    private RippleView mBuyRippleView;
    private BillingManager mBillingManager;
    private SoftKeybordManager mSoftKeybordManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_buy_premium);

        mBillingManager = new BillingManager(this);
        mBillingManager.setOnBuyCompleteListener(this);
        mBillingManager.initBilling();

        mBuyRippleView = (RippleView)findViewById(R.id.premium_buyRippleView_buy_button);
        mBuyRippleView.setOnClickListener(this);
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
        mBillingManager.buyInapp();
    }

    @Override
    public void onBuySuccess(List<Purchase> purchases) {
        for(Purchase purchase : purchases){
            String sku = purchase.getSku();
            if(sku != null){
                if(sku.equals(BillingManager.SKU_PREMIUM)){
                    Toast.makeText(this, getString(R.string.buy_success), Toast.LENGTH_SHORT).show();
                    SharedPreferencesUtils.setBoolean(this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION, true);
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
}
