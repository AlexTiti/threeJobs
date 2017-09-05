package com.findtech.threePomelos.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.adapter.GuidePagerAdapter;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.home.MainHomeActivity;
import com.findtech.threePomelos.home.musicbean.DeviceCarBean;
import com.findtech.threePomelos.login.LoginActivity;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.RequestUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhi.zhang on 3/16/16.
 */
public class LaunchActivity extends BaseActivity implements OperateDBUtils.SaveBabyInfoFinishListener {

    private OperateDBUtils operateDBUtils;
    private NetWorkRequest netWorkRequest;
    private boolean isShowOnce = false; //是否已经显示一次了
    private final int TIMEOUT = 0x01;
    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        sp = getSharedPreferences(IContent.IS_FIRST_USE, MODE_PRIVATE);

            Message message = new Message();
            message.what = TIMEOUT;
            mHandle.sendMessageDelayed(message, 2000);
        operateDBUtils = new OperateDBUtils(this);
        operateDBUtils.setSaveBabyInfoFinishListener(this);
        netWorkRequest = new NetWorkRequest(this);
        if (AVUser.getCurrentUser() != null && NetUtils.isConnectInternet(this)) {
            netWorkRequest.getTravelInfoDataAndSaveToDB();
           // netWorkRequest.getTotalMileageDataAndSaveToSP();
            netWorkRequest.getBabyHeightDataAndSaveToDB();
            netWorkRequest.getBabyWeightDataAndSaveToDB();
            netWorkRequest.getBabyInfoDataAndSaveToDB();
        }
    }
    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMEOUT:
                    if (sp.getBoolean(IContent.IS_FIRST_USE, true)){
                        startActivity(new Intent(LaunchActivity.this, GuideActivity.class));
                        finish();
                        return;
                    }
                    if (AVUser.getCurrentUser() != null) {
                        operateDBUtils.queryBabyInfoDataFromDB();
                        operateDBUtils.queryUserHeightData();
                        operateDBUtils.queryUserWeightData();
                        operateDBUtils.queryTravelInfoDataFromDB();
                        TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
                        travelInfoEntity.setTotalMileage(RequestUtils.getSharepreference(LaunchActivity.this)
                                .getString(OperateDBUtils.TOTAL_MILEAGE, "0.0"));
                    } else {
                        startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                        finish();
                    }
                    break;
            }
        }
    };


    @Override
    public void saveBabyInfoFinish() {
        if (!isShowOnce) {
            if (BabyInfoEntity.getInstance().getIsBind()) {
                startActivity(new Intent(LaunchActivity.this, MainHomeActivity.class));
                finish();
            } else {
                Intent intent = new Intent(LaunchActivity.this, MainHomeActivity.class);
                intent.putExtra("from", "LaunchActivity");
                startActivity(intent);
                finish();
            }
            isShowOnce = true;
        }
    }
}
