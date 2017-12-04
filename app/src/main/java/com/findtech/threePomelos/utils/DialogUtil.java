package com.findtech.threePomelos.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.findtech.threePomelos.R;

/**
 * <pre>
 *
 *   @author   :   Alex
 *   @e_mail   :   18238818283@sina.cn
 *   @time     :   2017/11/23
 *   @desc     :
 *   @version  :   V 1.0.9
 */

public class DialogUtil {
    public static DialogUtil dialogUtil;

    private ConfirmClick confirmClick;

    public void setConfirmClick(ConfirmClick confirmClick) {
        this.confirmClick = confirmClick;
    }

    public static DialogUtil getIntence() {

        if (dialogUtil == null) {
            synchronized (DialogUtil.class) {
                if (dialogUtil == null) {
                    dialogUtil = new DialogUtil();
                }
            }
        }
        return dialogUtil;
    }

    private DialogUtil() {

    }

    public void showDialogNoConfirm(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popwindowtest, null);
        Dialog dialog = new Dialog(context, R.style.MyDialogStyleBottom);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.8f;
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    /**
     * show dialog with one confirm
     */
    public void showDialogConfirm(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popwindow_update, null);
        ImageView imageButton = (ImageView) view.findViewById(R.id.imageButton);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Glide.with(context).load(IContent.getInstacne().newCodeUrl).into(imageView);
        final Dialog dialog = new Dialog(context, R.style.MyDialogStyleBottom);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (confirmClick != null) {
                    confirmClick.onConfirm();
                }
            }
        });
        dialog.show();
    }

    public interface ConfirmClick {
        /**
         * confirm of the dialog
         */
        void onConfirm();
    }


}
