package com.findtech.threePomelos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.utils.FileUtils;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.CustomShareBoard;
import com.findtech.threePomelos.view.TagImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class TagImageShareActivity extends MyActionBarActivity implements View.OnClickListener {
    private LinearLayout shareLayout;
    private ImageView btnNext;
    private CircleImageView mShareImage;
    private String isShowDataTag = null;
    private Button btnTagViewShare;
    private TextView btnTagViewSave;
    private String weight;
    private String height;
    private String babyDays;
    private boolean isSaved;
    private PicOperator picOperator;
    private CustomShareBoard customShareBoard;
    private String saveImagePath;

    private ProgressDialog progressDialog;
    private Handler myHandler = null;
    private final int IMAGE_SAVE_OK = 0x00;
    private final int IMAGE_SAVE_FAILED = 0x01;
    private final int PREPARE_FOR_SHARE_DONE = 0x02;
    private final int PREPARE_FOR_SHARE_UNDONE = 0x03;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_image_share);
        setToolbar(getString(R.string.edit_done), true, "Save And Share");
        Intent intent = getIntent();
        isShowDataTag = intent.getStringExtra("show_tag");
        weight = intent.getStringExtra("baby_weight");
        height = intent.getStringExtra("baby_height");
        babyDays = intent.getStringExtra("baby_days");
        saveImagePath = intent.getStringExtra("savePath");
        isSaved = false;
        picOperator = new PicOperator(this);
        initViews();
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case IMAGE_SAVE_OK:
                        progressDialog.dismiss();
                        Toast.makeText(TagImageShareActivity.this, getResources().getString(R.string.text_toast_pic_saved_successfully) + saveImagePath, Toast.LENGTH_SHORT).show();
                        break;
                    case IMAGE_SAVE_FAILED:
                        progressDialog.dismiss();
                        Toast.makeText(TagImageShareActivity.this, getResources().getString(R.string.text_toast_pic_saved_failed), Toast.LENGTH_SHORT).show();
                        break;
                    case PREPARE_FOR_SHARE_DONE:
                        progressDialog.dismiss();
                        CustomShareBoard.Builder builder = new CustomShareBoard.Builder(TagImageShareActivity.this);
                        builder.setUMImage(saveImagePath);
                        customShareBoard = builder.create();
                        customShareBoard.show();
                        break;
                    case PREPARE_FOR_SHARE_UNDONE:
                        progressDialog.dismiss();
                        Toast.makeText(TagImageShareActivity.this, getResources().getString(R.string.text_toast_pic_saved_failed), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerMusicBroadcast();
    }
    @Override
    protected void onResume() {
        super.onResume();
        colorChange("000000");
    }

    private void initViews() {
        shareLayout = (LinearLayout) findViewById(R.id.share_button_layout);
        btnNext = (ImageView) findViewById(R.id.btn_tagimage_edit_next);
        mShareImage = (CircleImageView) findViewById(R.id.tag_image_share);
        btnTagViewShare = (Button) findViewById(R.id.btn_tagimage_save);
        btnTagViewSave = (TextView) findViewById(R.id.btn_tagimage_text_share);
        btnNext.setVisibility(View.INVISIBLE);
        shareLayout.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.tag_share_pic_handle_progress_txt));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        Bitmap bitmap = BitmapFactory.decodeFile(saveImagePath, null);
        if (bitmap != null) {
            mShareImage.setImageBitmap(bitmap);
        }

        btnTagViewSave.setOnClickListener(this);
        btnTagViewShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tagimage_text_share:
                saveTagPictureAndShare();

                break;
            case R.id.btn_tagimage_save:
                //finish;
                if (isSaved) {
                    Toast.makeText(this, getResources().getString(R.string.text_toast_pic_had_been_saved), Toast.LENGTH_SHORT).show();
                } else {
                   // saveTagPicture();
                }
                break;
            default:
                break;
        }
    }

//    private void saveTagPicture() {
//        if (isSaved) {
//            Toast.makeText(this, getResources().getString(R.string.text_toast_pic_had_been_saved), Toast.LENGTH_SHORT).show();
//        } else {
//            progressDialog.show();
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    if (!isSaved) {
//                        Message msg = new Message();
//                        saveImagePath = FileUtils.saveCroppedImage(TagImageShareActivity.this, FileUtils.createBitmapFromView(mTagImageView), false);
//                        if (saveImagePath != null) {
//                            isSaved = true;
//                            if (FileUtils.fileIsExists(FileUtils.SOURCE_IMAGE_FILE_TEMP)) {
//                                FileUtils.deleteFileFromPath(FileUtils.SOURCE_IMAGE_FILE_TEMP);
//                            }
//                            msg.what = IMAGE_SAVE_OK;
//                            myHandler.sendMessage(msg);
//                        } else {
//                            msg.what = IMAGE_SAVE_FAILED;
//                            myHandler.sendMessage(msg);
//                        }
//                    }
//                }
//            }.start();
//        }
//    }
    private void saveTagPictureAndShare() {
        CustomShareBoard.Builder builder = new CustomShareBoard.Builder(TagImageShareActivity.this);
        if (saveImagePath != null) {
            builder.setUMImage(saveImagePath);
            customShareBoard = builder.create();
            customShareBoard.show();
        } else {
            ToastUtil.showToast(this,getString(R.string.save_error));

//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    saveImagePath = FileUtils.saveCroppedImage(TagImageShareActivity.this, FileUtils.createBitmapFromView(mTagImageView), false);
//                    if (saveImagePath != null) {
//                        if (FileUtils.fileIsExists(FileUtils.SOURCE_IMAGE_FILE_TEMP)) {
//                            FileUtils.deleteFileFromPath(FileUtils.SOURCE_IMAGE_FILE_TEMP);
//                        }
//                        Message msg = new Message();
//                        msg.what = PREPARE_FOR_SHARE_DONE;
//                        myHandler.sendMessage(msg);
//                    } else {
//                        Message msg = new Message();
//                        msg.what = PREPARE_FOR_SHARE_UNDONE;
//                        myHandler.sendMessage(msg);
//                    }
//                }
//            }.start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FileUtils.fileIsExists(FileUtils.SOURCE_IMAGE_FILE_TEMP)) {
            FileUtils.deleteFileFromPath(FileUtils.SOURCE_IMAGE_FILE_TEMP);
        }
    }
}
