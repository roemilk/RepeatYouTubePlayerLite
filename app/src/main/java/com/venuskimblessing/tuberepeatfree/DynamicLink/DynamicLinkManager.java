package com.venuskimblessing.tuberepeatfree.DynamicLink;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.venuskimblessing.tuberepeatfree.Dialog.LoadingIndicator;
import com.venuskimblessing.tuberepeatfree.R;
import com.venuskimblessing.tuberepeatfree.Utils.MediaUtils;

import java.util.ArrayList;
import java.util.List;

public class DynamicLinkManager {
    private final String TAG = "DynamicLinkManager";

    private static final String DOMAIN_MAIN = "https://www.youtube.com/watch?v=";
    private static final String DOMAIN_MAIN_KR = "http://blessingvenus.com/link_kr.php?";
    private static final String DOMAIN_MAIN_EN = "http://blessingvenus.com/link_en.php?";

    private Activity mActivity;
    private LoadingIndicator mLoadingIndicator;

    public DynamicLinkManager(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 짧은 다이나믹 링크를 만듭니다.
     *
     * @param title
     * @param id
     * @param startTime
     * @param endTime
     */
    public void createShortDynamicLink(final String title, String id, final String startTime, final String endTime) {
        //loading
        mLoadingIndicator = new LoadingIndicator(mActivity, R.style.custom_dialog_fullScreen);
        mLoadingIndicator.startLoadingView();

        String domain = DOMAIN_MAIN + id + "&";
//        String language = OSUtils.getLocaleLanguage(mActivity);
//        Log.d(TAG, "createShortDynamicLink language : " + language);
//
//        if(language.equals(LANGUAGE_EN)){
//            domain = DOMAIN_MAIN_EN;
//        }else if(language.equals(LANGUAGE_KR)){
//            domain = DOMAIN_MAIN_KR;
//        }else{
//            domain = DOMAIN_MAIN_EN;
//        }

        Log.d(TAG, "domain language result : " + domain);

        StringBuffer sb = new StringBuffer();
        sb.append(domain);
        sb.append("id=" + id);
        sb.append("&");
        sb.append("starttime=" + startTime);
        sb.append("&");
        sb.append("endtime=" + endTime);
        String link = sb.toString();
        Log.d(TAG, "link url : " + link);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://tuberepeatfree.page.link/")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder()
                                .setMinimumVersion(1).build()) //설치된 앱의 버전이 4버전 이하일 경우는 설치페이지로 보냄 4 밑의 버전은 다이나믹링크가 구현되어 있지 않아 동작하지 않기 때문임..
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
     *
     * @param dynamicLinkUrl
     * @param title
     * @param startTime
     * @param endTime
     */
    private void shareLink(String dynamicLinkUrl, String title, String startTime, String endTime) {
        String convertStartTime = MediaUtils.getMillSecToHMS(Integer.parseInt(startTime));
        String convertEndTime = MediaUtils.getMillSecToHMS(Integer.parseInt(endTime));

        String shareHint = mActivity.getString(R.string.range_share_hint) + "\n" + convertStartTime + " - " + convertEndTime;
        String shareText = mActivity.getString(R.string.range_share_text);
        String shareResultText = shareText + "\n\n" + title + "\n\n" + convertStartTime + " - " + convertEndTime + "\n\n" + dynamicLinkUrl;

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, shareResultText);

        Intent chooser = Intent.createChooser(intent, shareHint);
        mActivity.startActivity(chooser);

//        Intent reciver = new Intent(mActivity, ShareAppReciver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity, 0, reciver, PendingIntent.FLAG_UPDATE_CURRENT);

//        List<Intent> targetList = getTargetList(shareResultText);
//        Intent chooser = null;
//        if (Build.VERSION.SDK_INT >= 22) {
//            chooser = Intent.createChooser(intent, shareHint, pendingIntent.getIntentSender());
//        } else {
//            chooser = Intent.createChooser(targetList.remove(0), shareHint);
//        }

//        Log.d(TAG, "targetList : " + targetList);
//
//        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetList.toArray(new Parcelable[]{}));
//        mActivity.startActivity(chooser);
        mLoadingIndicator.stopLoadingView();
    }

    private List getTargetList(String text) {
        String subject = "메시지 제목";
        List targetedShareIntents = new ArrayList<>();

        // 페이스북
        Intent facebookIntent = getShareIntent("facebook", subject, text);
        if (facebookIntent != null)
            targetedShareIntents.add(facebookIntent);

        // 트위터
        Intent twitterIntent = getShareIntent("twitter", subject, text);
        if (twitterIntent != null)
            targetedShareIntents.add(twitterIntent);

        // 구글 플러스
        Intent googlePlusIntent = getShareIntent("com.google.android.apps.plus", subject, text);
        if (googlePlusIntent != null)
            targetedShareIntents.add(googlePlusIntent);

        // Gmail
        Intent gmailIntent = getShareIntent("gmail", subject, text);
        if (gmailIntent != null)
            targetedShareIntents.add(gmailIntent);

        Intent kakaoIntent = getShareIntent("kakao", subject, text);
        if (kakaoIntent != null)
            targetedShareIntents.add(kakaoIntent);

        Intent snapchatIntent = getShareIntent("snapchat", subject, text);
        if (snapchatIntent != null)
            targetedShareIntents.add(snapchatIntent);

        return targetedShareIntents;
    }

    /**
     * @param name    패키지나 앱 이름
     * @param subject 제목
     * @param text    내용
     * @return
     */
    private Intent getShareIntent(String name, String subject, String text) {
        boolean found = false;

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfos = mActivity.getPackageManager().queryIntentActivities(intent, 0);

        if (resInfos == null || resInfos.size() == 0)
            return null;

        for (ResolveInfo info : resInfos) {
            if (info.activityInfo.packageName.toLowerCase().contains(name) ||
                    info.activityInfo.name.toLowerCase().contains(name)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                intent.setPackage(info.activityInfo.packageName);
                found = true;
                break;
            }
        }

        if (found)
            return intent;

        return null;
    }
}
