package com.venuskimblessing.tuberepeatfree.Common;

public class CommonConfig {
    public static final String KEY_REWARD_REMOVEALLAD = "reward_removeallad";
    public static final String KEY_FACEBOOK_SHARE = "facebook_share";
    public static final String KEY_REQUIRED_VERSION = "required_version_code";
    public static final String KEY_EVENT_SHOW = "event";
    public static final String KEY_PIP = "pip";

    public static boolean sPip = true; //pip 기능
    public static boolean sConfigRewardRemoveAllAdSate = false; //광고 제거 리워드 노출여부
    public static boolean sConfigFacebookShareState = false; //페이스북 친구 공유 노출여부
    public static int sConfigRequiredVersionCode = 0; //필수(최소) 업데이트 대상 버전코드
    public static boolean sConfigEventShow = false; //이벤트 노출 여부
}
