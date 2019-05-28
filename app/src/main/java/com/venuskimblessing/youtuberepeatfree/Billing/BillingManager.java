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

    private final String SKU_PREMIUM = "premium_version";

    private Activity mActivity = null;

    //인앱결제
    private BillingClient mBillingClient = null;
    private SkuDetails mSkuDetails;

    public BillingManager(Activity activity) {
        this.mActivity = activity;
    }

    //인앱 초기화
    public void initBilling() {
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    Toast.makeText(SearchActivity.this, "Billing Setup Finished Success...", Toast.LENGTH_SHORT).show();
                    queryInappProductDetails();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    /**
     * 판매 상품 정보 조회
     */
    public void queryInappProductDetails() {
        List<String> skuList = new ArrayList<String>();
        skuList.add("premium_version");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    for (SkuDetails details : skuDetailsList) {
                        Log.d(TAG, "test details : " + details.getSku());
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

        Log.d(TAG, "queryPurchases list size : " + purchaseList.size());

        boolean premiumState = false;
        for (Purchase purchase : purchaseList) {
            if(purchase.getSku().equals(SKU_PREMIUM)){
                premiumState = true;
                Toast.makeText(mActivity, "프리미엄 버전 사용자입니다. state code : " + purchase.getPurchaseState(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mActivity, "아이템 소모 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "아이템 소모 실패", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mBillingClient.consumeAsync(consumeParams, consumeResponseListener);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
                String sku = purchase.getSku();
                Log.d(TAG, "onPurchasesUpdated purchase sku.. : " + sku);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(mActivity, "onPurchasesUpdated 구매를 취소하였습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onPurchasesUpdated Error : " + billingResult.getResponseCode());
            // Handle any other error codes.
        }
    }
}
