package com.venuskimblessing.youtuberepeatfree.Common;

public class CommonUserData {

    //재생 횟수
    public static final int COUNT_DEFAULT = 99;
    public static final int COUNT_MAX = 99;
    public static final int REWORD_ADD_COUNT = 10; //보상 카운드 증가수
    public static int sMaxRepeatCount = COUNT_DEFAULT;

    //플레이리스트
    public static final int PLAYLIST_LIMIT_COUNT = 20; //무료 사용자 플레이리스트 추가 최대개수

    //보상광고 카운트
    public static final int REWARD_SCREENLOCK_COUNT = 2;
    public static int sRewardCount = 0;

    //광고 카운트
    public static int sAdCount = 0;
    public static final int AD_DEALY_COUNT = 2; //2번에 한번씩 노출

    //친구초대 카운트
    public static final int INVITE_COUNT_COMPLETE = 10; //친구초대 완료 횟수 (사용하지 않음)

    //프리미엄
    public static boolean sPremiumState = false;

    //프리패스 광고
    public static boolean sRemoveAllAd = false; //보상형 광고로 얻은 프리패스 광고
    public static boolean sRewardUnlockedFeatureBatterSaving = false;
    //기능 부분 일시적 잠금해제
    public static final int MUSIC_MODE_LIMITED_COUNT = 10;
    public static final int SHUFFLE_MODE_LIMITED_COUNT = 10;
}
