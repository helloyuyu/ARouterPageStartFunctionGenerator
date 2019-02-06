package com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String firstCharacterToLow(String srcStr) {
        if (srcStr == null) {
            return "";
        }
        if (srcStr.length() == 1) {
            return srcStr.toLowerCase();
        }
        return srcStr.substring(0, 1).toLowerCase() + srcStr.substring(1);
    }

    public static String firstCharacterToUp(String srcStr) {
        if (srcStr == null) {
            return "";
        }
        if (srcStr.length() == 1) {
            return srcStr.toUpperCase();
        }
        return srcStr.substring(0, 1).toUpperCase() + srcStr.substring(1);
    }

    private static Pattern sStartWithMPattern = Pattern.compile("^m[A-Z]");

    public static boolean isStartWithM(String str) {
        Matcher matcher = sStartWithMPattern.matcher(str);
        return matcher.find();
    }
}
