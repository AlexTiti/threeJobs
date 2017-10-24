package com.findtech.threePomelos.mydevices.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.InstructionsMainActivity;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.bluetooth.CubicBLEDevice;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.home.musicbean.DeviceCarBean;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.datepicker.AdultWeightPickerDialog;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.util.List;


public class DeviceDetailActivity extends MyActionBarActivity implements View.OnClickListener, BLEDevice.RFStarBLEBroadcastReceiver, AdultWeightPickerDialog.OnWeightListener {

    private ImageView mImage, isnot_ble_car, isnot_ble_car_bac;
    private ImageView image_wheel_detail, iamge_bac__;
    private ImageView image_brake_detail;
    private TextView textView_toolbar;
   // private ImageView back;
    private ImageView img_is_tempurature;
    private RelativeLayout idBle_device;
    private RelativeLayout is_tempurature;
    private LinearLayout layout_ble_detail;
    private LinearLayout is_notBledevice;
    private MyApplication app;
    private SeekBar mSeekBar;
    private NetWorkRequest netWorkRequest;
    private TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
    private MyBroadcastReceiver mBroadcastReceiver;
    private TextView electric_rate;
    private TextView text_tempurature;
    private TextView text_speed_detail;
    private TextView text_today_mileage, text_today_calor;
    private TextView text_total_mileage, total_calories, weight_parent;
    private ImageView electricity_layout;
    private RelativeLayout image_elec_relayout,protect_layout;
    private int height;
    public static int RED_COLOR_LOW = 0xffED487A;
    public static int RED_COLOR_HIGH = 0xffB33647;
    public static int YELLO_COLOR_LOW = 0xffFCCA6D;
    public static int YELLO_COLOR_HIGH = 0xffCE6435;
    public static int GREEN_COLOR_LOW = 0xff4EE77A;
    public static int GREEN_COLOR_HIGH = 0xff117E47;
    boolean isRun = false;
    private TextView text_brake_state;
    private Button mButton;
    private ImageView image_back_speed;
    private RelativeLayout layout_hhh;
    private SwitchCompat mSwitchCompat,switch_brake_close;
    private ImageView image_edit_detail;
    private ImageView imageView[] = new ImageView[7];
    private int id[] = {R.id.ianme01, R.id.ianme02, R.id.ianme03, R.id.ianme04, R.id.ianme05, R.id.ianme06, R.id.ianme07};
    private AdultWeightPickerDialog weightPickerDialog;
    private String selectAddress;
    private String selectName;
    private String deviceFunction;
    BluetoothAdapter bleAdapter;
    BluetoothManager manager;
    boolean  isopen_Auto_brake = true;
    private RelativeLayout layout_seekbar_detail;
    private int progres;
    int elec;
    private OperateDBUtils operateDBUtils;
    private boolean car_protect_state;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Intent intent = getIntent();
        if (intent != null) {
            selectAddress = intent.getStringExtra(IContent.getInstacne().clickPositionAddress);
            selectName = intent.getStringExtra(IContent.getInstacne().clickPositionName);
            deviceFunction =  intent.getStringExtra(IContent.getInstacne().clickPositionFunction);
            L.e("=============deviceFunction","============deviceFunction=========="+deviceFunction);
        }
        if (selectName == null)
        setToolbar(getString(R.string.detail_car), true, null);
        else
        setToolbar(selectName, true, null);
        mImage = (ImageView) findViewById(R.id.image_electricity_bac);
        image_edit_detail = (ImageView) findViewById(R.id.image_edit_detail);
        image_wheel_detail = (ImageView) findViewById(R.id.image_wheel_detail);
        iamge_bac__ = (ImageView) findViewById(R.id.iamge_bac__);
        image_brake_detail = (ImageView) findViewById(R.id.image_brake_detail);
        idBle_device = (RelativeLayout) findViewById(R.id.idBle_device);
        is_tempurature = (RelativeLayout) findViewById(R.id.is_tempurature);
        img_is_tempurature = (ImageView) findViewById(R.id.img_is_tempurature);
        text_total_mileage = (TextView) findViewById(R.id.text_total_mileage);
        text_today_mileage = (TextView) findViewById(R.id.text_today_mileage);
        text_tempurature = (TextView) findViewById(R.id.text_tempurature);
        electric_rate = (TextView) findViewById(R.id.electric_rate);
        text_speed_detail = (TextView) findViewById(R.id.text_speed_detail);
        electricity_layout = (ImageView) findViewById(R.id.electricity_layout);
        image_elec_relayout = (RelativeLayout) findViewById(R.id.image_elec_relayout);
        layout_hhh = (RelativeLayout) findViewById(R.id.layout_hhh);
        text_brake_state = (TextView) findViewById(R.id.text_brake_state);
        weight_parent = (TextView) findViewById(R.id.weight_parent);
        total_calories = (TextView) findViewById(R.id.total_calories);
        text_today_calor = (TextView) findViewById(R.id.text_today_calor);
        image_back_speed = (ImageView) findViewById(R.id.image_back_speed);
        mSwitchCompat = (SwitchCompat) findViewById(R.id.switch_close);
        switch_brake_close = (SwitchCompat) findViewById(R.id.switch_brake_close);
        isnot_ble_car_bac = (ImageView) findViewById(R.id.isnot_ble_car_bac);
        isnot_ble_car = (ImageView) findViewById(R.id.isnot_ble_car);
        protect_layout = (RelativeLayout) findViewById(R.id.protect_layout);
        mSeekBar = (SeekBar) findViewById(R.id.voice_seekbar);
        layout_seekbar_detail = (RelativeLayout) findViewById(R.id.layout_seekbar_detail);
        layout_ble_detail = (LinearLayout) findViewById(R.id.layout_ble_detail);
        is_notBledevice = (LinearLayout) findViewById(R.id.is_notBledevice);
        mButton = (Button) findViewById(R.id.btn_close_open);
        refresh_Unlink_Close();
        layout_seekbar_detail.setFocusable(false);
        animator =(ObjectAnimator.ofFloat(image_wheel_detail, "rotation", new float[]{0.0F, -360.0F}));
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setDuration(3000L);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                L.e("============onAnimationStart","==============");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                L.e("============onAnimationEnd","==============");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                L.e("============onAnimationCancel","==============");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                L.e("============onAnimationRepeat","==============");
            }
        });


        mSeekBar.setMax(6);
        mSeekBar.setProgress(0);
       // back = (ImageView) findViewById(R.id.back);
        for (int i = 0; i < imageView.length; i++) {
            imageView[i] = (ImageView) findViewById(id[i]);
        }
       // back.setOnClickListener(this);
        image_edit_detail.setOnClickListener(this);
        app = (MyApplication) getApplication();
        operateDBUtils = new OperateDBUtils(this);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        image_elec_relayout.measure(w, h);
        height = image_elec_relayout.getMeasuredHeight();
        netWorkRequest = new NetWorkRequest(this);
        getCalor();
        getTravelInfo();
        initToolBar();
        initView();
        if (app.manager.cubicBLEDevice != null) {
            if (IContent.isBLE) {
                app.manager.cubicBLEDevice.registerReceiver();
                app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
                showProgressDialog(getResources().getString(R.string.data_update),getString(R.string.data_update_fail));
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.NOTIFY_DATA);
            } else {
                refresh_opne();
            }
        } else {
            showProgressDialog(getResources().getString(R.string.data_update), 25000 ,getString(R.string.data_update_fail));
            manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bleAdapter = manager.getAdapter();
            app.manager.bluetoothDevice = bleAdapter.getRemoteDevice(selectAddress);
            app.manager.isEnabled(this);
             app.manager.cubicBLEDevice = new CubicBLEDevice(
                    this, app.manager.bluetoothDevice);
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
        if ( travelInfoEntity.getAdultWeight().equals("0")){
            showNameBlueToothDialog();
        }
    }
    private void refresh_Unlink_Close(){
        if (  deviceFunction!= null && deviceFunction.equals("0")){
           L.e("==========","===============");
            idBle_device.setVisibility(View.GONE);
            layout_ble_detail.setVisibility(View.GONE);
            is_tempurature.setVisibility(View.VISIBLE);
            is_notBledevice.setVisibility(View.VISIBLE);
            text_speed_detail.setVisibility(View.GONE);
      }
    }

    private void getCalor() {
        netWorkRequest.getCalorFromServer(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        String calor = avObject.getString("totalCaloriesValue");
                        String adultWeight = avObject.getString("adultWeight");
                        travelInfoEntity.setAdultWeight(adultWeight);
                        travelInfoEntity.setTotalCalor(calor);
                        RequestUtils.getSharepreferenceEditor(DeviceDetailActivity.this).putString(OperateDBUtils.TOTAL_CALOR,
                                calor).commit();
                        RequestUtils.getSharepreferenceEditor(DeviceDetailActivity.this).putString(OperateDBUtils.ADULT_WEIGHT,
                                adultWeight).commit();
                    }
                    weight_parent.setText(travelInfoEntity.getAdultWeight());
                    total_calories.setText(travelInfoEntity.getTotalCalor());
                }else {
                    travelInfoEntity.setAdultWeight(RequestUtils.getSharepreference(DeviceDetailActivity.this).getString(OperateDBUtils.ADULT_WEIGHT, "56"));
                    travelInfoEntity.setTotalCalor(RequestUtils.getSharepreference(DeviceDetailActivity.this).getString(OperateDBUtils.TOTAL_CALOR, "0"));
                    weight_parent.setText(travelInfoEntity.getAdultWeight());
                    total_calories.setText(travelInfoEntity.getTotalCalor());
                }
            }
        });
    }
    private void getTravelInfo() {
        netWorkRequest.getTravelInfo(selectAddress, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        AVObject avObject = list.get(0);
                        travelInfoEntity.setTodayMileage(avObject.getString(OperateDBUtils.TODAY_MILEAGE));
                        travelInfoEntity.setTotalMileage(avObject.getString(OperateDBUtils.TOTAL_MILEAGE));
                        String todayCalor = avObject.getString("calorieValue");
                        travelInfoEntity.setTodayCalor(todayCalor );
                        RequestUtils.getSharepreferenceEditor(DeviceDetailActivity.this).putString(OperateDBUtils.TODAY_CALOR,
                                todayCalor).commit();
                    }
                }else {
                    travelInfoEntity.setTodayCalor(RequestUtils.getSharepreference(DeviceDetailActivity.this).getString(OperateDBUtils.TODAY_CALOR, "0"));
                }
                text_today_mileage.setText(travelInfoEntity.getTodayMileage());
                text_total_mileage.setText(travelInfoEntity.getTotalMileage());
                text_today_calor.setText(travelInfoEntity.getTodayCalor());
            }
        });
    }

    private void initToolBar() {
//        textView_toolbar = (TextView) findViewById(R.id.toolbar_title_detail);
//        textView_toolbar.setText(getResources().getString(R.string.pomoles_car));
        btn_menu_more.setVisibility(View.VISIBLE);
        btn_menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
    }

    private ObjectAnimator animator;
   // private WeakReference<ObjectAnimator> animatorWeakReference;
    Animation a;
    Animation y;
    AnimationSet animationSet;
    private void initAnimation() {

        a = new RotateAnimation(0, -12, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0.5f);
        y = new TranslateAnimation(0,0,0,-10);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(a);
        animationSet.addAnimation(y );
        animationSet.setDuration(1000);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.setFillAfter(true);
        image_brake_detail.setAnimation(animationSet);
        if (isRun) {
            text_brake_state.setText(getResources().getString(R.string.device_running));
            iamge_bac__.setVisibility(View.VISIBLE);
            L.e("=================="," animator.start();");
            animator.start();
            animationSet.start();
        } else {
            text_brake_state.setText(getResources().getString(R.string.device_isbrake));
            iamge_bac__.setVisibility(View.GONE);
            L.e("===================","=================="+String.valueOf(animator == null));
           animator.cancel();
           // animator.end();

           // image_wheel_detail.clearAnimation();
           // animator.cancel();
           // animator.end();

           image_wheel_detail.clearAnimation();
            image_brake_detail.clearAnimation();
        }
    }

    private void initColor(int i) {
        switch (i) {
            case 80:
                i = 100;
                break;
            case 40:
                i = 60;
                break;
            case 15:
                i = 20;
                break;
            case 5:
                i = 10;
                break;
            case 0:
                i = 0;
                break;
        }
        electric_rate.setText(getResources().getString(R.string.electricity, i + "%"));
        int color[] = new int[2];
        image_elec_relayout.setPadding(0, height * (100 - i) / 100, 0, 0);
        if (i <= 20) {
            electricity_layout.setImageResource(R.drawable.elec_bac_low);
            color[0] = RED_COLOR_LOW;
            color[1] = RED_COLOR_HIGH;
        } else if (i > 20 && i <= 60) {
            electricity_layout.setImageResource(R.drawable.elec_back);
            color[0] = YELLO_COLOR_LOW;
            color[1] = YELLO_COLOR_HIGH;
        } else if (i > 60 && i <= 100) {
            electricity_layout.setImageResource(R.drawable.elec_bac_high);
            color[0] = GREEN_COLOR_LOW;
            color[1] = GREEN_COLOR_HIGH;
        }
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color);
        mImage.setImageDrawable(bg);
    }

    private void initView() {
        if (IContent.isBLE) {
            mButton.setOnClickListener(this);
            mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BRAKE_CAR);
                        }
                    } else {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BRAKE_CAR_CLEAR);
                        }
                    }
                }
            });
            switch_brake_close.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (app.manager.cubicBLEDevice != null) {

                        byte b[] =  {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x0E, (byte) 0xE7};
                           app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE,b);
                        }
                    } else {
                        byte b[] =  {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x0D, (byte) 0xE8};
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, b);
                        }
                    }

                }
            });
        }
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OperateDBUtils.QUERY_FINISH);
        intentFilter.addAction(RequestUtils.RECEIVE_TEMPERATURE_ELECTRIC_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.back:
//                finish();
//                break;
//            case R.id.layout_seekbar_detail:
//                L.e("=================",isRun+"================================"+isopen_Auto_brake);
//                if (isRun && isopen_Auto_brake)
//                ToastUtil.showToast(DeviceDetailActivity.this,getString(R.string.run_action_notice));
//
//                break;
            case R.id.image_edit_detail:
                showNameBlueToothDialog();
                break;
            case R.id.btn_close_open:
                closeDevice();
                break;
            case R.id.layout_repair_popwindow:
                if (isRun) {
                    ToastUtil.showToast(DeviceDetailActivity.this, getString(R.string.run_action_notice));
                    return;
                }
                if (car_protect_state){
                    ToastUtil.showToast(DeviceDetailActivity.this, getString(R.string.pro_action_notice));
                    mPicChooserDialog.dismiss();
                    return;
                }
                mPicChooserDialog.dismiss();
                startActivityForResult(new Intent(DeviceDetailActivity.this, RepairActivity.class),100);
                break;
            case R.id.layout_update_popwindow:
                mPicChooserDialog.dismiss();
                startActivityForResult(new Intent(DeviceDetailActivity.this, DeviceUpdateActivity.class),100);
                break;
            case R.id.layout_changeName_popwindow:
                mPicChooserDialog.dismiss();
                showTextDialog();
                break;
            case R.id.layout_Instructions_popwindow:
                mPicChooserDialog.dismiss();
                netWorkRequest.getInstruction(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null){
                    if (list ==null || list.size() == 0)
                        return;
                    String url0 = null;
                    String url1 = null;
                    for (AVObject avObject : list){
                        L.e("============",avObject.getString(NetWorkRequest.COMPANY)+"============="+avObject.getString(NetWorkRequest.DEVICEIDENTIFITER));
                        if ( avObject.getString(NetWorkRequest.COMPANY).equals(IContent.getInstacne().company) && avObject.getString(NetWorkRequest.DEVICEIDENTIFITER).equals(IContent.getInstacne().clickPositionType)){
                            if (avObject.getString("function").equals("C")){
                                AVFile avFile =avObject.getAVFile("instruction_File");
                                url0  = avFile.getUrl();
                                L.e("url0================="+url0);
                            }else {
                                AVFile avFile1 = avObject.getAVFile("instruction_File");
                                 url1 = avFile1.getUrl();
                                L.e("url1================="+url1);
                            }

                        }
                    }
                    Intent intent = new Intent(DeviceDetailActivity.this, InstructionsMainActivity.class);
                    intent.putExtra("url0",url0);
                    intent.putExtra("url1",url1);
                    startActivity(intent);

                }else {
                    checkNetWork();
                }

            }
        });
                break;
            case R.id.cancle_button:
                mPopupdialog.dismiss();
                break;
            case R.id.layout_unbind_popwindow:
                mPicChooserDialog.dismiss();
                final CustomDialog.Builder builder = new CustomDialog.Builder(DeviceDetailActivity.this);
                builder.setTitle(getResources().getString(R.string.notice));
                builder.setShowBindInfo(getResources().getString(R.string.unbound_message));
                builder.setShowButton(true);
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.disconnectedDevice();
                        }

                        final String deviceNum = RequestUtils.getSharepreference(DeviceDetailActivity.this).getString(RequestUtils.DEVICE, "");
//                        netWorkRequest.deleteBlueToothIsBind(deviceNum, new DeleteCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.unbound_success));
//
//                                removeIContent(deviceNum);
//                                finish();
//                            }
//                        });

                        netWorkRequest.deleteBlueToothIsBind(deviceNum, new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    for (int i = 0; i < list.size(); i++) {
                                        AVObject avObjects = list.get(i);
                                        avObjects.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                dismissProgressDialog();
                                                if (e == null) {
                                                    ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.unbound_success));
                                                    removeIContent(deviceNum);
                                                    finish();
                                                } else {
                                                    checkNetWork();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                   dismissProgressDialog();
                                    checkNetWork();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancle),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
        }

    }

    private void removeIContent(String address) {

        for (int i = 0; i < IContent.getInstacne().addressList.size(); i++) {
            if (IContent.getInstacne().addressList.get(i).getDeviceaAddress().equals(address)) {
                IContent.getInstacne().addressList.remove(i);
            }
        }
    }

    private void closeDevice() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(DeviceDetailActivity.this);
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setShowBindInfo(getResources().getString(R.string.close_message));
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                if (app.manager.cubicBLEDevice != null) {
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.CLOSE_DEVICE);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    private Dialog mPopupdialog;
    private void showTextDialog() {
        View viewDialog = getLayoutInflater().inflate(R.layout.textview_popwindow, null);
        final EditText editText = (EditText) viewDialog.findViewById(R.id.editText_weight);
        editText.setHint(selectName);
        TextView leftButton = (TextView) viewDialog.findViewById(R.id.cancle_button);
        TextView rightButtton = (TextView) viewDialog.findViewById(R.id.confirm_button);
        mPopupdialog = new Dialog(this, R.style.MyDialogStyleBottom);
        mPopupdialog.setContentView(viewDialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = mPopupdialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
        mPopupdialog.show();
        leftButton.setOnClickListener(this);
        rightButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupdialog.dismiss();
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name))
                    sendNameToServer(name);
                else
                    ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.name_not));
            }
        });
    }
    private void sendNameToServer(final String name) {
        netWorkRequest.sendNameToServer(name, selectAddress, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    setToolbar(name, true, null);
                    ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.name_change_sucess));
                    refreshList();
                } else {
                    checkNetWork();
                }
            }
        });
    }
    private void refreshList() {
        netWorkRequest.getDeviceUser(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject avObject : list) {
                        String address = avObject.getString("bluetoothDeviceId");
                        String name = avObject.getString("bluetoothName");
                        String deviceType = avObject.getString(NetWorkRequest.FUNCTION_TYPE);
                        String functionType = avObject.getString(NetWorkRequest.FUNCTION_TYPE);
                        String company = avObject.getString(NetWorkRequest.COMPANY);
                        if (address != null) {
                            IContent.getInstacne().addressList.add(new DeviceCarBean(name, address, deviceType,functionType,company));
                        }
                    }
                } else {
                    checkNetWork();
                }

            }
        });
    }

    private void sendWeightToServer(final String weight) {
        netWorkRequest.sendAadultWeight(weight, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    weight_parent.setText(weight);
                } else {
                    checkNetWork();
                }
            }
        });
    }

    private Dialog mPicChooserDialog = null;

    TextView text_code;
    Button text_update_notice;

    private void showPopWindow() {
        if (app.manager.cubicBLEDevice != null) {
            byte bytes[] = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x0B, (byte) 0xEA};
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
        }

        View viewDialog = getLayoutInflater().inflate(R.layout.popwindow_item, null);
        LinearLayout layout_repair_popwindow = (LinearLayout) viewDialog.findViewById(R.id.layout_repair_popwindow);
        LinearLayout layout_unbind_popwindow = (LinearLayout) viewDialog.findViewById(R.id.layout_unbind_popwindow);
        LinearLayout layout_update_popwindow = (LinearLayout) viewDialog.findViewById(R.id.layout_update_popwindow);
        LinearLayout layout_code_popwindow = (LinearLayout) viewDialog.findViewById(R.id.layout_code_popwindow);
        LinearLayout layout_nameChange_popwindow = (LinearLayout) viewDialog.findViewById(R.id.layout_changeName_popwindow);
        LinearLayout layout_Instructions_popwindow = (LinearLayout) viewDialog.findViewById(R.id.layout_Instructions_popwindow);
        text_update_notice = (Button) viewDialog.findViewById(R.id.text_update_notice);
        layout_nameChange_popwindow.setOnClickListener(this);
        layout_update_popwindow.setOnClickListener(this);
        layout_repair_popwindow.setOnClickListener(this);
        layout_unbind_popwindow.setOnClickListener(this);
        layout_Instructions_popwindow.setOnClickListener(this);
        TextView textView = (TextView) viewDialog.findViewById(R.id.code_text);
        text_code = (TextView) viewDialog.findViewById(R.id.text_update_code);

        if (IContent.isBLE &&deviceFunction != null&& deviceFunction.equals("1")) {
            layout_update_popwindow.setVisibility(View.VISIBLE);
            layout_repair_popwindow.setVisibility(View.VISIBLE);
            layout_Instructions_popwindow.setVisibility(View.VISIBLE);
            layout_code_popwindow.setVisibility(View.GONE);
            L.e("==================",IContent.getInstacne().newCode+"======================="+IContent.getInstacne().code);
        } else {
            layout_update_popwindow.setVisibility(View.GONE);
            layout_repair_popwindow.setVisibility(View.GONE);
            layout_Instructions_popwindow.setVisibility(View.GONE);
            layout_code_popwindow.setVisibility(View.VISIBLE);
            textView.setText(getResources().getString(R.string.code, "V 1.0.1"));
        }
        mPicChooserDialog = new Dialog(this, R.style.MyDialogStyleBottom);
        mPicChooserDialog.setContentView(viewDialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = mPicChooserDialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(p);
        mPicChooserDialog.show();
    }
    private void showNameBlueToothDialog() {
        if (weightPickerDialog != null && weightPickerDialog.isShowing()) {
            return;
        }
        weightPickerDialog = new AdultWeightPickerDialog(this);
        weightPickerDialog.setOnWeightListener(this);
        DatepickerDialog.setWindowSize(weightPickerDialog);
        weightPickerDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isopen_Auto_brake) {
            if (IContent.isBLE && deviceFunction != null && deviceFunction.equals("1")) {
                L.e("============","=============="+IContent.isBLE);
                idBle_device.setVisibility(View.VISIBLE);
                layout_ble_detail.setVisibility(View.VISIBLE);
                is_tempurature.setVisibility(View.GONE);
                is_notBledevice.setVisibility(View.GONE);
                initAnimation();
            } else {
                idBle_device.setVisibility(View.GONE);
                layout_ble_detail.setVisibility(View.GONE);
                is_tempurature.setVisibility(View.VISIBLE);
                is_notBledevice.setVisibility(View.VISIBLE);
            }
        }
        text_speed_detail.setText(travelInfoEntity.getAverageSpeed());
        text_today_mileage.setText(travelInfoEntity.getTodayMileage());
        text_total_mileage.setText(travelInfoEntity.getTotalMileage());
    }

    public void refresh_close() {
        if (IContent.isBLE) {
            image_wheel_detail.setVisibility(View.GONE);
            image_brake_detail.setVisibility(View.GONE);
            iamge_bac__.setVisibility(View.GONE);
            electricity_layout.setImageResource(R.drawable.elec_back_close);
            image_back_speed.setImageResource(R.drawable.brake_bac_close);
            mButton.setEnabled(false);
            mButton.setBackgroundResource(R.drawable.btn_bac_close);
            initSeekBar(-1);
            mSwitchCompat.setEnabled(false);
            mSwitchCompat.setThumbResource(R.drawable.seek_button_close);
            switch_brake_close.setEnabled(false);
            switch_brake_close.setThumbResource(R.drawable.seek_button_close);
            mSeekBar.setEnabled(false);
            IContent.getInstacne().address = null;
            IContent.getInstacne().isBind = false;
            mSeekBar.setThumb(getResources().getDrawable(R.drawable.seek_button_close));
            image_elec_relayout.setVisibility(View.GONE);
            if (animator != null)
                animator.end();
            if (a != null)
                image_brake_detail.clearAnimation();
            layout_hhh.setVisibility(View.GONE);
            electric_rate.setText(getResources().getString(R.string.electricity, 00 + "%"));

        } else {
            img_is_tempurature.setImageResource(R.drawable.temperature_bac_close);
            image_elec_relayout.setVisibility(View.GONE);
            electricity_layout.setImageResource(R.drawable.elec_back_close);
            text_tempurature.setText("");
            electric_rate.setText(getResources().getString(R.string.electricity, "0"));
            text_speed_detail.setText("");
            is_tempurature.setVisibility(View.VISIBLE);
            isnot_ble_car.setVisibility(View.GONE);
            isnot_ble_car_bac.setImageResource(R.drawable.average_speed_close);
        }
    }

    public void refresh_opne() {
        if (IContent.isBLE) {
            electricity_layout.setImageResource(R.drawable.elec_back);
            image_back_speed.setImageResource(R.drawable.brake_bac);
            mButton.setBackgroundResource(R.drawable.button_close);
            mSwitchCompat.setThumbResource(R.drawable.play_plybar_btn);
            switch_brake_close.setThumbResource(R.drawable.play_plybar_btn);
            if ( !isRun || !isopen_Auto_brake) {
                L.e("==============",isRun+"=========="+isopen_Auto_brake);
                mSeekBar.setEnabled(true);
            }
            mSeekBar.setThumb(getResources().getDrawable(R.drawable.play_plybar_btn));
            image_elec_relayout.setVisibility(View.VISIBLE);
            layout_hhh.setVisibility(View.VISIBLE);
            electric_rate.setText(getResources().getString(R.string.electricity, 00 + "%"));

            if (isopen_Auto_brake) {
                L.e("======","================"+isopen_Auto_brake);
                image_wheel_detail.setVisibility(View.VISIBLE);
                image_brake_detail.setVisibility(View.VISIBLE);
                if (isRun)
                    iamge_bac__.setVisibility(View.VISIBLE);
                else
                    iamge_bac__.setVisibility(View.GONE);
            }
            initColor(elec);
        } else {
            L.e("==============","=================="+Integer.valueOf(travelInfoEntity.getElectric_rate()));
            initColor(Integer.valueOf(travelInfoEntity.getElectric_rate()));
            text_tempurature.setText(travelInfoEntity.getText_tempurature() + " ℃");
            image_elec_relayout.setVisibility(View.VISIBLE);
            img_is_tempurature.setImageResource(R.drawable.temperature_bac);
            isnot_ble_car.setVisibility(View.VISIBLE);
            isnot_ble_car_bac.setImageResource(R.drawable.average_speed);
            text_speed_detail.setVisibility(View.VISIBLE);
        }
    }

    private void saveDataToServer() {
        text_speed_detail.setText(travelInfoEntity.getAverageSpeed());
        text_today_mileage.setText(travelInfoEntity.getTodayMileage());
        text_total_mileage.setText(travelInfoEntity.getTotalMileage());
        if (IContent.getInstacne().address!= null)
        netWorkRequest.saveDataToServer(IContent.getInstacne().address, travelInfoEntity.getTodayMileage(), travelInfoEntity.getTotalMileage(), travelInfoEntity.getAverageSpeed(), IContent.getInstacne().clickPositionType, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    getCalor();
                    getTravelInfo();
                } else
                    checkNetWork();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            app.manager.cubicBLEDevice.closeDevice();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onReceive(Context context, Intent intent, final String macData, String uuid) {

        if (intent == null)
            return;

        String action = intent.getAction();
        if (action == null)
            return;
        if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
            L.e("=======================ACTION_GATT_CONNECTED","========================");
            netWorkRequest.updateUUIDCreatAt(macData, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    L.e("================",macData);
                }
            });
        } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
            IContent.getInstacne().isBind = false;
            IContent.getInstacne().address = null;
            IContent.getInstacne().deviceName = null;
            refresh_close();
        } else if(RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
            L.e("=======================ACTION_GATT_SERVICES_DISCOVERED","========================");
            IContent.getInstacne().isBind = true;
            IContent.getInstacne().address = macData;
            IContent.getInstacne().deviceName = selectName;

        } else if (RFStarBLEService.DESCRIPTOR_WRITER_DONE
                .equals(action)) {
            L.e("========================","============================");
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.NOTIFY_DATA);
        } else if (action.equals(RFStarBLEService.ACTION_WRITE_DONE)) {
            if (IContent.getInstacne().WRITEVALUE != null)
                app.manager.cubicBLEDevice.readValue(IContent.SERVERUUID_BLE, IContent.READUUID_BLE, IContent.getInstacne().WRITEVALUE);
        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {
            dismissProgressDialog();
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);

            if (!IContent.isBLE) {
                return;
            }
            if (data[3] == (byte) 0x81 && data[4] == 0x03) {
                if (data[5] == 0x01) {
                    IContent.getInstacne().SD_Mode = true;
                }
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x08) {
                toSendData(data);
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x01) {
                int averageSpeed0 = data[6] & 0xff;
                int today_mileage0 = data[8] & 0xff;
                int total_mileage0 = data[10] & 0xff;
                int averageSpeed1 = (data[5] << 8) & 0xff;
                int today_mileage1 = (data[7] << 8) & 0xff;
                int total_mileage1 = (data[9] << 8) & 0xff;
                double today_mileage = (today_mileage0 + today_mileage1) * 1.0 / 100;
                double total_mileage = (total_mileage0 + total_mileage1) * 1.0 / 10;
                double averageSpeed = (averageSpeed0 + averageSpeed1) * 1.0 / 10;


                travelInfoEntity.setTodayMileage(String.valueOf(today_mileage));
                travelInfoEntity.setTotalMileage(String.valueOf(total_mileage));
                travelInfoEntity.setAverageSpeed(String.valueOf(averageSpeed));
                saveDataToServer();
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x07) {
                elec = data[5] & 0xff;
                refresh_opne();
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x04) {
                if (data[05] == 0x00) {
                    isRun = true;
                   if (isopen_Auto_brake) {
                    L.e("======================","=====================================");
                        mSeekBar.setEnabled(false);
                       layout_seekbar_detail.setEnabled(true);
                        layout_seekbar_detail.setFocusable(true);
                        layout_seekbar_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtil.showToast(DeviceDetailActivity.this,getString(R.string.run_action_notice));
                        }
                    });
                    }
                } else if (data[05] == 0x55) {
                    L.e("======================","=====================================data[05] == 0x55");
                    isRun = false;
                    mSeekBar.setEnabled(true);
                    layout_seekbar_detail.setFocusable(false);
                    layout_seekbar_detail.setOnClickListener(null);
                }
                onResume();
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x05) {

                if (data[05] == 0x01) {
                    mSwitchCompat.setChecked(true);
                    switch_brake_close.setEnabled(false);
                    switch_brake_close.setThumbResource(R.drawable.seek_button_close);
                    car_protect_state = true;
                } else {
                    mSwitchCompat.setChecked(false);
                    switch_brake_close.setEnabled(true);
                    switch_brake_close.setThumbResource(R.drawable.play_plybar_btn);
                    car_protect_state = false;
                }
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x09) {
                mSeekBar.setProgress(data[5]);
                initSeekBar(data[5]);
            }
            if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
                ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.close_sucess));
                if (app.manager.cubicBLEDevice != null)
                    app.manager.cubicBLEDevice.disconnectedDevice();
                refresh_close();
                return;
            }
            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D){
                switch_brake_close.setChecked(false);
                protect_layout.setAlpha(0.3f);
                mSwitchCompat.setThumbResource(R.drawable.seek_button_close);
                mSwitchCompat.setEnabled(false);
                refresh_wheel_close();
            }
            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E){
                switch_brake_close.setChecked(true);
                protect_layout.setAlpha(1f);
                mSwitchCompat.setThumbResource(R.drawable.play_plybar_btn);
                mSwitchCompat.setEnabled(true);
                refresh_wheel_open();
            }

            if (data[3] == (byte) 0x83 ) {
                L.e("=========doMusic(data)==========",Tools.byte2Hex(data));
                doMusic(data);
            }


        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE_READ)) {
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
                ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.close_sucess));
                if (app.manager.cubicBLEDevice != null)
                    app.manager.cubicBLEDevice.disconnectedDevice();

                refresh_close();
                return;
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x05) {
                if (data[05] == 0x01) {
                    mSwitchCompat.setChecked(true);
                    switch_brake_close.setEnabled(false);
                    switch_brake_close.setThumbResource(R.drawable.seek_button_close);
                    car_protect_state = true;
                }
//                if (animation != null)
//                    image_wheel_detail.clearAnimation();
//                if (a != null)
//                    image_brake_detail.clearAnimation();
                return;
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x06) {
                if (data[05] == 0x00) {
                    mSwitchCompat.setChecked(false);
                    switch_brake_close.setEnabled(true);
                    switch_brake_close.setThumbResource(R.drawable.play_plybar_btn);
                    car_protect_state = false;
                }
//                if (animation != null)
//                    animation.start();
//                if (a != null)
//                    a.start();
                return;
            }
            if (data[3] == (byte) 0x8B && data[4] == 0x09) {
                mSeekBar.setProgress(data[5]);
                initSeekBar(data[5]);
            }
            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0B) {
                String str = "V" + data[5] + "." + data[6] + "." + data[7];

                IContent.getInstacne().code = str;
                text_code.setText(str);
                L.e("===============",IContent.getInstacne().newCode+"================"+IContent.getInstacne().code);
                if (IContent.getInstacne().newCode != null && !IContent.getInstacne().newCode.equals(IContent.getInstacne().code))
                    text_update_notice.setVisibility(View.VISIBLE);
                else
                    text_update_notice.setVisibility(View.INVISIBLE);
                L.e("===============","================"+text_code.getText()+"====");

                return;
            }
            if (data[3] == (byte) 0x8B && data[4] == (byte)0x0D) {
                switch_brake_close.setChecked(false);
                protect_layout.setAlpha(0.3f);
                mSwitchCompat.setThumbResource(R.drawable.seek_button_close);
                mSwitchCompat.setEnabled(false);
                refresh_wheel_close();
                return;
            }
            if (data[3] == (byte) 0x8B && data[4] == (byte)0x0E) {
                switch_brake_close.setChecked(true);
                mSwitchCompat.setThumbResource(R.drawable.play_plybar_btn);
                protect_layout.setAlpha(1f);
                mSwitchCompat.setEnabled(true);
                refresh_wheel_open();
                return;
            }
        }
    }
    private void toSendData(byte b[]){
      String data_message =  Tools.byte2Hex(b);
        netWorkRequest.sendDataMessage(data_message, Tools.getCurrentDateHour(), selectAddress,IContent.getInstacne().clickPositionType  , new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null){
                    checkNetWork();
                }
            }
        });
    }
    private void refresh_wheel_close(){
        mSeekBar.setEnabled(true);

        layout_seekbar_detail.setEnabled(false);
        layout_seekbar_detail.setFocusable(false);
        layout_seekbar_detail.setOnClickListener(null);

        text_brake_state.setText(getResources().getString(R.string.device_close));
        isopen_Auto_brake = false;
        image_wheel_detail.setVisibility(View.GONE);
        image_brake_detail.setVisibility(View.GONE);
        iamge_bac__.setVisibility(View.GONE);
        image_back_speed.setImageResource(R.drawable.brake_bac_close);
    }

    private void refresh_wheel_open(){
        isopen_Auto_brake = true;
       // if (isRun) {
            L.e("===============","===================="+isRun);
            mSeekBar.setEnabled(true);
            layout_seekbar_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showToast(DeviceDetailActivity.this,getString(R.string.run_action_notice));
                }
            });
        //}else {
            L.e("===============","===================="+isRun);
//            mSeekBar.setEnabled(true);
//            layout_seekbar_detail.setOnClickListener(null);
       // }
        image_wheel_detail.setVisibility(View.VISIBLE);
        image_brake_detail.setVisibility(View.VISIBLE);
        if (isRun) {
            text_brake_state.setText(getResources().getString(R.string.device_running));
            iamge_bac__.setVisibility(View.VISIBLE);
        }
        else {
            text_brake_state.setText(getResources().getString(R.string.device_isbrake));
            iamge_bac__.setVisibility(View.GONE);
        }
        image_back_speed.setImageResource(R.drawable.brake_bac);
    }

    private void initSeekBar(int data) {
        for (int i = 0; i < imageView.length; i++) {
            if (i <= data)
                imageView[i].setImageResource(R.drawable.red_circle);
            else
                imageView[i].setImageResource(R.drawable.gray_circle);
        }
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progres = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                L.e("=====","onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                byte bytes[] = {0x55, (byte) 0xAA, 0x01, 0x0B, 0x09, (byte) progres, (byte) (0 - (0x01 + 0x0B + 0x09 + (byte) progres))};
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
            }
        });
    }

    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {
        if (RequestUtils.TRAVEL_INFO.equals(dataType)) {
            if (travelInfoEntity != null) {
                text_speed_detail.setText(travelInfoEntity.getAverageSpeed());
                text_today_mileage.setText(travelInfoEntity.getTodayMileage());
                text_total_mileage.setText(travelInfoEntity.getTotalMileage());


            }
        }
        if (RequestUtils.WEIGHT.equals(dataType)) {
            RequestUtils.getSharepreferenceEditor(this).putString(RequestUtils.WEIGHT, data).commit();
            operateDBUtils.queryUserWeightData();
        }
        L.e("============","==================onReceiveDataAvailable");


    }

    @Override
    public void onClick(String weight) {
        weightPickerDialog.dismiss();
        sendWeightToServer(weight);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getExtras() != null) {

                if (intent.getAction().equals(OperateDBUtils.QUERY_FINISH)) {
                    String data = intent.getExtras().getString(OperateDBUtils.QUERY_DATA);
                }
                if (intent.getAction().equals(RequestUtils.RECEIVE_TEMPERATURE_ELECTRIC_ACTION)) {
                    String temperature = intent.getExtras().getString(RequestUtils.TEMPERATURE);
                    String electric = intent.getExtras().getString(RequestUtils.CURRENT_ELECTRIC) + "%";
                    electric_rate.setText(getResources().getString(R.string.electricity, electric));
                    electric = electric.replace("%", "");
                    initColor(Integer.valueOf(electric));
                    text_tempurature.setText(temperature + " ℃");
                    travelInfoEntity.setElectric_rate(electric);
                    travelInfoEntity.setText_tempurature(temperature);
                    refresh_opne();
                    onResume();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (app.manager.cubicBLEDevice != null ) {
            byte b[] = Tools.getDateTimeSplit();
            byte bytes[] = {0x55, (byte) 0xAA, 0x07, 0x0B, 0x0A, b[0], b[1], b[2], b[3], b[4], b[5], b[6], (byte) (0 - (0x07 + 0x0B + 0x0A + b[0] + b[1] + b[2] + b[3] + b[4] + b[5] + b[6]))};
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.e("============","===================="+requestCode);
        if (app.manager.cubicBLEDevice != null) {
            if (IContent.isBLE) {
                app.manager.cubicBLEDevice.registerReceiver();
                app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
            }
        }



    }
}
