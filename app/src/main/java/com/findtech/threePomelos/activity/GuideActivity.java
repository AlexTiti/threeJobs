package com.findtech.threePomelos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.adapter.GuidePagerAdapter;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.login.LoginActivity;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.RequestUtils;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {

    private int[] imageId;
    private List<ImageView> imageViewList;
    ViewPager guideContent;
    LinearLayout llPoint;
    Button bt_goto_main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_launch);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        SharedPreferences sp = getSharedPreferences(IContent.IS_FIRST_USE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        guideContent = (ViewPager) findViewById(R.id.vp_launch_content);
        llPoint = (LinearLayout) findViewById(R.id.ll_point);
        bt_goto_main = (Button) findViewById(R.id.bt_goto_main);
        guideContent.setVisibility(View.VISIBLE);
        editor.putBoolean(IContent.IS_FIRST_USE, false).apply();
        guide();

    }

    private void guide() {
        initData();
        guideContent.setVisibility(View.VISIBLE);
        llPoint.setVisibility(View.VISIBLE);

        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            private int preSelect = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == imageViewList.size() - 1 && positionOffset > 0.6) {
                    bt_goto_main.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                llPoint.getChildAt(preSelect).setEnabled(false);
                llPoint.getChildAt(position).setEnabled(true);
                preSelect = position;
                if (position == imageViewList.size() - 1) {
                    bt_goto_main.setVisibility(View.VISIBLE);
                    llPoint.setVisibility(View.GONE);
                    bt_goto_main.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                                finish();
                        }
                    });
                } else {
                    llPoint.setVisibility(View.VISIBLE);
                    bt_goto_main.setVisibility(View.GONE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };


        ImageView imageView;
        View pointView;
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < imageId.length; i++) {
            imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageViewList.add(imageView);

            //指示器
            pointView = new ImageView(this);
            pointView.setBackgroundResource(R.drawable.launch_select_bac);
            layoutParams = new LinearLayout.LayoutParams(30, 30);
            if (i != 0)
                layoutParams.leftMargin = 50;

            pointView.setEnabled(false);
            llPoint.addView(pointView, layoutParams);
        }
        GuidePagerAdapter adapter = new GuidePagerAdapter(imageViewList, this, imageId);
        guideContent.setAdapter(adapter);
        llPoint.getChildAt(0).setEnabled(true);
        guideContent.addOnPageChangeListener(listener);
    }


    private void initData() {
        imageId = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3,R.drawable.guide_4};
        imageViewList = new ArrayList<>();
    }



}
