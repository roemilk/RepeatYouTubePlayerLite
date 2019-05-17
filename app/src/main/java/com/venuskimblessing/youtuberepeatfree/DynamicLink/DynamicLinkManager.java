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
import com.venuskimblessing.youtuberepeatfree.Utils.MediaUtils;

public class DynamicLinkManager {
    private final String TAG = "DynamicLinkManager";

    private Activity mActivity;

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
                        .setMinimumVersion(4).build())
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

        String shareTitle = "최고의 장면을 친구에게 보여주기";

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "꼭 함께 보고 싶은 최고의 장면! \n\n" + title + "\n" + convertStartTime + " - " + convertEndTime + "\n\n" + dynamicLinkUrl);
        Intent chooser = Intent.createChooser(intent, shareTitle);
        mActivity.startActivity(chooser);
    }
}
