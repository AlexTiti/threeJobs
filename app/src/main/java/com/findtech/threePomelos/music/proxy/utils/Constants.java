package com.findtech.threePomelos.music.proxy.utils;

import android.os.Environment;

/**
 * @author Alex
 */
public class Constants {
    /**
     * SD卡路径
     */
    public static final String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    /**
     * 缓存保存路径
     */
    public static final String DOWNLOAD_PATH = SD_PATH + "MusicPlayerTest/BufferFiles/";
    /**
     * SD卡预留最小值
     */
    public static final int SD_REMAIN_SIZE = 5 * 1024 * 1024;
    public static final int SD_REMAIN_SIZE_MIN = 5 * 1024 * 1024;
    /**
     * 单次缓存文件最大值
     */
    public static final int AUDIO_BUFFER_MAX_LENGTH = 6 * 1024 * 1024;
    /**
     * 缓存文件个数最大值
     */
    public static final int CACHE_FILE_NUMBER = 30;
    public final static String RANGE = "Range";
    public final static String HOST = "Host";
    public final static String RANGE_PARAMS = "bytes=";
    public final static String RANGE_PARAMS_0 = "bytes=0-";
    public final static String CONTENT_RANGE_PARAMS = "bytes ";
    public final static String LINE_BREAK = "\r\n";
    public final static String HTTP_END = LINE_BREAK + LINE_BREAK;
}
