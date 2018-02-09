package com.findtech.threePomelos.activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.fragment.HeightViewFragment;
import com.findtech.threePomelos.fragment.WeightBarCharFragment;
import com.findtech.threePomelos.fragment.WeightTipsFragment;
import com.findtech.threePomelos.fragment.WeightViewFragment;
import com.findtech.threePomelos.utils.FileUtils;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.CustomShareBoard;
import com.findtech.threePomelos.view.dialog.CustomDialog;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.ArrayList;
import java.util.List;

public class BabyDataDetailActivity extends MyActionBarActivity {
    private static final int[] TABS = {R.string.main_tab_height, R.string.main_tab_weight, R.string.main_tab_mileage};
    private List<Fragment> mFragments;
    private ArrayList<View> dots;
    ViewPager mViewPager;
    View mHead;
    final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int mBabyWeightColor;
    private int mCurveInfoColor;
    private int mPosition = 0;
    private int oldPosition;
    private PagerAdapter mPagerAdapter;
    WeightBarCharFragment weightBarCharFragment;
    WeightTipsFragment weightTipsFragment;
    private CustomShareBoard customShareBoard;
    private RelativeLayout babyDetailHeadLayout;
    private LinearLayout dotsLayout;
    private LinearLayout tipsLayout;
    private LinearLayout sharePageLayout;
    private LinearLayout sharePageButtomLayout;
    private ImageView babyShareHeadImage;
    private TextView babyDescriptionText;
    private HeightViewFragment mHeightViewFragment;
    private WeightViewFragment mWeightViewFragment;
    private ProgressDialog progressDialog;
    private ArrayList<PersonDataEntity> timeWeightDataArray = new ArrayList<>();
    private ArrayList<PersonDataEntity> timeHeightDataArray = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x40:
                    sharePageLayout.invalidate();
                    if (FileUtils.fileIsExists(FileUtils.SOURCE_IMAGE_FILE_TEMP)) {
                        FileUtils.deleteFileFromPath(FileUtils.SOURCE_IMAGE_FILE_TEMP);
                    }
                    Bitmap bitmap = FileUtils.createBitmapFromView(sharePageLayout);
                    if (bitmap != null) {
                        FileUtils.SaveBitmapToFilePath(bitmap, FileUtils.SOURCE_IMAGE_FILEFOLDER_TEMP);
                    } else {
                        return;
                    }
                    progressDialog.dismiss();
                    CustomShareBoard.Builder builder = new CustomShareBoard.Builder(BabyDataDetailActivity.this);
                    customShareBoard = builder.create();
                    builder.setUMImage(FileUtils.SOURCE_IMAGE_FILE_TEMP);
                    Tools.setDialogBackground(customShareBoard, 0.0f, 1.0f);
                    customShareBoard.show();
                    customShareBoard.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showSharePage(false);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_weight_info);
        registerMusicBroadcast();
        initView();
        timeWeightDataArray = MyApplication.getInstance().getUserWeightData();
        timeHeightDataArray = MyApplication.getInstance().getUserHeightData();
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getPackageManager();
                boolean permission = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", getBaseContext().getPackageName()));
                if (permission) {
                    DoShareStep();
                } else {
                    ActivityCompat.requestPermissions(BabyDataDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                requestOverlays(this,getResources().getString(R.string.open_bluetooth_cancle));
            }
        }
    }
//    private void requestAlertWindowPermission() {
//        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
//        builder.setTitle(getResources().getString(R.string.notice));
//        builder.setNotifyInfo(getResources().getString(R.string.open_bluetooth_message));
//        builder.setShowButton(true);
//        builder.setPositiveButton(getResources().getString(R.string.set), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW_PERMISSIONS);
//            }
//        });
//
//        builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                ToastUtil.showToast(BabyDataDetailActivity.this, getResources().getString(R.string.open_bluetooth_cancle), Toast.LENGTH_LONG);
//            }
//        });
//        builder.create().show();
//    }

    public void DoShareStep() {
        showSharePage(true);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 0x40;
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DoShareStep();
                } else {
                    ToastUtil.showToast(this, getResources().getString(R.string.text_toast_no_storage_permission));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        getContentResolver().registerContentObserver(OperateDBUtils.HEIGHT_URI, true, contentObserver);
        getContentResolver().registerContentObserver(OperateDBUtils.WEIGHT_URI, true, contentObserver);
        weightBarCharFragment = new WeightBarCharFragment();
        weightTipsFragment = new WeightTipsFragment();
        switch (mPosition) {
            case 0:
                setToolbar(getResources().getString(R.string.main_tab_height), true, null);
                getSupportFragmentManager().beginTransaction().replace(R.id.tips, weightTipsFragment).commitAllowingStateLoss();
                break;
            case 1:
                setToolbar(getResources().getString(R.string.main_tab_weight), true, null);
                getSupportFragmentManager().beginTransaction().replace(R.id.tips, weightTipsFragment).commitAllowingStateLoss();
                break;

        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (customShareBoard != null) {
            customShareBoard.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    public void showSharePage(boolean isNeedShow) {
        if (isNeedShow) {
            Bitmap bitmap = PicOperator.getIconFromData(this);
            if (bitmap != null) {
                babyShareHeadImage.setImageBitmap(PicOperator.toRoundBitmap(bitmap));
            } else {
                babyShareHeadImage.setImageResource(R.mipmap.homepage_headdata_bg_nor);
            }
            mToolbar.setVisibility(View.GONE);
            dotsLayout.setVisibility(View.GONE);
            tipsLayout.setVisibility(View.GONE);
            babyDetailHeadLayout.setVisibility(View.VISIBLE);
            sharePageButtomLayout.setVisibility(View.VISIBLE);
            String str = "";
            if (mPosition == 0) {
                String heightNumStr = RequestUtils.getSharepreference(this).getString(RequestUtils.HEIGHT, "0");
                if (timeHeightDataArray.size() == 0) {
                    str = getString(R.string.text_share_no_data_description_text);
                } else {
                    str = String.format(getString(R.string.text_share_hegiht_description_text), getString(R.string.baby), heightNumStr);
                }
                babyDescriptionText.setText(str);
                babyDescriptionText.setTextSize(18);
                babyDescriptionText.setPadding(0, 0, 0, 0);
                mHeightViewFragment.setAsShareFragement(isNeedShow);
            } else if (mPosition == 1) {
                String weightNum = RequestUtils.getSharepreference(this).getString(RequestUtils.WEIGHT, "0");
                if (timeWeightDataArray.size() == 0) {
                    str = getString(R.string.text_share_no_data_description_text);
                } else {
                    str = String.format(getString(R.string.text_share_wegiht_description_text), getString(R.string.baby), weightNum);
                }
                babyDescriptionText.setText(str);
                babyDescriptionText.setTextSize(18);
                babyDescriptionText.setPadding(0, 0, 0, 0);
                mWeightViewFragment.setAsShareFragement(isNeedShow);
            }
        } else {
            mToolbar.setVisibility(View.VISIBLE);
            dotsLayout.setVisibility(View.VISIBLE);
            tipsLayout.setVisibility(View.VISIBLE);
            babyDetailHeadLayout.setVisibility(View.GONE);
            sharePageButtomLayout.setVisibility(View.GONE);
            mHeightViewFragment.setAsShareFragement(isNeedShow);
            mWeightViewFragment.setAsShareFragement(isNeedShow);

        }
    }

    private void initView() {
        initShareView();
        babyDetailHeadLayout = (RelativeLayout) findViewById(R.id.id_rl_baby_image);
        dotsLayout = (LinearLayout) findViewById(R.id.layout_dots);
        tipsLayout = (LinearLayout) findViewById(R.id.tips);
        sharePageLayout = (LinearLayout) findViewById(R.id.layout_share_page);
        sharePageButtomLayout = (LinearLayout) findViewById(R.id.layout_share_buttom);
        babyShareHeadImage = (ImageView) findViewById(R.id.image_share_baby_head);
        babyDescriptionText = (TextView) findViewById(R.id.text_baby_description);
        mViewPager = (ViewPager) findViewById(R.id.id_tabViewPager);
        mHead = findViewById(R.id.id_rl_head);
        progressDialog = new ProgressDialog(BabyDataDetailActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.tag_edit_pic_handle_progress_txt));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mBabyWeightColor = getApplication().getResources().getColor(R.color.text_pink);
        mCurveInfoColor = getApplication().getResources().getColor(R.color.text_pink);
        dots = new ArrayList<>();
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        mHeightViewFragment = new HeightViewFragment();
        mWeightViewFragment = new WeightViewFragment();
        mFragments = new ArrayList<>();
        mFragments.add(mHeightViewFragment);
        mFragments.add(mWeightViewFragment);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int finalColor;
                if (position < mPagerAdapter.getCount() - 1) {
                    finalColor = (Integer) argbEvaluator.evaluate(positionOffset, mBabyWeightColor, mCurveInfoColor);
                } else {
                    finalColor = mCurveInfoColor;
                }
                mHead.setBackgroundColor(finalColor);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                oldPosition = position;

                switch (position) {
                    case 0:
                        mPosition = 0;
                        onResume();
                        break;
                    case 1:
                        mPosition = 1;
                        onResume();
                        break;
                    case 2:
                        mPosition = 2;
                        onResume();
                        break;
                }
            }
        });

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mPagerAdapter);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt("position");
            mViewPager.setCurrentItem(position);
            oldPosition = position;
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public PagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.mFragments = fragments;
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;//getString(mTabText[position]);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW_PERMISSIONS) {
                if (!Settings.canDrawOverlays(this)) {
                    ToastUtil.showToast(BabyDataDetailActivity.this, getResources().getString(R.string.not_open_message), Toast.LENGTH_LONG);
                }
            }
        }
    }

    ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.getPath().contains(OperateDBUtils.HEIGHT)) {
                RequestUtils.mUserHeightChangeListener.userHeightChange();
            }
            if (uri.getPath().contains(OperateDBUtils.WEIGHT)) {
                RequestUtils.mUserWeightChangeListener.userWeightChange();
            }

        }
    };


}
