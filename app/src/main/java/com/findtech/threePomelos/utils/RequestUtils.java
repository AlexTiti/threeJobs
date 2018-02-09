package com.findtech.threePomelos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.findtech.threePomelos.base.MyApplication;

/**
 * Created by zhi.zhang on 12/22/15.
 */
public class RequestUtils {

    private Context mContext;
    public static String sharepreference = "com.findtech.threePomelos.register";
    public static UserHeightChangeListener mUserHeightChangeListener;
    public static UserWeightChangeListener mUserWeightChangeListener;
    public RequestUtils(Context context) {
        mContext = context;
    }
    public static String USERNAME = "username";//phone number
    public static String BABYSEX = "sex";
    public static String BIRTHDAY = "birthday";
    /**
     * user info
     */
    public static String WEIGHT = "weight";
    public static String HEIGHT = "height";
    public static String TOTALE_MILEAGE = "total_mileage";
    public static String TOTALE_DAY = "total_day";
    public static String TRAVEL_INFO = "travel_info";
    /**
     * bluetooth info
     */
    public static String DEVICE = "device_num";
    public static String FIRMWARE_VERSION = "FIRMWARE_VERSION";
    public static String CURRENT_ELECTRIC = "CURRENT_ELECTRIC";
    public static String TEMPERATURE = "TEMPERATURE";
    public static String RECEIVE_TEMPERATURE_ELECTRIC_ACTION = "android.receive.temperature.electric";

    public static final String BABY_TOTAL_MONTH = "baby_total_month";
    public static final String HEIGHT_HEALTH_STATE = "height_health_state";
    public static final String WEIGHT_HEALTH_STATE = "weight_health_state";
    public static String IS_LOGIN = "is_login";

    /**
     * 显示育儿预览
     */
    public static final String TIPS_PREVIEW = "tips_preview";

    public static SharedPreferences.Editor getSharepreferenceEditor(Context context) {
        SharedPreferences.Editor sharedPreferencesEditor;
        if (context != null) {
            sharedPreferencesEditor = context.getSharedPreferences(sharepreference, Context.MODE_PRIVATE).edit();
        } else {
            sharedPreferencesEditor = MyApplication.getInstance().getApplicationContext().getSharedPreferences(sharepreference, Context.MODE_PRIVATE).edit();
        }
        return sharedPreferencesEditor;
    }

    public static SharedPreferences getSharepreference(Context context) {
        SharedPreferences sharedPreferences;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(sharepreference, Context.MODE_PRIVATE);
        } else {
            sharedPreferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences(sharepreference, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface UserHeightChangeListener {
        void userHeightChange();
    }

    public void setUserHeightChangeListener(UserHeightChangeListener userHeightChangeListener) {
        mUserHeightChangeListener = userHeightChangeListener;
    }

    public interface UserWeightChangeListener {
        void userWeightChange();
    }

    public void setUserWeightChangeListener(UserWeightChangeListener userWeightChangeListener) {
        mUserWeightChangeListener = userWeightChangeListener;
    }


}
