package com.findtech.threePomelos.music.proxy.utils;

import java.net.HttpURLConnection;
import java.net.URLConnection;

public class HttpUtils {

    private static final String LOG_TAG = HttpUtils.class.getSimpleName();
    /**
     * 生成返回MediaPlayer的Response Header
     *
     * @param rangeStart
     * @param rangeEnd
     * @param fileLength
     * @return
     */
    public static String genResponseHeader(int rangeStart, int rangeEnd, int fileLength) {
        StringBuffer sb = new StringBuffer();
        sb.append("HTTP/1.1 206 Partial Content").append("\n");
        sb.append("Content-Type: audio/mpeg").append("\n");
        sb.append("Content-Length: ").append(rangeEnd - rangeStart + 1).append("\n");
        sb.append("Connection: keep-alive").append("\n");
        sb.append("Accept-Ranges: bytes").append("\n");
        String contentRangeValue = String.format(Constants.CONTENT_RANGE_PARAMS + "%d-%d/%d", rangeStart, rangeEnd,
                fileLength);
        sb.append("Content-Range: ").append(contentRangeValue).append("\n");
        sb.append("\n");
        return sb.toString();
    }


    /**
     * 发送请求,得到Response
     *
     * @return
     */
    public static HttpURLConnection send(URLConnection request) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) request;
        httpURLConnection.setConnectTimeout(20000);
        httpURLConnection.setReadTimeout(60000);
        return httpURLConnection;
    }


}
