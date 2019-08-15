package com.venuskimblessing.tuberepeatfree.Utils;

/**
 * Created by Kim YoungHun on 2016-05-16.
 * 미디어 작업에 관련된 유틸리티 모음입니다.
 */
public class MediaUtils {

    private MediaUtils() {}

    /**
     * iso 방식의 시간형식을 밀리초로 바꾼다.
     * @param timeString
     * @return
     */
    public static long getDuration(String timeString) {
        try{
            String time = timeString.substring(2);
            long duration = 0L;
            Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
            for(int i = 0; i < indexs.length; i++) {
                int index = time.indexOf((String) indexs[i][0]);
                if(index != -1) {
                    String value = time.substring(0, index);
                    duration += Integer.parseInt(value) * (int) indexs[i][1] * 1000;
                    time = time.substring(value.length() + 1);
                }
            }
            return duration;
        }catch(Exception e){ //간혹 Duration 형식이 이상하게 내려오는 문제로 NumberformatException이 발생하여 파싱이 불가하여 예외처리 (ex .. P1DT..)
            return 0;
        }
    }

    /**
     *밀리초를 시분초 형식으로 변환하여 리턴합니다.
     * @param milliSec
     */
    public static String getMillSecToHMS(int milliSec){

        int hour = ( milliSec / ( 1000 * 60 * 60 )) % 100;
        int minute = ( milliSec / ( 1000 * 60 )) % 60;
        int second = ( milliSec / 1000 ) % 60;

        String result = String.format("%02d:%02d:%02d", hour, minute, second);
        return result;
    }

    /**
     * 초를 시분초 형식으로 변환하여 리턴합니다.
     * @param sec
     * @return
     */
    public static String getSecToHMS(int sec){
        int hour = ( sec / ( 1 * 60 * 60 )) % 100;
        int minute = ( sec / ( 1 * 60 )) % 60;
        int second = ( sec / 1 ) % 60;

        String result = String.format("%02d:%02d:%02d", hour, minute, second);
        return result;
    }
}
