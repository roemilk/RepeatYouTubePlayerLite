package com.venuskimblessing.youtuberepeatfree.DynamicLink;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.venuskimblessing.youtuberepeatfree.Dialog.LoadingIndicator;
import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.Utils.MediaUtils;
import com.wang.avi.AVLoadingIndicatorView;

public class DynamicLinkManager {
    private final String TAG = "DynamicLinkManager";

    private Activity mActivity;
    private LoadingIndicator mLoadingIndicator;

    public DynamicLinkManager(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 짧은 다이나믹 링크를 만듭니다.
     * @param title
     * @param id
     * @param startTime
     * @param endTime
     */
    public void createShortDynamicLink(final String title, String id, final String startTime, final String endTime) {
        //loading
        mLoadingIndicator = new LoadingIndicator(mActivity, R.style.custom_dialog_fullScreen);
        mLoadingIndicator.startLoadingView();

        StringBuffer sb = new StringBuffer();
        sb.append("https://youtubeplayerfree.com?");
        sb.append("id=" + id);
        sb.append("&");
        sb.append("starttime=" + startTime);
        sb.append("&");
        sb.append("endtime=" + endTime);
        String link = sb.toString();
        Log.d(TAG, "link url : " + link);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://youtuberepeatfree.page.link/")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(4).build()) //설치된 앱의 버전이 4버전 이하일 경우는 설치페이지로 보냄 4 밑의 버전은 다이나믹링크가 구현되어 있지 않아 동작하지 않기 때문임..
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(mActivity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.d(TAG, "short url : " + shortLink.toString());
                            Log.d(TAG, "flowchartLink url : " + flowchartLink.toString());

                            String shortLinkUrl = shortLink.toString();
                            shareLink(shortLinkUrl, title, startTime, endTime);
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

    /**
     * 공유하기
     * @param dynamicLinkUrl
     * @param title
     * @param startTime
     * @param endTime
     */
    private void shareLink(String dynamicLinkUrl, String title, String startTime, String endTime){
        String convertStartTime = MediaUtils.getMillSecToHMS(Integer.parseInt(startTime));
        String convertEndTime = MediaUtils.getMillSecToHMS(Integer.parseInt(endTime));

        String shareHint = mActivity.getString(R.string.range_share_hint) + "\n" + convertStartTime + " - " + convertEndTime;
        String shareText = mActivity.getString(R.string.range_share_text);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText + "\n\n" + title + "\n\n" + convertStartTime + " - " + convertEndTime + "\n\n" + dynamicLinkUrl);
        Intent chooser = Intent.createChooser(intent, shareHint);
        mActivity.startActivity(chooser);

        mLoadingIndicator.stopLoadingView();
    }
}
