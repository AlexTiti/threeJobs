package com.findtech.threePomelos.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.login.ThirdPartyController;
import com.findtech.threePomelos.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;

/**
 *
 */
public class CustomShareBoard extends Dialog {
    private Context mContext;

    public CustomShareBoard(Context context) {
        super(context);
    }

    public CustomShareBoard(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected CustomShareBoard(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener {
        private Context context;
        private UMSocialService mController;
        CustomShareBoard dialog;
        UMImage umImage;
        private String str = null;
        private UMusic music;

        private ThirdPartyController thirdPartyController;

        public Builder(Context context) {
            this.context = context;
        }

        public CustomShareBoard create() {
            thirdPartyController = new ThirdPartyController(context);
            thirdPartyController.configPlatforms();
            mController = thirdPartyController.getUMSocialServiceShareInstance();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog = new CustomShareBoard(context, R.style.MyDialogStyleBottom);
            View viewDialog = inflater.inflate(R.layout.custom_share_board, null);
            dialog.setContentView(viewDialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            setWindowSize(dialog);
            viewDialog.findViewById(R.id.wechat).setOnClickListener(this);
            viewDialog.findViewById(R.id.wechat_circle).setOnClickListener(this);
            viewDialog.findViewById(R.id.wei_bo).setOnClickListener(this);
            viewDialog.findViewById(R.id.qq).setOnClickListener(this);
//            viewDialog.findViewById(R.id.qzone).setOnClickListener(this);
            //截屏代码
//            Bitmap bitmap = ThirdPartyController.ScreenShot.takeScreenShot((Activity) context);
//            String mFilePath = Environment.getExternalStorageDirectory().toString() + File.separator + "3pomelos" + File.separator;
//            ThirdPartyController.ScreenShot.savePic(bitmap, mFilePath + "share.png");
            return dialog;
        }

        private void setWindowSize(Dialog dialog) {
            DisplayMetrics dm = new DisplayMetrics();
            Window dialogWindow = dialog.getWindow();
            WindowManager m = dialogWindow.getWindowManager();
            m.getDefaultDisplay().getMetrics(dm);
            // 为获取屏幕宽、高
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.height = (int) (dm.heightPixels * 0.2); //高度设置为屏幕的0.15
            p.width = dm.widthPixels;
            p.alpha = 1.0f; // 设置本身透明度
            p.dimAmount = 0.6f; // 设置黑暗度
            p.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(p);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.wechat:
                    setShareContent();
                    performShare(SHARE_MEDIA.WEIXIN);
                    break;
                case R.id.wechat_circle:
                    setShareContent();
                    performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case R.id.wei_bo:
                    setShareContent();
                    performShare(SHARE_MEDIA.SINA);
                    break;
                case R.id.qq:
                    try {
                        if (thirdPartyController.isQQClientInstalled()) {
                            setShareContent();
                            performShare(SHARE_MEDIA.QQ);
                        } else {
                            ToastUtil.showToast(context, context.getResources().getString(R.string.install_qq));
                        }
                    } catch (Exception e) {

                    }
                    break;
//                case R.id.qzone:
//                    QZoneShareContent qZoneShareContent = new QZoneShareContent();
//                    qZoneShareContent.setShareImage(umImage);
//                    qZoneShareContent.setShareContent("fenxiang");
//                    mController.setShareMedia(qZoneShareContent);
//                    performShare(SHARE_MEDIA.QZONE);
//                    ToastUtil.showToast(context, "暂不支持");
//                    break;
                default:
                    break;
            }
        }

        private void performShare(SHARE_MEDIA platform) {
            mController.postShare(context, platform, new SnsPostListener() {

                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                    dialog.dismiss();
                    String eMsg = "";
                    if (eCode == 200) {
                        if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                            eMsg = context.getResources().getString(R.string.share_wei_friend_suc);
                        }
                        if (platform == SHARE_MEDIA.WEIXIN) {
                            eMsg = context.getResources().getString(R.string.share_wei_suc);
                        }
                        if (platform == SHARE_MEDIA.SINA) {
                            eMsg = context.getResources().getString(R.string.share_xinl);
                        }
                        if (platform == SHARE_MEDIA.QQ) {
                            eMsg = context.getResources().getString(R.string.share_qq);
                        }
                        Toast.makeText(context, eMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.share_defail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void setShareContent() {
            if (str == null) {
                mController.setShareMedia(umImage);
            } else {
                mController.setShareMedia(music);
            }
        }

        public void setUMImage(String path) {
            umImage = new UMImage(context, BitmapFactory.decodeFile(path));
        }
    }
}
