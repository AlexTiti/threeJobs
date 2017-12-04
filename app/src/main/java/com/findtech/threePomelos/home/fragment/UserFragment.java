package com.findtech.threePomelos.home.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.AboutUSActivity;
import com.findtech.threePomelos.activity.BabyInfoActivity;
import com.findtech.threePomelos.activity.CommendProblemActivity;
import com.findtech.threePomelos.activity.FeedBack;
import com.findtech.threePomelos.base.BaseLazyFragment;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.home.MyUICheckUpdateCallback;
import com.findtech.threePomelos.login.LoginActivity;
import com.findtech.threePomelos.login.ThirdPartyController;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners;

import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alex on 2017/5/5.
 * <pre>
 *     @author  ： Alex
 *     @e-mail  ： 18238818283@sina.cn
 *     @time    ： 2017/05/05
 *     @desc    ：
 *     @version ： 1.0
 */
public class UserFragment extends BaseLazyFragment implements View.OnClickListener {
    private RelativeLayout faceback,relay_common_problem;
    private RelativeLayout update;
    private RelativeLayout about_us;
    private ProgressDialog progressDialog;
    private Button text_out;
    private ThirdPartyController mThirdPartyController;
    private RelativeLayout relativeLayout;
    private TextView text_weight_user, text_height_user,text_update_notice;
    private CircleImageView circleImage;
    private Bitmap bitmap;
    private TextView text_id, text_age_user;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();
    public final static String CHANGE = "CHANGE";
    public final int TOAST_NUMB = 1001;
    private IContent content = IContent.getInstacne();


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TOAST_NUMB:
                    Toast.makeText(mContext, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.userfragment;
    }

    @Override
    protected void initViewsAndEvents(View view) {

        text_id = (TextView) view.findViewById(R.id.text_id);
        text_age_user = (TextView) view.findViewById(R.id.text_age_user);
        circleImage = (CircleImageView) view.findViewById(R.id.circleImage);
        faceback = (RelativeLayout) view.findViewById(R.id.faceback);
        update = (RelativeLayout) view.findViewById(R.id.update);
        about_us = (RelativeLayout) view.findViewById(R.id.about_us);
        text_out = (Button) view.findViewById(R.id.text_out);
        text_update_notice = (TextView) view.findViewById(R.id.text_update_notice);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        relay_common_problem = (RelativeLayout) view.findViewById(R.id.relay_common_problem);
        text_weight_user = (TextView) view.findViewById(R.id.text_weight_user);
        text_height_user = (TextView) view.findViewById(R.id.text_height_user);

        String currentVersion = Tools.getCurrentVersion(getActivity());
        if ( content.newVersion != null && content.newVersion.compareToIgnoreCase(currentVersion) == 1){
            text_update_notice.setVisibility(View.VISIBLE);
        }else {
            text_update_notice.setVisibility(View.INVISIBLE);
        }

        View view1 = view.findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(getActivity());
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);
        setCircleImage();
        if (babyInfoEntity.getBabyName() != null) {
            text_id.setText(babyInfoEntity.getBabyName());
        }
        text_age_user.setText(getBabyDate());
        faceback.setOnClickListener(this);
        update.setOnClickListener(this);
        about_us.setOnClickListener(this);
        text_out.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        relay_common_problem.setOnClickListener(this);
        initProgressDialog();
    }

    private String getBabyDate() {
        if (babyInfoEntity.getBirthday() == null) {
            return "0" + getString(R.string.text_tag_babydata_text_day);
        }
        String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
        String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
        try {
            MyCalendar myCalendar = new MyCalendar(birthday, currentDate, getActivity());
            return myCalendar.getDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @Override
    protected void onFirstUserVisible() {
        if (content.newVersion == null){
            NetWorkRequest.getAPPVersion();
        }
    }

    private void setCircleImage() {
        bitmap = PicOperator.getIconFromData(getActivity());
        if (bitmap != null) {
            circleImage.setImageBitmap(bitmap);
        } else {
            circleImage.setImageResource(R.mipmap.homepage_headdata_bg_nor);
        }
    }

    @Override
    protected void onUserVisible() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faceback:
                startActivity(new Intent(mContext, FeedBack.class));
                break;
            case R.id.update:
                progressDialog.setMessage(getActivity().getResources().getString(R.string.upDateMessage_));
                progressDialog.show();
                BDAutoUpdateSDK.cpUpdateCheck(mContext, new CPCheckUpdateCallback() {
                    @Override
                    public void onCheckUpdateCallback(AppUpdateInfo appUpdateInfo, AppUpdateInfoForInstall appUpdateInfoForInstall) {
                        if (appUpdateInfoForInstall != null && !TextUtils.isEmpty(appUpdateInfoForInstall.getInstallPath())) {
                            BDAutoUpdateSDK.uiUpdateAction(mContext, new MyUICheckUpdateCallback());
                        } else if (appUpdateInfo != null) {
                            BDAutoUpdateSDK.uiUpdateAction(mContext, new MyUICheckUpdateCallback());
                        } else {
                            ToastUtil.showToast(mContext, getActivity().getResources().getString(R.string.upDateNew));
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            case R.id.about_us:
                startActivity(new Intent(mContext, AboutUSActivity.class));
                break;
            case R.id.text_out:
                showVerifyLogoutDialog();
                break;
            case R.id.relativeLayout:
                startActivityForResult(new Intent(mContext, BabyInfoActivity.class), 1000);
                break;
            case R.id.relay_common_problem:
                startActivity(new Intent(mContext, CommendProblemActivity.class));
                break;
                default:
                    break;
        }
    }

    private void showVerifyLogoutDialog() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(getString(R.string.notice));
        builder.setNotifyInfo(getString(R.string.logout_confirm));
        builder.setShowButton(true);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logOut();
                dialog.dismiss();
                RequestUtils.getSharepreferenceEditor(mContext).clear().commit();
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });

        builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mThirdPartyController = new ThirdPartyController(mContext);
        String weight = RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT, "");
        String height = RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT, "");
        text_height_user.setText(getResources().getString(R.string.height, height));
        text_weight_user.setText(getResources().getString(R.string.weight, weight));
        AVAnalytics.onFragmentStart("UserFragment");
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
    }


    public void logOut() {
        AVUser.logOut();
        if (!mThirdPartyController.getUMSocialServiceLoginInstance().getEntity().mInitialized) {
            mThirdPartyController.getUMSocialServiceLoginInstance().initEntity(mContext,
                    new SocializeListeners.SocializeClientListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onComplete(int status, SocializeEntity entity) {
                            if (status == 200) {
                                mThirdPartyController.getUMSocialServiceLoginInstance().deleteOauth(
                                        mContext, SHARE_MEDIA.WEIXIN,
                                        new SocializeListeners.SocializeClientListener() {
                                            @Override
                                            public void onStart() {
                                            }

                                            @Override
                                            public void onComplete(int status, SocializeEntity entity) {
                                                if (status == 200) {
                                                    //  Toast.makeText(mContext, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                                                    Message message = new Message();
                                                    message.what = TOAST_NUMB;
                                                    handler.sendMessage(message);
                                                }
                                            }
                                        }
                                );
                            }
                        }
                    });
        } else {
            mThirdPartyController.getUMSocialServiceLoginInstance().deleteOauth(mContext, SHARE_MEDIA.WEIXIN,
                    new SocializeListeners.SocializeClientListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onComplete(int status, SocializeEntity entity) {
                            if (status == 200) {
                                //
                                Message message = new Message();
                                message.what = TOAST_NUMB;
                                handler.sendMessage(message);
                            } else {

                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("UserFragment");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            L.e("======", "requestCode == 1000=====" + resultCode);
            text_id.setText(babyInfoEntity.getBabyName());
            text_age_user.setText(getBabyDate());
            if (data != null && CHANGE.equals(data.getStringExtra("intent"))) {
                setCircleImage();

            }
        }

    }
}
