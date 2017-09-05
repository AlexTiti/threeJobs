package com.findtech.threePomelos.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.utils.FileUtils;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.view.TagImageView;

public class TagImageEditActivity extends MyActionBarActivity implements View.OnClickListener {
    private TextView btnCustom;
    private ImageView btnTag1;
    private TextView btnTag2,btnTag0;
    private TextView btnTag3;
    private ImageView btnTagPanel;
    private RelativeLayout mBabydataTag;
    private LinearLayout mTagChooseLayout,btn_tag_1_layout,btn_tag_layout_layout;
    private TagImageView tagImageView;
    private String weight;
    private String height;
    private String babyDays;
    private TextView txtBabyDays,text_lable,text_info;
    private TextView txtBabyHeight;
    private TextView txtBabyWeight;
    boolean isBabyDataTagChoosed = true;
    private ImageView btnTagImageEditNext;
    private ImageView mImageView = null;
    private String camera_path;
    private Bitmap resizeBmp;
    PicOperator picOperator;
    private ProgressDialog progressDialog;
    private Handler myHandler = null;
    private Intent intentShare;
    private boolean isShowTag = true;
    private String savePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_image_edit);

        setToolbar(getResources().getString(R.string.text_activity_title_tag_image_edit), true,"Edit WaterPrint");
        Intent intent = getIntent();
        camera_path = intent.getStringExtra("camera_path");
        weight = intent.getStringExtra("baby_weight");
        height = intent.getStringExtra("baby_height");
        babyDays = intent.getStringExtra("baby_days");
        picOperator = new PicOperator(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.tag_edit_pic_handle_progress_txt));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        initViews();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        resizeBmp = BitmapFactory.decodeFile(camera_path, null);
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.dismiss();
                startActivity(intentShare);
                finish();
            }
        };
        registerMusicBroadcast();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mImageView.setImageBitmap(resizeBmp);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) tagImageView.getLayoutParams();
        double bili = (double) resizeBmp.getHeight() / resizeBmp.getWidth();
        linearParams.height = (int) (bili * picOperator.screenWidth);
        tagImageView.setLayoutParams(linearParams);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @SuppressLint("NewApi")
    private void initViews() {
        txtBabyDays = (TextView) findViewById(R.id.baby_data_tag).findViewById(R.id.txt_baby_days);
        txtBabyHeight = (TextView) findViewById(R.id.baby_data_tag).findViewById(R.id.tag_text_height);
        txtBabyWeight = (TextView) findViewById(R.id.baby_data_tag).findViewById(R.id.tag_text_weight);
        mImageView = (ImageView) findViewById(R.id.main_image_1);
        btnTagImageEditNext = (ImageView) findViewById(R.id.btn_tagimage_edit_next);
        btnCustom = (TextView) findViewById(R.id.btn_tag_custom);
        btnTag1 = (ImageView) findViewById(R.id.btn_tag_1);
        btnTag0 = (TextView) findViewById(R.id.btn_tag_0);
        btnTag2 = (TextView) findViewById(R.id.btn_tag_2);
        btnTag3 = (TextView) findViewById(R.id.btn_tag_3);
        text_info = (TextView) findViewById(R.id.text_info);
        text_lable = (TextView) findViewById(R.id.text_lable);
        btnTagPanel = (ImageView) findViewById(R.id.btn_tag_layout);
        tagImageView = (TagImageView) findViewById(R.id.image_layout);
        mTagChooseLayout = (LinearLayout) findViewById(R.id.tag_choose_layout);
        mBabydataTag = (RelativeLayout) findViewById(R.id.baby_data_tag);
        btn_tag_layout_layout = (LinearLayout) findViewById(R.id.btn_tag_layout_layout);
        btn_tag_1_layout = (LinearLayout) findViewById(R.id.btn_tag_1_layout);

        btnCustom.setOnClickListener(this);
       // btnTag1.setOnClickListener(this);
        btn_tag_1_layout.setOnClickListener(this);

        if (isBabyDataTagChoosed) {
            btnTag1.setImageResource(R.drawable.icon_tag_babyinfo);
            text_info.setSelected(true);
            text_lable.setSelected(true);
        } else {
            btnTag1.setImageResource(R.drawable.icon_tag_babyinfo_un);
            text_info.setSelected(false);
        }
        btnTag0.setOnClickListener(this);
        btnTag2.setOnClickListener(this);
        btnTag3.setOnClickListener(this);
        btn_tag_layout_layout.setOnClickListener(this);
        //btnTagPanel.setOnClickListener(this);
        btnTagImageEditNext.setOnClickListener(this);
        txtBabyDays.setText(babyDays);
        txtBabyHeight.setText(height+" cm");
        txtBabyWeight.setText(weight+" kg");
    }

    private void showCustomTagDialog() {
        final EditText textTag = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.text_tag_input_dalog_title))
                .setView(textTag).setPositiveButton(getResources().getString(R.string.tag_edit_dialog_ok_button_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = textTag.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    tagImageView.addTextTag(str, 100, 400);
                } else {
                    Toast.makeText(TagImageEditActivity.this, getResources().getString(R.string.tag_edit_empty_toast_txt), Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton(getResources().getString(R.string.tag_edit_dialog_cancel_button_txt), null)
                .show();
        textTag.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tag_1_layout:
                if (isBabyDataTagChoosed) {
                    isBabyDataTagChoosed = false;
                    mBabydataTag.setVisibility(View.GONE);
                    btnTag1.setImageResource(R.drawable.icon_tag_babyinfo_un);
                    text_info.setSelected(false);
                } else {
                    isBabyDataTagChoosed = true;
                    mBabydataTag.setVisibility(View.VISIBLE);
                    btnTag1.setImageResource(R.drawable.icon_tag_babyinfo);
                    text_info.setSelected(true);
                }
                break;
            case R.id.btn_tag_0:
                tagImageView.addTextTag(getString(R.string.pic_tag_monther), 200, 200);
                break;
            case R.id.btn_tag_2:
                tagImageView.addTextTag(getString(R.string.pic_tag_love), 200, 200);
                break;
            case R.id.btn_tag_3:
                tagImageView.addTextTag(getString(R.string.pic_tag_praise), 300, 300);
                break;
            case R.id.btn_tag_custom:
                showCustomTagDialog();
                break;
            case R.id.btn_tagimage_edit_next:
                progressDialog.show();
                intentShare = new Intent();
                if (isBabyDataTagChoosed) {
                    intentShare.putExtra("show_tag", "yes");
                } else {
                    intentShare.putExtra("show_tag", "no");
                }
                intentShare.putExtra("baby_days", babyDays);
                intentShare.putExtra("baby_height", height);
                intentShare.putExtra("baby_weight", weight);

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        savePath =   FileUtils.saveCroppedImage(TagImageEditActivity.this, FileUtils.createBitmapFromView(tagImageView), false);
                        intentShare.putExtra("savePath",savePath);
                        intentShare.setClass(TagImageEditActivity.this, TagImageShareActivity.class);

                        intentShare.putExtra("savePath",savePath);
                        myHandler.sendEmptyMessage(0);
                    }
                }.start();
                break;
            case R.id.btn_tag_layout_layout:
                if (mTagChooseLayout.isShown()) {
                    btnTagPanel.setImageResource(R.drawable.icon_edit_tag_un);
                    mTagChooseLayout.setVisibility(View.GONE);
                    text_lable.setSelected(false);
                } else {
                    btnTagPanel.setImageResource(R.drawable.icon_edit_tag);
                    mTagChooseLayout.setVisibility(View.VISIBLE);
                    text_lable.setSelected(true);
                }
                break;
            case R.id.main_image_1:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        colorChange("000000");
        tagImageView.destroyDrawingCache();
    }
}
