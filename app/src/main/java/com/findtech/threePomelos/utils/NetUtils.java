package com.findtech.threePomelos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/19
 *   desc     :
 *   version  :   V 1.0.5
 */
public class NetUtils {

    public static boolean isConnectInternet(final Context pContext) {
        final ConnectivityManager conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }

        return false;
    }


}
