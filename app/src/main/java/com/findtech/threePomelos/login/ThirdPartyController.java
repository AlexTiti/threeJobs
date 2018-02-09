package com.findtech.threePomelos.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.MainHomeActivity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.net.QueryBabyInfoCallBack;
import com.findtech.threePomelos.utils.RequestUtils;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhi.zhang on 3/2/16.
 */
public class ThirdPartyController {
    private UMSocialService mLoginController;
    private UMSocialService mShareController;
    private Context mContext;
    private final String QQAppId = "1105227558";//100424468
    private final String QQAppKey = "T9B4KDL0ypc6rNCT";//c7394704798a158208a74ab60104f0ba
    private final String WXAppId = "wxe4636c9399ddee3f";
    private final String WXAppSecret = "ef46c5de4156f80d4fc4f7c52cacd657";
    private ProgressDialog progressDialog;
    private UMWXHandler wxHandler;
    private UMQQSsoHandler qqSsoHandler;


    public ThirdPartyController(Context context) {
        mContext = context;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(context.getString(R.string.loging_state));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        configPlatforms();
    }

    public void configPlatforms() {
        mLoginController = UMServiceFactory.getUMSocialService("com.umeng.login");
        // 添加新浪SSO授权
        mLoginController.getConfig().setSsoHandler(new SinaSsoHandler());

        mShareController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 添加新浪SSO授权
        mShareController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加QQ、QZone平台
        addQQAndQZonePlatform();
        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }

    public UMSocialService getUMSocialServiceLoginInstance() {
        if (mLoginController == null) {
            mLoginController = UMServiceFactory.getUMSocialService("com.umeng.login");
        }
        return mLoginController;
    }

    public UMSocialService getUMSocialServiceShareInstance() {
        if (mShareController == null) {
            mShareController = UMServiceFactory.getUMSocialService("com.umeng.share");
        }
        return mShareController;
    }

    private void addQQAndQZonePlatform() {
        // 添加QQ支持
        qqSsoHandler = new UMQQSsoHandler((Activity) mContext, QQAppId, QQAppKey);
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) mContext, QQAppId, QQAppKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // 添加微信平台
        wxHandler = new UMWXHandler(mContext, WXAppId, WXAppSecret);
        wxHandler.showCompressToast(false);
        mLoginController.getConfig().closeToast();
        mShareController.getConfig().closeToast();
        wxHandler.addToSocialSDK();

        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, WXAppId, WXAppId);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.showCompressToast(false);
        wxCircleHandler.addToSocialSDK();
    }

    public UMWXHandler getUMWXHandler() {
        return wxHandler;
    }

    public boolean isQQClientInstalled() {
        Tencent mTencent = Tencent.createInstance(QQAppId, mContext);
        return mTencent.isSupportSSOLogin((Activity) mContext);
    }

    public boolean isWeiboClientInstalled() {
        return checkApkExist(mContext, "com.sina.weibo");
    }


    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void ThirdPartyLogin(SHARE_MEDIA platform) {
        mLoginController.doOauthVerify(mContext, platform, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {

                progressDialog.show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {

            }

            @Override
            public void onComplete(final Bundle value, final SHARE_MEDIA platform) {
                //获取相关授权信息

                mLoginController.getPlatformInfo(mContext, platform, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {

                        if (status == 200 && info != null) {
                            StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for (String key : keys) {
                                sb.append(key + "=" + info.get(key).toString() + "\r\n");
                            }
                            if (platform == SHARE_MEDIA.WEIXIN) {
                                bindWeixinAccount(value, info);
                            }
                            if (platform == SHARE_MEDIA.QQ) {
                                bindQQAccount(value, info);
                            }
                            if (platform == SHARE_MEDIA.SINA) {
                                bindWeiboAccount(value, info);
                            }

                        } else {

                        }
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                progressDialog.dismiss();

            }
        });
    }

    void bindWeixinAccount(Bundle value, Map<String, Object> info) {
        String access_token = null;
        String expires_in = null;
        String uid = null;
        if (value != null) {
            access_token = value.getString("access_token");
            expires_in = value.getString("expires_in");
            uid = value.getString("uid");
        }

        AVUser.AVThirdPartyUserAuth auth =
                new AVUser.AVThirdPartyUserAuth(access_token, expires_in,
                        AVUser.AVThirdPartyUserAuth.SNS_TENCENT_WEIXIN, uid);
        AVUser.loginWithAuthData(auth, new LogInCallback<AVUser>() {

            @Override
            public void done(AVUser user, AVException e) {
                if (e == null) {
                    //恭喜你，已经和我们的 AVUser 绑定成功

                    NetWorkRequest netWorkRequest = new NetWorkRequest(mContext);
                    netWorkRequest.getTravelInfoDataAndSaveToDB();
                    netWorkRequest.getTotalMileageDataAndSaveToSP();
                    netWorkRequest.getBabyWeightDataAndSaveToDB();
                    netWorkRequest.getBabyHeightDataAndSaveToDB();
                    netWorkRequest.getBabyInfoDataAndSaveToDB(new QueryBabyInfoCallBack.QueryIsBind() {

                        @Override
                        public void finishQueryIsBind(boolean isBind, String deviceId) {
                            RequestUtils.getSharepreferenceEditor(mContext)
                                    .putBoolean(RequestUtils.IS_LOGIN, true).apply();
                            if (isBind) {
                                mContext.startActivity(new Intent(mContext, MainHomeActivity.class));
                                progressDialog.dismiss();
                            } else {
                                mContext.startActivity(new Intent(mContext, MainHomeActivity.class));
                                progressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    void bindQQAccount(Bundle value, Map<String, Object> info) {

        String access_token = null;
        String expires_in = null;
        String uid = null;
        if (value != null) {
            access_token = value.getString("access_token");
            expires_in = value.getString("expires_in");
            uid = value.getString("uid");
        }
        AVUser.AVThirdPartyUserAuth auth =
                new AVUser.AVThirdPartyUserAuth(access_token, expires_in,
                        AVUser.AVThirdPartyUserAuth.SNS_TENCENT_WEIBO, uid);
        AVUser.loginWithAuthData(auth, new LogInCallback<AVUser>() {

            @Override
            public void done(AVUser user, AVException e) {
                if (e == null) {
                    //恭喜你，已经和我们的 AVUser 绑定成功
                    NetWorkRequest netWorkRequest = new NetWorkRequest(mContext);
                    netWorkRequest.getTravelInfoDataAndSaveToDB();
                    netWorkRequest.getTotalMileageDataAndSaveToSP();
                    netWorkRequest.getBabyWeightDataAndSaveToDB();
                    netWorkRequest.getBabyHeightDataAndSaveToDB();
                    netWorkRequest.getBabyInfoDataAndSaveToDB(new QueryBabyInfoCallBack.QueryIsBind() {

                        @Override
                        public void finishQueryIsBind(boolean isBind, String deviceId) {
                            RequestUtils.getSharepreferenceEditor(mContext)
                                    .putBoolean(RequestUtils.IS_LOGIN, true).apply();
                            if (isBind) {
                                mContext.startActivity(new Intent(mContext, MainHomeActivity.class));
                                progressDialog.dismiss();
                            } else {
                                mContext.startActivity(new Intent(mContext, MainHomeActivity.class));
                                progressDialog.dismiss();
                            }
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    void bindWeiboAccount(Bundle value, Map<String, Object> info) {
        String access_token = null;
        String expires_in = null;
        String uid = null;
        if (value != null) {
            if (!TextUtils.isEmpty(value.getString("access_token"))) {
                access_token = value.getString("access_token");
            } else if (!TextUtils.isEmpty(value.getString("access_key"))) {
                access_token = value.getString("access_key");
            }
            expires_in = value.getString("expires_in");
            uid = value.getString("uid");
        }
        AVUser.AVThirdPartyUserAuth auth =
                new AVUser.AVThirdPartyUserAuth(access_token, expires_in,
                        AVUser.AVThirdPartyUserAuth.SNS_SINA_WEIBO, uid);
        AVUser.loginWithAuthData(auth, new LogInCallback<AVUser>() {

            @Override
            public void done(AVUser user, AVException e) {
                if (e == null) {
                    //恭喜你，已经和我们的 AVUser 绑定成功
                    NetWorkRequest netWorkRequest = new NetWorkRequest(mContext);
                    netWorkRequest.getTravelInfoDataAndSaveToDB();
                    netWorkRequest.getTotalMileageDataAndSaveToSP();
                    netWorkRequest.getBabyWeightDataAndSaveToDB();
                    netWorkRequest.getBabyHeightDataAndSaveToDB();
                    netWorkRequest.getBabyInfoDataAndSaveToDB(new QueryBabyInfoCallBack.QueryIsBind() {

                        @Override
                        public void finishQueryIsBind(boolean isBind, String deviceId) {
                            RequestUtils.getSharepreferenceEditor(mContext)
                                    .putBoolean(RequestUtils.IS_LOGIN, true).apply();
                            if (isBind) {
                                mContext.startActivity(new Intent(mContext, MainHomeActivity.class));
                                progressDialog.dismiss();
                            } else {
                                mContext.startActivity(new Intent(mContext, MainHomeActivity.class));
                                progressDialog.dismiss();
                            }
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public static class ScreenShot {

        // 获取指定Activity的截屏，保存到png文件

        public static Bitmap takeScreenShot(Activity activity) {

            // View是你须要截图的View
            View view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap b1 = view.getDrawingCache();

            // 获取状况栏高度
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            // 获取屏幕长和高
            int width = activity.getWindowManager().getDefaultDisplay().getWidth();
            int height = activity.getWindowManager().getDefaultDisplay().getHeight();

            // 去掉题目栏
            // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
            Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                    - statusBarHeight);
            view.destroyDrawingCache();
            return b;
        }

        // 保存到sdcard
        public static void savePic(Bitmap b, String strFileName) {

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(strFileName);
                if (null != fos) {
                    b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
