package com.findtech.threePomelos.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.home.fragment.HealthTipsFragment;
import com.findtech.threePomelos.home.presenter.HomePresenter;
import com.findtech.threePomelos.home.view.IViewMainHome;
import com.findtech.threePomelos.music.activity.HotRecommenActivity;
import com.findtech.threePomelos.music.activity.PlayDetailActivity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.utils.ToastUtil;


import java.util.ArrayList;

public class MainHomeActivity extends MyActionBarActivity implements IViewMainHome,ViewPager.OnPageChangeListener{

    HomePresenter homePresenter;
    public   ViewPager viewpager_home;
    TabLayout  tab_home_layout;
    FragmentAdapter fragmentAdapter;
    private static long DOUBLE_CLICK_TIME = 0L;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        PushService.setDefaultPushCallback(this, MainHomeActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });

    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
        if (!Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE, 1
            );
        }
    }

    @Override
    public void refreshUI(ArrayList<Fragment> fragments) {
        if (fragments == null)
            return;
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),this,fragments);
        viewpager_home.setAdapter(fragmentAdapter);
        viewpager_home.setOffscreenPageLimit(1);

        tab_home_layout.setupWithViewPager(viewpager_home);
        for (int i=0 ;i<fragments.size();i++){
            TabLayout.Tab tab = tab_home_layout.getTabAt(i);
            tab.setCustomView(fragmentAdapter.getView(i));

            if (i == 0) {
                tab.getCustomView().setSelected(true);
//              TextView textView = (TextView) tab.getCustomView().findViewById(R.id.text_tab);
//                textView.setTextColor(getResources().getColor(R.color.black));
            }


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - DOUBLE_CLICK_TIME) > 2000) {
                    ToastUtil.showToast(this,getString(R.string.double_click_exit));
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
