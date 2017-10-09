package com.findtech.threePomelos.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.AppManager;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.home.MainHomeActivity;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.musicserver.PlaylistInfo;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhi.zhang on 1/7/16.
 */
public class MyApplication extends Application {

    public static Context mContext ;
    public AppManager manager = null;
    public static final String TAG = "_TAG";
    private List<Activity> activitys = null;
    private static MyApplication instance;
  // private static final String APPID = "1R2oS0W0U9dJGveftbxOHGy3-gzGzoHsz";
    //private static final String APPKEY = "Pf2Gper3lCPI0neKo0EKWdLN";

   private static final String APPID_TEST = "prnPRaln6v5xwNqUkQu5sFUA-gzGzoHsz";
  private static final String APPKEY_TEST  = "zQodN3qR7OOVjizMfqTI3LXE";
    public static final boolean DEBUG = false;
    public static String passwordStr;
    private ArrayList<PersonDataEntity> timeHeightDataArray = new ArrayList<>();
    private ArrayList<PersonDataEntity> timeWeightDataArray = new ArrayList<>();
    private long favPlaylist = IConstants.FAV_PLAYLIST;

    public MyApplication() {
        activitys = new LinkedList<>();
    }

    //APP Sign : 32d061e3034c6bb083095beacf1894b4
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, APPID, APPKEY);
        manager = new AppManager(getApplicationContext());
//        //微信 appid appsecret
//        PlatformConfig.setWeixin("wx897cd02216ae4253", "dbd4985c48250cb8fedda58b338c00f8");
//        //新浪微博 appkey appsecret
//        PlatformConfig.setSinaWeibo("1796335974", "04d7ffd3c8855d1dbe0c7b3e2fecc781");
//        // QQ和Qzone appid appkey
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        mContext = this;
        if (!PreferencesUtility.getInstance(this).getFavriateMusicPlaylist()) {
            PlaylistInfo.getInstance(this).addPlaylist(favPlaylist, getResources().getString(R.string.baby_like),
                    0, "res:/" + null, "local");
            PreferencesUtility.getInstance(this).setFavriateMusicPlaylist(true);
        }


    }
    private static Gson gson;

    public static Gson gsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void setPassword(String passwordStr) {
        MyApplication.passwordStr = passwordStr;
    }

    public static String getPassword() {
        return passwordStr;
    }

    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        } else {
            activitys.add(activity);
        }
    }

    public void removeActivity(Activity activity){

        if (activitys != null && activitys.size() >0 && activitys.contains(activity)){
            activitys.remove(activity);

        }
    }

    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
    }

    public void setUserHeightData(ArrayList<PersonDataEntity> arrayList) {
        timeHeightDataArray = arrayList;
    }

    public ArrayList<PersonDataEntity> getUserHeightData() {
        return timeHeightDataArray;
    }

    public void setUserWeightData(ArrayList<PersonDataEntity> arrayList) {
        timeWeightDataArray = arrayList;
    }

    public ArrayList<PersonDataEntity> getUserWeightData() {
        return timeWeightDataArray;
    }

    public String getHeadIconPath() {
        String path = "default_head_icon.png";
        if (AVUser.getCurrentUser().getObjectId() != null) {
            path = AVUser.getCurrentUser().getObjectId() + "_head_icon.png";
        }
        return path;
    }

}
