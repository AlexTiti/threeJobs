package com.findtech.threePomelos.home.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.BabyDataDetailActivity;
import com.findtech.threePomelos.activity.BabyInfoActivity;
import com.findtech.threePomelos.activity.TagImageEditActivity;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.BaseLazyFragment;
import com.findtech.threePomelos.bluetooth.AppManager;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.home.MainHomeActivity;
import com.findtech.threePomelos.home.musicbean.DeviceCarBean;
import com.findtech.threePomelos.login.ThirdPartyController;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.mydevices.activity.BluetoothlinkActivity;
import com.findtech.threePomelos.mydevices.activity.DeviceDetailActivity;
import com.findtech.threePomelos.mydevices.adapter.BluetoothLinkAdapter;
import com.findtech.threePomelos.mydevices.bean.BluetoothLinkBean;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.DialogUtil;
import com.findtech.threePomelos.utils.FileUtils;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.HeadZoomScrollView;
import com.findtech.threePomelos.view.MyListView;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Alex on 2017/5/3.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */


public class PageFragment extends BaseLazyFragment implements View.OnClickListener, HeadZoomScrollView.OnScrollListener, ItemClickListtener, BluetoothLinkAdapter.LongClick {
    LinearLayout heightLayout;
    LinearLayout weightLayout;
    CircleImageView mBabyInfoView;
    ImageView btnImageChoosePic;
    TextView weightNum;
    TextView babyName;
    TextView heightNum;
    TextView babyAge;
    TextView heightHealthState;
    TextView weightHealthState;
    private ListView mylist_showcar_page;
    private RelativeLayout relativeLayout;
    private ImageView image_searchcar;
    private String photoPath = null, tempPhotoPath, camera_path;
    private File mCurrentPhotoFile;
    private ThirdPartyController mThirdPartyController;
    PicOperator picOperator;
    private NetWorkRequest netWorkRequest;
    private OperateDBUtils operateDBUtils;
    private boolean isBind, onceShow;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();
    private String deviceNum;
    private String babyTotalMonth, oldBabyTotalMonth, oldBabySex, nowBabySex;
    private Bitmap bitmap;
    private HeadZoomScrollView headZoomScrollView;
    private ImageView iv;
    private View view1;
    private TextView text_add_car_page;
    private BluetoothLinkAdapter bluetoothLinkAdapter;
    private static final String TAG = "PageFragment";
    private int position;
    private IContent content;
    BluetoothAdapter bleAdapter;
    BluetoothManager manager;
    CloseReceiver closeReceiver;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main_h;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        view1 = view.findViewById(R.id.view_test);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(getActivity());
        view1.setLayoutParams(layoutParams);
        netWorkRequest = new NetWorkRequest(mContext);
        content = IContent.getInstacne();
        refreshdata();
        if (content.newCode == null) {
            netWorkRequest.downUpDateOr(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        if (list.size() != 0) {
                            AVObject avObject = list.get(0);
                            String code = avObject.getString("fileVersion");
                            String codeUrl = avObject.getAVFile("imageNotice").getUrl();
                            if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(codeUrl)) {
                                content.newCode = code;
                                content.newCodeUrl = codeUrl;
                            }
                        }
                    }
                }
            });
        }
        text_add_car_page = (TextView) view.findViewById(R.id.text_add_car_page);
        heightLayout = (LinearLayout) view.findViewById(R.id.height_layout);
        weightLayout = (LinearLayout) view.findViewById(R.id.weight_layout);
        mBabyInfoView = (CircleImageView) view.findViewById(R.id.baby_image);
        btnImageChoosePic = (ImageView) view.findViewById(R.id.btn_image_choose_pic);
        weightNum = (TextView) view.findViewById(R.id.weight_num);
        heightNum = (TextView) view.findViewById(R.id.height_num);
        babyName = (TextView) view.findViewById(R.id.baby_name);
        babyAge = (TextView) view.findViewById(R.id.text_id_below);
        heightHealthState = (TextView) view.findViewById(R.id.height_health_state);
        weightHealthState = (TextView) view.findViewById(R.id.weight_health_state);
        mylist_showcar_page = (ListView) view.findViewById(R.id.mylist_showcar_page);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.layout_top_page);
        headZoomScrollView = (HeadZoomScrollView) view.findViewById(R.id.dzsv);
        image_searchcar = (ImageView) view.findViewById(R.id.image_searchcar);
        iv = (ImageView) view.findViewById(R.id.iv);
        if (Tools.getTime() >= 7 && Tools.getTime() < 19) {
            babyName.setTextColor(getResources().getColor(R.color.grey_color));
            babyAge.setTextColor(getResources().getColor(R.color.grey_color));
            iv.setImageResource(R.drawable.bg_day);
        } else {
            babyName.setTextColor(getResources().getColor(R.color.white));
            babyAge.setTextColor(getResources().getColor(R.color.white));
            iv.setImageResource(R.drawable.bg_night);
        }
        headZoomScrollView.setOnScrollListener(this);
        bluetoothLinkAdapter = new BluetoothLinkAdapter(getContext());
        bluetoothLinkAdapter.setItemClickListtener(this);
        bluetoothLinkAdapter.setLongClick(this);
        mylist_showcar_page.setAdapter(bluetoothLinkAdapter);

        heightLayout.setOnClickListener(this);
        weightLayout.setOnClickListener(this);
        mBabyInfoView.setOnClickListener(this);
        btnImageChoosePic.setOnClickListener(this);
        image_searchcar.setOnClickListener(this);
        text_add_car_page.setOnClickListener(this);
        mContext.getContentResolver().registerContentObserver(OperateDBUtils.WEIGHT_URI, true, contentObserver);
        operateDBUtils = new OperateDBUtils(mContext);
        picOperator = new PicOperator(getActivity());
        isBind = babyInfoEntity.getIsBind();
        deviceNum = babyInfoEntity.getBluetoothDeviceId();
        oldBabyTotalMonth = RequestUtils.getSharepreference(mContext).getString(RequestUtils.BABY_TOTAL_MONTH, "-1");
        oldBabySex = babyInfoEntity.getBabySex();
        manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();
        closeReceiver = new CloseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BaseActivity.DEVICE_CLOSE_ONPAGE);
        getActivity().registerReceiver(closeReceiver, filter);
//        DialogUtil dialogUtil = DialogUtil.getIntence(getActivity());
//        dialogUtil.showDialogNoConfirm();
    }

    @Override
    public void onStart() {
        super.onStart();
        onceShow = true;
    }

    public void refreshdata() {
        netWorkRequest.getDeviceUser(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject avObject : list) {
                        String address = avObject.getString("bluetoothDeviceId");
                        String name = avObject.getString("bluetoothName");
                        String deviceType = avObject.getString(NetWorkRequest.DEVICEIDENTIFITER);
                        String functionType = avObject.getString(NetWorkRequest.FUNCTION_TYPE);
                        String company = avObject.getString(NetWorkRequest.COMPANY);
                        if (address != null) {
                            IContent.getInstacne().addressList.add(new DeviceCarBean(name, address, deviceType, functionType, company));
                        }
                    }
                    toRefreshAdapter();
                } else {
                    ((MainHomeActivity) getActivity()).checkNetWork();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isLogin = RequestUtils.getSharepreference(mContext)
                .getBoolean(RequestUtils.IS_LOGIN, false);
        if (isLogin) {
            if (getString(R.string.input_sex_baby).equals(babyInfoEntity.getBabySex()) ||
                    getString(R.string.input_birth_baby).equals(babyInfoEntity.getBirthday()) ||
                    getString(R.string.input_address_baby).equals(babyInfoEntity.getBabyNative())) {
                RequestUtils.getSharepreferenceEditor(mContext)
                        .putBoolean(RequestUtils.IS_LOGIN, false).apply();
                gotoBabyInfoViewDialog();
            }
        }
        mThirdPartyController = new ThirdPartyController(mContext);
        updateView();
        bitmap = PicOperator.getIconFromData(mContext);
        if (bitmap != null) {
            mBabyInfoView.setImageBitmap(PicOperator.toRoundBitmap(bitmap));
        } else {
            mBabyInfoView.setImageResource(R.mipmap.homepage_headdata_bg_nor);
        }

        if (babyInfoEntity.getBabyName() != null) {
            babyName.setText(babyInfoEntity.getBabyName());
        }
        heightHealthState.setText(mContext.getResources().getString(R.string.xliff_height_health_state, RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT_HEALTH_STATE, "0~0")));
        weightHealthState.setText(mContext.getResources().getString(R.string.xliff_weight_health_state,
                RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT_HEALTH_STATE, "0~0")));
        if (babyInfoEntity.getBirthday() != null) {
            try {
                final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
                String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
                babyInfoEntity.setBabyTotalDay(mContext, birthday, "0");
                MyCalendar myCalendar = new MyCalendar(birthday, currentDate, getActivity());
                babyAge.setText(myCalendar.getDate());
                babyTotalMonth = myCalendar.getStandardDate();
                L.e("==============getHealth", babyInfoEntity.getBirthday() + "============" + babyTotalMonth);
                String heightHealthStateSp = RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT_HEALTH_STATE, "0~0");
                String weightHealthStateSp = RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT_HEALTH_STATE, "0~0");
                nowBabySex = babyInfoEntity.getBabySex();
                if (!oldBabyTotalMonth.equals(babyTotalMonth) ||
                        (oldBabySex != null && !oldBabySex.equals(nowBabySex)) ||
                        "0~0".equals(heightHealthStateSp) ||
                        "0~0".equals(weightHealthStateSp)) {
                    getHealthStateDataUpdateView(babyTotalMonth);
                    RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.BABY_TOTAL_MONTH, babyTotalMonth).commit();
                    if (oldBabySex != null && !oldBabySex.equals(nowBabySex)) {
                        oldBabySex = nowBabySex;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            babyAge.setText(Tools.getCurrentTime());
            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.HEIGHT_HEALTH_STATE, "0~0").commit();
            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.WEIGHT_HEALTH_STATE, "0~0").commit();
            heightHealthState.setText(mContext.getResources().getString(R.string.xliff_height_health_state, "0~0"));
            weightHealthState.setText(mContext.getResources().getString(R.string.xliff_weight_health_state, "0~0"));
        }
        AVAnalytics.onFragmentStart("PageFragment");


    }

    private void toRefreshAdapter() {
        ArrayList<DeviceCarBean> strings = IContent.getInstacne().addressList;
        bluetoothLinkAdapter.setArrayList(strings);
        setListViewHeight(mylist_showcar_page);
        bluetoothLinkAdapter.notifyDataSetChanged();
        if (IContent.getInstacne().addressList.size() == 0) {
            text_add_car_page.setVisibility(View.VISIBLE);
        } else {
            text_add_car_page.setVisibility(View.GONE);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(contentObserver);
        getActivity().unregisterReceiver(closeReceiver);
    }

    float heightNum_float;
    float weightNum_float;

    private void updateView() {
        final String weight = RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT, "0");
        final String height = RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT, "0");
        final float weight_int = Float.valueOf(weight);
        final float height_int = Float.valueOf(height);
        L.e(TAG, height_int + "==" + weight_int);
        heightNum_float = Float.valueOf(heightNum.getText().toString());
        weightNum_float = Float.valueOf(weightNum.getText().toString());
        ValueAnimator anim = ObjectAnimator.ofFloat(heightNum_float, height_int).setDuration((1500));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DecimalFormat d = new DecimalFormat("##.#");
                heightNum.setText(d.format((animation.getAnimatedValue())));
            }
        });
        ValueAnimator anim_w = ObjectAnimator.ofFloat(weightNum_float, weight_int).setDuration((1500));
        anim_w.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DecimalFormat d = new DecimalFormat("##.#");
                weightNum.setText(d.format((animation.getAnimatedValue())));
            }
        });
        anim_w.start();
        anim.start();
    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {
    }

    private void gotoBabyInfoViewDialog() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(getString(R.string.notice));
        builder.setNotifyInfo(getString(R.string.input_baby_info));
        builder.setShowButton(true);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(mContext, BabyInfoActivity.class));
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

    ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.getPath().contains(OperateDBUtils.WEIGHT)) {
                operateDBUtils.queryUserWeightData();
            }
        }
    };

    @Override
    public void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY >= 150) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.text_pink));
        } else {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.see));
        }
    }

    @Override
    public void click(int position) {
        this.position = position;
        if (!bleAdapter.isEnabled()) {
            app.manager.isEnabled(getActivity());
            return;
        }
        IContent.isSended = false;
        DeviceCarBean deviceCarBean = IContent.getInstacne().addressList.get(position);
        L.e("=========", deviceCarBean.getDeviceaAddress() + "====" + IContent.getInstacne().address);
        if (app.manager.cubicBLEDevice != null && deviceCarBean.getDeviceaAddress() != null && !deviceCarBean.getDeviceaAddress().equals(IContent.getInstacne().address)) {
            app.manager.cubicBLEDevice.disconnectedDevice();
            app.manager.cubicBLEDevice = null;
        }
        Intent intent = new Intent(getActivity(), DeviceDetailActivity.class);
        intent.putExtra(IContent.getInstacne().clickPositionAddress, deviceCarBean.getDeviceaAddress());
        intent.putExtra(IContent.getInstacne().clickPositionName, deviceCarBean.getDeviceName());
        intent.putExtra(IContent.getInstacne().clickPositionFunction, deviceCarBean.getFunctionType());
        IContent.getInstacne().clickPositionType = deviceCarBean.getDeviceType();
        IContent.getInstacne().functionType = deviceCarBean.getFunctionType();
        IContent.getInstacne().company = deviceCarBean.getCompany();
        startActivityForResult(intent, 110);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.height_layout:
                intent = new Intent(mContext, BabyDataDetailActivity.class);
                intent.putExtra("position", 0);
                startActivityForResult(intent, 100);
                break;
            case R.id.weight_layout:
                intent = new Intent(mContext, BabyDataDetailActivity.class);
                intent.putExtra("position", 1);
                startActivityForResult(intent, 100);
                break;
            case R.id.baby_image:
                startActivity(new Intent(mContext, BabyInfoActivity.class));
                break;
            case R.id.btn_image_choose_pic:
                PackageManager pm = mContext.getPackageManager();
                boolean permission = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", mContext.getPackageName()));
                if (permission) {
                    showPicChooserDialog();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
                break;
            case R.id.image_searchcar:
                searchDevice();
                break;
            case R.id.text_add_car_page:
                searchDevice();
                break;
        }
    }

    private void searchDevice() {
        if (!bleAdapter.isEnabled()) {
            app.manager.isEnabled(getActivity());
            return;
        }
        if (content.bluetoothLinkBeen != null && content.bluetoothLinkBeen.isEmpty()) {
            netWorkRequest.selectDeviceTypeAndIdentifier(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        content.bluetoothLinkBeen.clear();
                        for (AVObject avObject : list) {
                            BluetoothLinkBean bean = new BluetoothLinkBean();
                            bean.setDeviceIndentifier(avObject.getString(NetWorkRequest.DEVICEIDENTIFITER));
                            bean.setName(avObject.getString(NetWorkRequest.BLUETOOTH_NAME));
                            bean.setType(avObject.getString(NetWorkRequest.FUNCTION_TYPE));
                            bean.setCompany(avObject.getString(NetWorkRequest.COMPANY));
                            content.bluetoothLinkBeen.add(bean);
                        }
                        startActivityForResult(new Intent(mContext, BluetoothlinkActivity.class), 111);
                    } else {
                        ((MainHomeActivity) getActivity()).checkNetWork();
                    }
                }
            });
        } else {
            startActivityForResult(new Intent(mContext, BluetoothlinkActivity.class), 111);
        }
    }

    private Dialog mPicChooserDialog = null;

    private void showPicChooserDialog() {
        View viewDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_pic_page, null);
        mPicChooserDialog = new Dialog(mContext, R.style.MyDialogStyleBottom);
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
        LinearLayout btnCamera = (LinearLayout) viewDialog.findViewById(R.id.layout_take_page);
        LinearLayout btnGallery = (LinearLayout) viewDialog.findViewById(R.id.layout_show_page);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePicFromCamera();
                mPicChooserDialog.dismiss();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePicFromGallery();
                mPicChooserDialog.dismiss();
            }
        });
    }

    private void ChoosePicFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File dir = new File(FileUtils.DCIMCamera_PATH);
        if (!dir.exists()) {
            boolean isCreate = dir.mkdir();
            Log.d(TAG_LOG, "no dir and create it :" + isCreate);
        }
        tempPhotoPath = FileUtils.DCIMCamera_PATH + "temp.jpg";
        mCurrentPhotoFile = new File(tempPhotoPath);
        if (mCurrentPhotoFile.exists()) {
            mCurrentPhotoFile.delete();
        }
        try {
            mCurrentPhotoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(mCurrentPhotoFile));
        startActivityForResult(intent, Tools.CAMERA_WITH_DATA);
    }

    private void ChoosePicFromGallery() {
        Intent openphotoIntent = new Intent(Intent.ACTION_PICK);
        openphotoIntent.setType("image/*");
        startActivityForResult(openphotoIntent, Tools.PHOTO_PICKED_WITH_DATA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 111) {
            toRefreshAdapter();
            return;
        }
        if (requestCode == 110) {
            bluetoothLinkAdapter.notifyDataSetChanged();
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Tools.CAMERA_WITH_DATA:
                photoPath = tempPhotoPath;
                camera_path = FileUtils.SaveBitmapToFilePath(PicOperator.decodeBitmapFromFilePath(photoPath, 900, 900), FileUtils.SOURCE_IMAGE_FILEFOLDER_TEMP);
                startTagImageActivity();
                break;
            case Tools.PHOTO_PICKED_WITH_DATA:
                Uri originalUri = data.getData();
                photoPath = FileUtils.getPath(mContext, originalUri);
                camera_path = FileUtils.SaveBitmapToFilePath(PicOperator.decodeBitmapFromFilePath(photoPath, 900, 900), FileUtils.SOURCE_IMAGE_FILEFOLDER_TEMP);
                startTagImageActivity();
                break;
            case 100:
                ((MainHomeActivity) getActivity()).viewpager_home.setCurrentItem(2);
                break;
            default:
                break;
        }
    }


    public void startTagImageActivity() {
        String babyDays = RequestUtils.getSharepreference(mContext).getString(RequestUtils.TOTALE_DAY, "0");
        String weight = mContext.getResources().getString(R.string.xliff_weight_num,
                RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT, "- -"));
        String height = mContext.getResources().getString(R.string.xliff_height_num,
                RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT, "- -"));
        Intent intent = new Intent(getActivity(), TagImageEditActivity.class);
        intent.putExtra("camera_path", camera_path);
        intent.putExtra("baby_days", babyDays);
        intent.putExtra("baby_height", height);
        intent.putExtra("baby_weight", weight);
        startActivity(intent);
    }

    String height, weight;

    private void getHealthStateDataUpdateView(String babyTotalMonth) {

        netWorkRequest.getHealthStateDataFromServer(babyInfoEntity.getBabySex(), babyTotalMonth, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (AVObject avObject : list) {
                            height = avObject.getString(NetWorkRequest.HEIGHT_RANGE);
                            weight = avObject.getString(NetWorkRequest.WEIGHT_RANGE);
                        }
                    } else {
                        height = RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT_HEALTH_STATE, "0~0");
                        weight = RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT_HEALTH_STATE, "0~0");
                    }
                } else {
                    height = RequestUtils.getSharepreference(mContext).getString(RequestUtils.HEIGHT_HEALTH_STATE, "0~0");
                    weight = RequestUtils.getSharepreference(mContext).getString(RequestUtils.WEIGHT_HEALTH_STATE, "0~0");
                }
                handler.sendEmptyMessage(0x97);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x97) {
                RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.HEIGHT_HEALTH_STATE, height).commit();
                RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.WEIGHT_HEALTH_STATE, weight).commit();
                heightHealthState.setText(mContext.getResources().getString(R.string.xliff_height_health_state, height));
                weightHealthState.setText(mContext.getResources().getString(R.string.xliff_weight_health_state, weight));
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("PageFragment");
    }

    @Override
    public void longClick(final int position) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setShowBindInfo(getResources().getString(R.string.unbound_message));
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final DeviceCarBean deviceCarBean = IContent.getInstacne().addressList.get(position);
                final String deviceNumAddress = deviceCarBean.getDeviceaAddress();

                if (app.manager.cubicBLEDevice != null && deviceNumAddress.equals(IContent.getInstacne().address)) {
                    app.manager.cubicBLEDevice.disconnectedDevice();
                }

                ((MainHomeActivity) getActivity()).showProgressDialog(getString(R.string.save_message), getString(R.string.save_data_failed));
                netWorkRequest.deleteBlueToothIsBind(deviceNumAddress, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            for (int i = 0; i < list.size(); i++) {
                                AVObject avObjects = list.get(i);
                                avObjects.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        ((MainHomeActivity) getActivity()).dismissProgressDialog();
                                        if (e == null) {
                                            ToastUtil.showToast(getActivity(), getResources().getString(R.string.unbound_success));
                                            IContent.getInstacne().addressList.remove(deviceCarBean);
                                            toRefreshAdapter();
                                        } else {
                                            ((MainHomeActivity) getActivity()).checkNetWork();
                                        }
                                    }
                                });
                            }
                        } else {
                            ((MainHomeActivity) getActivity()).dismissProgressDialog();
                            ((MainHomeActivity) getActivity()).checkNetWork();
                        }
                    }
                });

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    class CloseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null && action.equals(BaseActivity.DEVICE_CLOSE_ONPAGE)) {
                    IContent.getInstacne().isBind = false;
                    IContent.getInstacne().address = null;
                    IContent.getInstacne().deviceName = null;
                    bluetoothLinkAdapter.notifyDataSetChanged();

                }
            }

        }
    }

    public static void setListViewHeight(ListView lv) {
        if (lv == null) {
            return;
        }
        ListAdapter adapter = lv.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, lv);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (lv.getCount() - 1));//这里还将分割线的高度考虑进去了，统计出所有分割线占有的高度和
        lv.setLayoutParams(params);

    }
}
