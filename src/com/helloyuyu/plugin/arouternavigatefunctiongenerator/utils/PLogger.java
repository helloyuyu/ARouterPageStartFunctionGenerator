package com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils;

import com.intellij.openapi.diagnostic.Logger;

/**
 * 日志打印
 * @author  xjs
 */
public class PLogger {

    private static final Logger log = Logger.getInstance(PLogger.class);

    private static PLogger sInstance = new PLogger();

    public static PLogger getInstance() {
        return sInstance;
    }

    public void logI(String s) {
        log.info(s);
    }

    public void logE(String s, Throwable throwable) {
        log.error(s, throwable);
    }

    public void logE(String s) {
        log.error(s);
    }
}
