package com.venuskimblessing.youtuberepeatlite.Utils;

import java.text.DecimalFormat;

public class FormatUtils {

    /**
     *
     * 요약 : 숫자를 3자리 콤마표시하여 리턴한다.
     * @return
     */
    public static String parseNumberFormat(String s) {
        int inValues = Integer.parseInt(s);
        DecimalFormat Commas = new DecimalFormat("#,###");
        String result = (String)Commas.format(inValues);
        return result;
    }
}
