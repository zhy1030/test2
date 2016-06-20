package com.example.hongyu.myapplication;

import android.util.Log;

/**
 * Created by hongyu on 16-6-19.
 */
public class PlayerBetResult {
    Integer mZhuang = 0;
    Integer mXian = 0;
    Integer mHe = 0;
    Integer mZhuangDui = 0;
    Integer mXianDui = 0;

    public PlayerBetResult (String string) {
        String[] firstSplit = string.replace(" ", "").split(";");
        for (int i = 0; i < firstSplit.length; i++) {
            Log.d("hongyu", "split i " + i + " is " + firstSplit[i]);
            String[] secondSplit  = firstSplit[i].split(":");
            switch (secondSplit[0]) {
                case "庄":
                    mZhuang = Integer.parseInt(secondSplit[1]);
                    break;
                case "闲":
                    mXian = Integer.parseInt(secondSplit[1]);
                    break;
                case "和":
                    mHe = Integer.parseInt(secondSplit[1]);
                    break;
                case "庄对":
                    mZhuangDui = Integer.parseInt(secondSplit[1]);
                    break;
                case "闲对":
                    mXianDui = Integer.parseInt(secondSplit[1]);
                    break;
                default:
            }
        }
    }
}
