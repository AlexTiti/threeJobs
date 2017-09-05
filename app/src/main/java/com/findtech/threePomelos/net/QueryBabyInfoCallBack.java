package com.findtech.threePomelos.net;

/**
 * Created by zhi.zhang on 4/16/16.
 */
public interface QueryBabyInfoCallBack {
    void finishQueryAll();

    interface QueryIsBind {
        void finishQueryIsBind(boolean isBind, String deviceId);
    }
    interface SaveIsBind {
        void finishSaveIsBindSuccess(boolean isBind, String deviceId);
        void finishSaveIsBindFail();
    }
}