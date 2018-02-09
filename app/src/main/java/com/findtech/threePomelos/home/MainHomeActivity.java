package com.findtech.threePomelos.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.home.fragment.UserFragment;
import com.findtech.threePomelos.home.presenter.HomePresenter;
import com.findtech.threePomelos.home.view.IViewMainHome;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class MainHomeActivity extends MyActionBarActivity implements IViewMainHome, ViewPager.OnPageChangeListener {

    HomePresenter homePresenter;
    public ViewPager viewpager_home;
    TabLayout tab_home_layout;
    FragmentAdapter fragmentAdapter;
    private static long DOUBLE_CLICK_TIME = 0L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        MyApplication.getInstance().addActivity(this);
        setToolBarDiss();
        viewpager_home = (ViewPager) findViewById(R.id.viewpager_home);
        viewpager_home.setOnPageChangeListener(this);
        tab_home_layout = (TabLayout) findViewById(R.id.tab_home_layout);
        homePresenter = new HomePresenter(this);
        homePresenter.installModelData();
        Nammu.init(this);

        PushService.setDefaultPushCallback(this, MainHomeActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    final String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    L.e("_Installation", installationId);
                    AVUser user = AVUser.getCurrentUser();
                    user.put("installationId", installationId);
                    user.put("deviceType", "Android");
                    user.saveInBackground();
                } else {
                }
            }
        });


        checkUpdate();
    }

    private void checkUpdate() {

        SharedPreferences sp = getSharedPreferences(IContent.IS_FIRST_USE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        final String sp_version = sp.getString(IContent.APP_UPDATE, Tools.getCurrentVersion(MainHomeActivity.this));
        L.e("sp_version=====",sp_version);
        BDAutoUpdateSDK.cpUpdateCheck(this, new CPCheckUpdateCallback() {
            @Override
            public void onCheckUpdateCallback(AppUpdateInfo appUpdateInfo, AppUpdateInfoForInstall appUpdateInfoForInstall) {

                if (appUpdateInfoForInstall != null && !TextUtils.isEmpty(appUpdateInfoForInstall.getInstallPath())) {
                    if (!sp_version.equals(appUpdateInfoForInstall.getAppVersionName())) {
                        BDAutoUpdateSDK.uiUpdateAction(MainHomeActivity.this, new MyUICheckUpdateCallback());
                        editor.putString(IContent.APP_UPDATE, appUpdateInfoForInstall.getAppVersionName()).apply();
                        L.e("sp_version=====",sp_version);
                    }
                } else if (appUpdateInfo != null) {
                    if (!sp_version.equals(appUpdateInfo.getAppVersionName())) {
                        BDAutoUpdateSDK.uiUpdateAction(MainHomeActivity.this, new MyUICheckUpdateCallback());
                        editor.putString(IContent.APP_UPDATE, appUpdateInfo.getAppVersionName()).apply();
                        L.e("sp_version=====",sp_version);
                    }
                }

            }
        });

    }


    @Override
    public void refreshUI(ArrayList<Fragment> fragments) {
        if (fragments == null) {
            return;
        }
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this, fragments);
        viewpager_home.setAdapter(fragmentAdapter);
        viewpager_home.setOffscreenPageLimit(1);
        tab_home_layout.setupWithViewPager(viewpager_home);
        for (int i = 0; i < fragments.size(); i++) {
            TabLayout.Tab tab = tab_home_layout.getTabAt(i);
            tab.setCustomView(fragmentAdapter.getView(i));

            if (i == 0) {
                tab.getCustomView().setSelected(true);
            }


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - DOUBLE_CLICK_TIME) > 2000) {
                ToastUtil.showToast(this, getString(R.string.double_click_exit));
                DOUBLE_CLICK_TIME = System.currentTimeMillis();
            } else {
                stopService(intent);
                MyApplication.getInstance().exit();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerMusicBroadcast();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        View view = fragmentAdapter.getView(position);
//        TextView textView = (TextView) view.findViewById(R.id.text_tab);
//        textView.setTextColor(getResources().getColor(R.color.black));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
