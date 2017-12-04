package com.findtech.threePomelos.music.proxy.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;


import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.proxy.db.CacheFileInfoDao;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;

public class RequestDealThread extends Thread {
    private static final String LOG_TAG = RequestDealThread.class.getSimpleName();

    Socket client;
    HttpURLConnection request;

    ProxyFileUtils fileUtils;
    /**
     * MediaPlayer发出的原始请求Range Start
     */
    private int originRangeStart;
    /**
     * 和本地缓存判断后，需要发出的请求Range Start
     */
    private long realRangeStart;
    CacheFileInfoDao cacheDao;
    Context mContext;

    public RequestDealThread(Context context, HttpURLConnection request, Socket client) {
        this.request = request;
        this.client = client;
        cacheDao = CacheFileInfoDao.getInstance();
        mContext = context;
    }

    @Override
    public void run() {
        try {
            fileUtils = ProxyFileUtils.getInstance(mContext, request.getURL().toURI(), true);
            processRequest(request, client);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private int getRangeStart(HttpURLConnection request) {
        String value = request.getRequestProperty(Constants.RANGE);
        //	Header rangeHeader = request.getFirstHeader(Constants.RANGE);
        if (value != null) {
            return Integer.valueOf(value.substring(value.indexOf("bytes=") + 6, value.indexOf("-")));
        }
        return 0;
    }

    /**
     * 伪造Response Header，发送缓存内容
     *
     * @param rangeStart 数据起始位置（如果从头开始则为0）
     * @param rangeEnd   数据截止位置（一般为缓存长度-1）
     * @param fileLength 缓存文件长度
     * @param audioCache 缓存内容
     * @throws IOException
     */
    private void sendLocalHeaderAndCache(int rangeStart, int rangeEnd, int fileLength, byte[] audioCache)
            throws IOException {
        // 返回MediaPlayer Header信息
        String httpString = HttpUtils.genResponseHeader(rangeStart, rangeEnd, fileLength);
        byte[] httpHeader = httpString.toString().getBytes();
        client.getOutputStream().write(httpHeader, 0, httpHeader.length);
        // 返回Content
        if (audioCache != null && audioCache.length > 0) {
            client.getOutputStream().write(audioCache, 0, audioCache.length);
        }
    }

    private void processRequest(HttpURLConnection request, Socket client) throws IllegalStateException, IOException {
        if (request == null) {
            return;
        }
        try {
            byte[] audioCache = null;
            // 得到MediaPlayer原始请求Range起始
            originRangeStart = getRangeStart(request);
            // 缓存的文件大小
            int cacheFileSize = cacheDao.getFileSize(fileUtils.getFileName());
            L.e(LOG_TAG, "原始请求Range起始值：" + originRangeStart + " 本地缓存长度：" + fileUtils.getLength()+"=="+cacheFileSize);
            /*
             * 如果缓存完成，无需发送请求，本地缓存返回MediaPlayer。
			 */
            if (fileUtils.isEnable() && (fileUtils.getLength() == cacheFileSize || fileUtils.getLength() -500 >= cacheFileSize )) {
                L.e(LOG_TAG, "如果缓存完成，无需发送请求");

                cacheFileSize = fileUtils.getLength();
                audioCache = fileUtils.read(originRangeStart, Constants.AUDIO_BUFFER_MAX_LENGTH);
                sendLocalHeaderAndCache(originRangeStart, cacheFileSize - 1, cacheFileSize, audioCache);

                return;
            }
            if (!NetUtils.isConnectInternet(mContext)){
                Looper.prepare();
                ToastUtil.showToast(mContext,mContext.getResources().getString(R.string.net_exception));
                Looper.loop();
            }
            /*
             * 请求Range起始值和本地缓存比对。如果有缓存，得到缓存内容，修改Range。 如果没有缓存，则Range不变。
			 */
            if (fileUtils.isEnable() && originRangeStart < fileUtils.getLength()) {
                audioCache = fileUtils.read(originRangeStart, Constants.AUDIO_BUFFER_MAX_LENGTH);
                L.e(LOG_TAG, Constants.AUDIO_BUFFER_MAX_LENGTH+"本地已缓存长度（跳过）:" + audioCache.length);
                // 得到需要发送请求Range Start（本地缓存结尾位置+1=缓存长度）
                realRangeStart = fileUtils.getLength();
                // 替换请求Header
                request.setRequestProperty(Constants.RANGE, Constants.RANGE_PARAMS + realRangeStart + "-");

            } else {
                realRangeStart = originRangeStart;
            }
            // 缓存是否已经到最大值（如果缓存已经到最大值，则只需要返回缓存）
            boolean isCacheEnough = (audioCache != null && audioCache.length == Constants.AUDIO_BUFFER_MAX_LENGTH) ? true
                    : false;

			/*
             * 如果缓存足够，且本地有文件长度，则直接发送缓存,不发送请求。。。。。。。。。。。。。。。。。。。
			 * 如果缓存足够，本地没有文件长度，则发送请求，使用ResponseHeader，返回缓存,!不接收ResponseContent
			 * 如果缓存不足，则发送请求，使用ResponseHeader，返回缓存，!返回Response Content
			 */
            // 缓存足够&&有文件大小
            if (isCacheEnough && cacheFileSize > 0) {
                L.e(LOG_TAG, "缓存足够&&有文件大小");
                sendLocalHeaderAndCache(originRangeStart, cacheFileSize - 1, cacheFileSize, audioCache);
            }
            // 缓存不够。或者数据库没有文件大小
            else {
                HttpURLConnection realResponse = null;
                /*
				 * 返回Header和Cache
				 */
                // 如果数据库没有存文件大小，则获取（处理数据库没有文件大小的情况）
                if (cacheFileSize <= 0) {

                    realResponse = HttpUtils.send(request);
                    if (realResponse == null) {
                        return;
                    }
                    cacheFileSize = getContentLength(realResponse);

                }
                sendLocalHeaderAndCache(originRangeStart, cacheFileSize - 1, cacheFileSize, audioCache);

                if (realResponse == null) {
                    cacheDao.delete(fileUtils.getFileName());
                    realResponse = HttpUtils.send(request);

                    if (realResponse == null) {
                        L.e(LOG_TAG, "二次缓存不足，发送请求");
                        return;
                    }

                }
                L.e(LOG_TAG, "接收ResponseContent");
                InputStream data = realResponse.getInputStream();
                L.e("isCacheEnough","================"+isCacheEnough);
                if (!isCacheEnough) {
                    byte[] buff = new byte[1024 * 40];
                    boolean isPrint = true;
                    int fileLength = 0;
                    int readBytes;
                    while (Thread.currentThread() == MediaPlayerProxy.downloadThread
                            && (readBytes = data.read(buff, 0, buff.length)) != -1) {
                        long fileBufferLocation = fileLength + realRangeStart;
                        fileLength += readBytes;
                        long fileBufferEndLocation = fileLength + realRangeStart;
                        if (fileUtils.getLength() == fileBufferLocation) {
                            fileUtils.write(buff, readBytes);
                        }
                        if (System.currentTimeMillis() / 1000 % 2 == 0) {
                            if (isPrint) {
                                L.e("RequestDealThread====", "Cache Size:" + readBytes + " File Start:" + fileBufferLocation
                                        + "File End:" + fileBufferEndLocation);
                                isPrint = false;
                            }
                        } else {
                            isPrint = true;
                        }
                        client.getOutputStream().write(buff, 0, readBytes);
                    }
                }
            }
        } catch (SocketException e) {
            L.e(LOG_TAG, "连接被终止"+e.toString());
        } catch (Exception e) {
            L.e(LOG_TAG, e.getMessage());
        } finally {
            client.close();
            L.e(LOG_TAG, "代理关闭");
        }
    }

    /**
     * 得到Content大小
     *
     * @param response
     * @return
     */
    private int getContentLength(HttpURLConnection response) {
        int contentLength = 0;
        String range = request.getHeaderField(Constants.RANGE);
        if (range != null) {
            contentLength = Integer.valueOf(range.substring(range.indexOf("-") + 1, range.indexOf("/"))) + 1;
        } else {
            contentLength = request.getContentLength();
        }
        if (contentLength != 0) {
            cacheDao.insertOrUpdate(fileUtils.getFileName(), contentLength);
        }
        return contentLength;
    }
}
