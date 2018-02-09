package com.findtech.threePomelos.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.adapter.BabyInfoAdapter;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.home.fragment.UserFragment;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.FileUtils;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;
import com.findtech.threePomelos.view.datepicker.NativePickerDialog;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhi-zhang on 2016/2/13.
 */
public class BabyInfoActivity extends MyActionBarActivity implements View.OnClickListener, RequestUtils.MyItemClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayout inputName;
    private TextView babyNameView, totalMileage, todayMileage, averageSpeed;
    private EditText edittext;
    private BabyInfoAdapter mAdapter;
    private CircleImageView mBabyImage;
    private DatepickerDialog mChangeBirthDialog = null;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private Map<String, String> listItem = new HashMap<>();
    private String[] title = {"性别", "生日", "年龄", "身高", "体重", "籍贯"};
    private int title_id[] = {R.string.sex_baby,R.string.birth_baby,R.string.age_bany,R.string.height_baby,R.string.weight_baby,R.string.address};
    private BabyInfoEntity babyInfoEntity;
    private OperateDBUtils operateDBUtils;
    private NetWorkRequest netWorkRequest;
    private ProgressDialog progressDialog;
    private AVObject postBabyName, postBabySex, postBirthday, postBabyNative, postBabyImageHead;
    private Bitmap bitmap;
    private Context mContext = BabyInfoActivity.this;
    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
    private NativePickerDialog mNativePickerDialog = null;
    Intent intent_0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_info);
        setToolbar(getResources().getString(R.string.title_activity_baby_info), true, null);
        inputName = (LinearLayout) findViewById(R.id.input_name);
        inputName.setOnClickListener(this);
        babyNameView = (TextView) findViewById(R.id.baby_name);
        totalMileage = (TextView) findViewById(R.id.total_mileage_info);
        todayMileage = (TextView) findViewById(R.id.today_mileage_info);
        averageSpeed = (TextView) findViewById(R.id.average_speed_info);
        mBabyImage = (CircleImageView) findViewById(R.id.image_baby_info);
        mBabyImage.setOnClickListener(this);
        mAdapter = new BabyInfoAdapter(this);
        mAdapter.setTitle(title);
        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.baby_info_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PicOperator.initFilePath();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        babyInfoEntity = BabyInfoEntity.getInstance();
        operateDBUtils = new OperateDBUtils(this);
        netWorkRequest = new NetWorkRequest(this);
        progressDialog = netWorkRequest.getProgressDialog();
        intent_0 = new Intent();
        registerMusicBroadcast();
    }

    private void initData(){
        for (int i=0;i<title.length;i++){
            title[i] = getString(title_id[i]);
        }
    }

    @Override
    protected void onResume() {
        getContentResolver().registerContentObserver(OperateDBUtils.HEIGHT_URI, true, contentObserver);
        getContentResolver().registerContentObserver(OperateDBUtils.WEIGHT_URI, true, contentObserver);
        getContentResolver().registerContentObserver(OperateDBUtils.BABYINFO_URI, true, contentObserver);
        String weight = RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT, "");
        String height = RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT, "");
        totalMileage.setText(getResources().getString(R.string.xliff_total_mileage_num,
                travelInfoEntity.getTotalMileage()));
        todayMileage.setText(getResources().getString(R.string.xliff_today_mileage_num,
                travelInfoEntity.getTodayMileage()));
        averageSpeed.setText(getResources().getString(R.string.xliff_average_speed_num,
                travelInfoEntity.getAverageSpeed()));
        bitmap = PicOperator.getIconFromData(this);
        if (bitmap != null) {
            mBabyImage.setImageBitmap(bitmap);
        } else {
            mBabyImage.setImageResource(R.mipmap.personal_head_bg2_nor2x);
        }
        if (babyInfoEntity.getBabyName()!= null) {
            babyNameView.setText(babyInfoEntity.getBabyName());
        }
        final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
        try {
            if (babyInfoEntity.getBirthday() != null && !babyInfoEntity.getBirthday().equals(getString(R.string.input_birth_baby))) {
                String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
                MyCalendar myCalendar = new MyCalendar(birthday, currentDate,this);
                listItem.put(title[2], myCalendar.getDate());
            } else {
                listItem.put(title[2], getString(R.string.input_birth_notice));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        listItem.put(title[0], babyInfoEntity.getBabySex());
        listItem.put(title[1], babyInfoEntity.getBirthday());
        listItem.put(title[3], getResources().getString(R.string.xliff_height_num, height));
        listItem.put(title[4], getResources().getString(R.string.xliff_weight_num, weight));
        listItem.put(title[5], babyInfoEntity.getBabyNative());
        mAdapter.setListItem(listItem);
        super.onResume();
    }

    @Override
    public void onItemClick(View view, int position) {

        switch (position) {
            case 0:
                showInputBabySexDialog();
                break;
            case 1:
                showInputBabyBirthdayDataPicker();
                break;
            case 2:
                ToastUtil.showToast(this,getString(R.string.age_info_notice));
                break;
            case 3:
                ToastUtil.showToast(this,getString(R.string.height_ino_notice));
                break;
            case 4:
                ToastUtil.showToast(this,getString(R.string.weight_ino_notice));
                break;
            case 5:
                showInputBabyNativeDialog();
                break;
        }
    }
    private void showInputBabyNativeDialog() {
        if (mNativePickerDialog != null && mNativePickerDialog.isShowing()) {
            return;
        }

        mNativePickerDialog = new NativePickerDialog(this, 2);
        NativePickerDialog.setWindowSize(mNativePickerDialog);
        mNativePickerDialog.show();
        mNativePickerDialog.setNativeListener(new NativePickerDialog.OnNativeListener() {
            @Override
            public void onClick(String province, String city, String county) {
                final String nativeStr = province + "," + city;
                progressDialog.show();
                if (babyInfoEntity.getBabyInfoObjectId() != null) {
                    AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                    query.getInBackground(babyInfoEntity.getBabyInfoObjectId(), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            if (e == null) {
                                postBabyNative = avObject;
                                postBabyNative.put(OperateDBUtils.BABYNATIVE, nativeStr);
                                postBabyNative.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        progressDialog.dismiss();
                                        if (e == null) {
                                            listItem.put(title[5], nativeStr);
                                            babyInfoEntity.setBabyNative(nativeStr, "");
                                            updateBabyInfoDB(babyInfoEntity);
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.show();
                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                    netWorkRequest.getBabyInfoDataAndSaveToDB();
                }
                mNativePickerDialog = null;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == inputName) {
            showInputBabyNameDialog();
        } else if (view == mBabyImage) {
            PackageManager pm = getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", this.getPackageName()));
            if (permission) {
                showPicChooserDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }

    private void showInputBabyNameDialog() {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.edit_nikename));
        builder.setShowEditView(true);
        builder.setShowButton(true);
        builder.setShowSelectSex(false);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!TextUtils.isEmpty(edittext.getText().toString())) {
                    String oldName = babyInfoEntity.getBabyName();
                    if (!(edittext.getText().toString()).equals(oldName)) {
                        progressDialog.show();
                        if (babyInfoEntity.getBabyInfoObjectId() != null) {
                            AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                            query.getInBackground(babyInfoEntity.getBabyInfoObjectId(), new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    if (e == null) {
                                        postBabyName = avObject;
                                        postBabyName.put(OperateDBUtils.BABYNAME, edittext.getText().toString());
                                        postBabyName.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                progressDialog.dismiss();
                                                if (e == null) {
                                                    babyNameView.setText(edittext.getText().toString());
                                                    babyInfoEntity.setBabyName(edittext.getText().toString(), getResources().getString(R.string.baby_niName));
                                                    updateBabyInfoDB(babyInfoEntity);
                                                } else {
                                                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.show();
                            Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                            netWorkRequest.getBabyInfoDataAndSaveToDB();
                        }
                    }
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        customDialog = builder.create();
        edittext = (EditText) customDialog.findViewById(R.id.input_data);
        customDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                builder.showKeyboard();
            }
        }, 200);
    }

    private void showInputBabySexDialog() {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.hint_sex));
        builder.setShowEditView(false);
        builder.setShowButton(false);
        builder.setShowSelectSex(true);
        customDialog = builder.create();

        RadioGroup group = (RadioGroup) customDialog.findViewById(R.id.radioGroup);
        RadioButton radioMale = (RadioButton) customDialog.findViewById(R.id.radioMale);
        RadioButton radioFemale = (RadioButton) customDialog.findViewById(R.id.radioFemale);
        String babySex = babyInfoEntity.getBabySex();
        if (getResources().getString(R.string.princeling).equals(babySex)) {
            radioMale.setChecked(true);
        } else if (getResources().getString(R.string.princess).equals(babySex)) {
            radioFemale.setChecked(true);
        }
        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        customDialog.dismiss();
                    }
                }, 300);
            }
        });
        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        customDialog.dismiss();
                    }
                }, 300);
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String male = getResources().getString(R.string.princeling);
            String female = getResources().getString(R.string.princess);

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == R.id.radioMale) {
                    progressDialog.show();
                    if (babyInfoEntity.getBabyInfoObjectId() != null) {
                        AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                        query.getInBackground(babyInfoEntity.getBabyInfoObjectId(), new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                if (e == null) {
                                    postBabySex = avObject;
                                    postBabySex.put(OperateDBUtils.BABYSEX, male);
                                    postBabySex.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                listItem.put(title[0], male);
                                                babyInfoEntity.setBabySex(male, male);
                                                updateBabyInfoDB(babyInfoEntity);
                                                mAdapter.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                        netWorkRequest.getBabyInfoDataAndSaveToDB();
                    }
                } else if (radioButtonId == R.id.radioFemale) {
                    progressDialog.show();
                    if (babyInfoEntity.getBabyInfoObjectId() != null) {
                        AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                        query.getInBackground(babyInfoEntity.getBabyInfoObjectId(), new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                if (e == null) {
                                    postBabySex = avObject;
                                    postBabySex.put(OperateDBUtils.BABYSEX, female);
                                    postBabySex.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                listItem.put(title[0], female);
                                                babyInfoEntity.setBabySex(female, female);
                                                updateBabyInfoDB(babyInfoEntity);
                                                mAdapter.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                        netWorkRequest.getBabyInfoDataAndSaveToDB();
                    }
                }
            }
        });
        customDialog.show();
    }

    private void showInputBabyBirthdayDataPicker() {
        if (mChangeBirthDialog != null && mChangeBirthDialog.isShowing()) {
            return;
        }
        mChangeBirthDialog = new DatepickerDialog(mContext);
        DatepickerDialog.setWindowSize(mChangeBirthDialog);
        mChangeBirthDialog.show();
        mChangeBirthDialog.setBirthdayListener(new DatepickerDialog.OnBirthListener() {
            @Override
            public void onClick(String year, String month, String day) {
               //final String birthdayStr = year + "年" + month + "月" + day + "日";
                final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
                final String birthdayStrFormat = year + "-" + month + "-" + day;
                progressDialog.show();
                if (babyInfoEntity.getBabyInfoObjectId() != null) {
                    AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                    query.getInBackground(babyInfoEntity.getBabyInfoObjectId(), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            if (e == null) {
                                postBirthday = avObject;
                                postBirthday.put(OperateDBUtils.BIRTHDAY, birthdayStrFormat);
                                postBirthday.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        progressDialog.dismiss();
                                        if (e == null) {
                                            listItem.put(title[1], birthdayStrFormat);
                                            babyInfoEntity.setBirthday(birthdayStrFormat, "");
                                            updateBabyInfoDB(babyInfoEntity);
                                            try {
                                                MyCalendar myCalendar = new MyCalendar(birthdayStrFormat, currentDate,BabyInfoActivity.this);
                                                listItem.put(title[2], myCalendar.getDate());
                                                babyInfoEntity.setBabyTotalDay(BabyInfoActivity.this, birthdayStrFormat, "0");
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.show();
                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                    netWorkRequest.getBabyInfoDataAndSaveToDB();
                }
                mChangeBirthDialog = null;
            }
        });
    }

    Dialog mPicChooserDialog;

    private void showPicChooserDialog() {
        View viewDialog = getLayoutInflater().inflate(R.layout.dialog_pic_chooser, null);
        mPicChooserDialog = new Dialog(mContext, R.style.MyDialogStyleBottom);
        mPicChooserDialog.setContentView(viewDialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = mPicChooserDialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = dm.widthPixels;
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.6f; // 设置黑暗度
        p.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(p);
        mPicChooserDialog.show();

        TextView btnCamera = (TextView) viewDialog.findViewById(R.id.btn_take_photo);
        TextView btnGallery = (TextView) viewDialog.findViewById(R.id.btn_pick_photo);
        TextView btnCancel = (TextView) viewDialog.findViewById(R.id.btn_cancel);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mIntent.putExtra(MediaStore.EXTRA_OUTPUT, PicOperator.SOURCE_IMAGE_URI);
                startActivityForResult(mIntent, CODE_CAMERA_REQUEST);
                mPicChooserDialog.dismiss();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, CODE_GALLERY_REQUEST);
                mPicChooserDialog.dismiss();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChooserDialog.dismiss();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), getResources().getString(R.string.cancle), Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                String path = FileUtils.getPath(this, intent.getData());
                PicOperator.SOURCE_IMAGE_URI = Uri.parse("file://" + path);
                PicOperator.toCropImageActivity(this, PicOperator.SOURCE_IMAGE_URI, PicOperator.OUTPUT_IMAGE_URI, 300, 300,
                        CODE_RESULT_REQUEST);
                break;
            case CODE_CAMERA_REQUEST:
                PicOperator.toCropImageActivity(this, PicOperator.SOURCE_IMAGE_URI, PicOperator.OUTPUT_IMAGE_URI, 300, 300,
                        CODE_RESULT_REQUEST);
                break;
            case CODE_RESULT_REQUEST:
                if (PicOperator.OUTPUT_IMAGE_URI != null) {
                    final Bitmap bitmap = PicOperator.decodeUriAsBitmap(this, PicOperator.OUTPUT_IMAGE_URI);
                    if (bitmap == null) {
                        return;
                    }
                    progressDialog.show();
                    if (babyInfoEntity.getBabyInfoObjectId() != null) {
                        AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                        query.getInBackground(babyInfoEntity.getBabyInfoObjectId(), new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                if (e == null) {
                                    postBabyImageHead = avObject;
                                    AVFile avFile = new AVFile(MyApplication.getInstance().getHeadIconPath(),
                                            PicOperator.bitmap2Bytes(bitmap));
                                    avFile.saveInBackground();
                                    postBabyImageHead.put(OperateDBUtils.HEADIMG, avFile);
                                    postBabyImageHead.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                PicOperator.saveToData(mContext, bitmap);
                                                mBabyImage.setImageBitmap(PicOperator.toRoundBitmap(bitmap));
                                                intent_0.putExtra("intent", UserFragment.CHANGE);
                                                L.e("====================","==========================intent_0.putExtra(\"intent\",\"CHANGE\");");
                                                setResult(10,intent_0);

                                            } else {
                                                Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.show();
                        Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                        netWorkRequest.getBabyInfoDataAndSaveToDB();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getContentResolver().unregisterContentObserver(contentObserver);
    }

    ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.getPath().contains(OperateDBUtils.TABLE_HEIGHT)) {
                operateDBUtils.queryUserHeightData();
                RequestUtils.mUserHeightChangeListener.userHeightChange();
            }
            if (uri.getPath().contains(OperateDBUtils.TABLE_WEIGHT)) {
                operateDBUtils.queryUserWeightData();
                RequestUtils.mUserWeightChangeListener.userWeightChange();
            }

        }
    };

    private void updateBabyInfoDB(BabyInfoEntity babyInfoEntity) {
        String where = OperateDBUtils.USER_ID + " = ? ";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        values.put(OperateDBUtils.BABY_INFO_OBJECT_ID, babyInfoEntity.getBabyInfoObjectId());
        values.put(OperateDBUtils.BABYNAME, babyInfoEntity.getBabyName());
        values.put(OperateDBUtils.BABYSEX, babyInfoEntity.getBabySex());
        values.put(OperateDBUtils.BIRTHDAY, babyInfoEntity.getBirthday());
        values.put(OperateDBUtils.BABYNATIVE, babyInfoEntity.getBabyNative());
        values.put(OperateDBUtils.HEADIMG, babyInfoEntity.getImage());
        values.put(OperateDBUtils.IS_BIND, babyInfoEntity.getIsBind() ? 1 : 0);
        values.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, babyInfoEntity.getBabyInfoObjectId());
        getContentResolver().update(OperateDBUtils.BABYINFO_URI, values, where,
                new String[]{AVUser.getCurrentUser().getObjectId()});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPicChooserDialog();
            } else {
                Toast.makeText(this, getResources().getString(R.string.text_toast_no_storage_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
