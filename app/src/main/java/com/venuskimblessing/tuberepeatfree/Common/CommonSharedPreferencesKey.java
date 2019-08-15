package com.venuskimblessing.tuberepeatfree.Common;

public class CommonSharedPreferencesKey {
    public static final String KEY_GUIDE = "guide";
    public static final String KEY_INVITATION = "invitation"; //친구 초대 했는지 유무
    public static final String KEY_INVITATION_COUTN = "invitationcount"; //친구를 초대한 횟수
    public static final String KEY_AUTOPLAY = "autoplay";
    public static final String KEY_ALLREPEAT = "allrepeat";

    //프리미엄 버전
    public static final String KEY_PREMIUM_VERSION = "premium_version"; //프리미엄 버전 업그레이드

    //일부 보상형 기능 잠금해제
    public static final String KEY_FEATURE_SHARE_UNLOCK = "feature_unlock"; // 페이스북 앱 소개로 잠금 해제
    public static final String KEY_FEATURE_REWARD_UNLOCK = "reward_unlock"; // 보상형 광고 시청으로 잠금 해제
    public static final String KEY_FEATURE_REWARD_UNLOCK_MUSIC_MODE_COUNT = "MUSIC_COUNT";
}
