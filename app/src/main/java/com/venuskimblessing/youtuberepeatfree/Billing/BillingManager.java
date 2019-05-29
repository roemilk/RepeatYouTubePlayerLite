package com.venuskimblessing.youtuberepeatfree.Billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class BillingManager implements PurchasesUpdatedListener {
    private final String TAG = "BillingManager";
    public static final String SKU_PREMIUM = "premium_version";

    private Activity mActivity = null;
    private BillingClient mBillingClient = null;
    private SkuDetails mSkuDetails;
    private OnBuyCompleteListener mOnBuyCompleteListener;

    public interface OnBuyCompleteListener{
        void onBuySuccess(List<Purchase> purchases);
        void onBuyCancel(BillingResult billingResult);
        void onBuyError(BillingResult billingResult);
    }


    public BillingManager(Activity activity) {
        this.mActivity = activity;
    }

    public void setOnBuyCompleteListener(OnBuyCompleteListener listener){
        this.mOnBuyCompleteListener = listener;
    }

    //인앱 초기화
    public void initBilling() {
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished...");
                    queryInappProductDetails();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected...");
            }
        });
    }

    /**
     * 판매 상품 정보 조회
     */
    public void queryInappProductDetails() {
        Log.d(TAG, "queryInappProductDetails...");

        List<String> skuList = new ArrayList<String>();
        skuList.add("premium_version");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    for (SkuDetails details : skuDetailsList) {
                        mSkuDetails = details;
                    }
                    queryPurchases();
                }
            }
        });
    }

    /**
     * 아이템 구매
     */
    public void buyInapp() {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetails)
                .build();
        BillingResult billingResult = mBillingClient.launchBillingFlow(mActivity, flowParams);
    }

    /**
     * 보유 아이템 조회
     */
    public void queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchaseList = purchasesResult.getPurchasesList();
        boolean premiumState = false;
        for (Purchase purchase : purchaseList) {
            if(purchase.getSku().equals(SKU_PREMIUM)){
                premiumState = true;
                Toast.makeText(mActivity, "프리미엄 버전 사용자입니다.", Toast.LENGTH_SHORT).show();
            }
        }

        if(premiumState){
            SharedPreferencesUtils.setBoolean(mActivity, SKU_PREMIUM, true);
        }else{
            SharedPreferencesUtils.setBoolean(mActivity, SKU_PREMIUM, false);
        }
    }

    /**
     * 아이템 소모 처리
     * @param token
     */
    public void consumeItem(String token) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder().
                setPurchaseToken(token).
                build();

        ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(mActivity, "Item Consume Success...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Item Consume Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mBillingClient.consumeAsync(consumeParams, consumeResponseListener);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            mOnBuyCompleteListener.onBuySuccess(purchases);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            mOnBuyCompleteListener.onBuyCancel(billingResult);
        } else {
            // Handle any other error codes.
            mOnBuyCompleteListener.onBuyError(billingResult);
        }
    }
}
