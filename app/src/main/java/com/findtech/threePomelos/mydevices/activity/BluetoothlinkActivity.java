package com.findtech.threePomelos.mydevices.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.bluetooth.AppManager;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.bluetooth.CubicBLEDevice;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.mydevices.adapter.BlueSearchAdapter;
import com.findtech.threePomelos.mydevices.bean.BluetoothLinkBean;
import com.findtech.threePomelos.net.NetWorkRequest;

import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;

import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.MyAlarmView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex
 */
public class BluetoothlinkActivity extends MyActionBarActivity implements BLEDevice.RFStarBLEBroadcastReceiver, AppManager.RFStarManageListener, ItemClickListtener {

    private ImageView image_gif_bluetooth;
    private MyAlarmView myAlarmView;
    private RecyclerView recyclerView;
    private ArrayList<BluetoothDevice> arraySource = new ArrayList<>();
    private BlueSearchAdapter blueSearchAdapter;
    private NetWorkRequest netWorkRequest;
    private int position;
    private OperateDBUtils mOperateDBUtils;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();
    private SwipeRefreshLayout swipeRefreshLayout;
    BluetoothManager manager;
    BluetoothAdapter bleAdapter;
    private RelativeLayout linking_layout;
    private TextView text_notice, text_name;
    private IContent content = IContent.getInstacne();
    String functionType = null;
    String deviceIndentifier = null;
    String company = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothlink);
        setToolbar(getResources().getString(R.string.link_buluetooth), true, null);
        mOperateDBUtils = new OperateDBUtils(this);
        netWorkRequest = new NetWorkRequest(this);
        image_gif_bluetooth = (ImageView) findViewById(R.id.image_gif_bluetooth);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_blue_link);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAlarmView = (MyAlarmView) findViewById(R.id.myAlarmview);
        Glide.with(this).load(R.mipmap.gif).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image_gif_bluetooth);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.text_pink);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myAlarmView.setVisibility(View.VISIBLE);
                myAlarmView.start();
                bindBluetooth();
            }
        });
        linking_layout = (RelativeLayout) findViewById(R.id.linking_layout);
        linking_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothlinkActivity.this, DeviceDetailActivity.class);
                intent.putExtra(IContent.getInstacne().clickPositionAddress, IContent.getInstacne().address);
                intent.putExtra(IContent.getInstacne().clickPositionName, IContent.getInstacne().deviceName);
                intent.putExtra(IContent.getInstacne().clickPositionFunction, IContent.getInstacne().functionType);
                startActivity(intent);
                finish();
            }
        });
        text_name = (TextView) findViewById(R.id.text_name);
        text_name.setText(IContent.getInstacne().deviceName);
        text_notice = (TextView) findViewById(R.id.text_notice);
        text_notice.setText(getString(R.string.drop_refresh));
        blueSearchAdapter = new BlueSearchAdapter(this);
        blueSearchAdapter.setItemCliclListener(this);
        recyclerView.setAdapter(blueSearchAdapter);
        swipeRefreshLayout.setRefreshing(true);
        manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();
        app.manager.isEnabled(this);
        if (bleAdapter != null && bleAdapter.isEnabled()) {
            bindBluetooth();
        }

        myAlarmView.setVisibility(View.VISIBLE);
        myAlarmView.start();
    }
    private void bindBluetooth() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                app.manager.setRFstarBLEManagerListener(BluetoothlinkActivity.this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    app.manager.startScanBluetoothDevice();
                } else {
                    startScanBluetoothDevice();
                }
            }
        }, 300);
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void startScanBluetoothDevice() {

        int hasAccessCoarseLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasAccessFineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasAccessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ASK_ACCESS_COARSE_LOCATION_PERMISSIONS);
            return;
        }
        if (hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_ACCESS_FINE_LOCATION_PERMISSIONS);
            return;
        }
        app.manager.startScanBluetoothDevice();
    }
    BluetoothLinkBean bluetoothLinkBean;
    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {

        try {
            String action = intent.getAction();
            if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                IContent.getInstacne().address = null;
                IContent.getInstacne().isBind = false;
                IContent.getInstacne().deviceName = null;
                IContent.getInstacne().functionType = null;
                IContent.getInstacne().company = null;
                blueSearchAdapter.notifyDataSetChanged();
            } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                final String deviceNum = macData;
                final String deviceName = arraySource.get(position).getName();
                for (BluetoothLinkBean bean : IContent.getInstacne().bluetoothLinkBeen) {
                    if (deviceName != null && deviceName.equals(bean.getName())) {
                        bluetoothLinkBean = bean;
                        deviceIndentifier = bean.getDeviceIndentifier();
                        IContent.getInstacne().clickPositionType = deviceIndentifier;
                        IContent.getInstacne().address = deviceNum;
                        functionType = bean.getType();
                        company = bean.getCompany();
                    }
                }
                if (deviceIndentifier != null && functionType != null ) {
                               IContent.getInstacne().functionType = functionType;
                               IContent.getInstacne().company =company;

                               netWorkRequest.thisBlueToothIsBinded(deviceNum, new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if (e == null) {

                                            if (list != null && list.size() == 0) {
                                                netWorkRequest.addBlueTooth(true, deviceNum, deviceName, functionType, deviceIndentifier, company,new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            dismissProgressDialog();
                                                            bindSuccess(deviceNum);
                                                        }else {
                                                            dismissProgressDialog();
                                                            checkNetWork();
                                                        }
                                                    }
                                                });
                                            } else {
                                                for (int i = 0; i < list.size(); i++) {
                                                    AVObject avObjects = list.get(i);
                                                    String deviceAddress = avObjects.getString(OperateDBUtils.BLUETOOTH_DEVICE_ID);
                                                    if (deviceAddress != null && deviceAddress.equals(deviceNum)) {
                                                        avObjects.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
                                                        avObjects.put("bluetoothName", deviceName);
                                                        avObjects.put("bluetoothBind", true);
                                                        avObjects.put(NetWorkRequest.DEVICEIDENTIFITER, deviceIndentifier);
                                                        avObjects.put(NetWorkRequest.FUNCTION_TYPE, functionType);
                                                        avObjects.put(NetWorkRequest.COMPANY, company);
                                                        avObjects.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                if (e == null) {
                                                                    dismissProgressDialog();
                                                                    bindSuccess(deviceNum);
                                                                }else {
                                                                    dismissProgressDialog();
                                                                    checkNetWork();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        } else {
                                            dismissProgressDialog();
                                            checkNetWork();
                                        }
                                    }
                                });
                           }
                        } else {
                            dismissProgressDialog();
                            checkNetWork();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void bindSuccess(String deviceNum) {
        IContent.getInstacne().address = deviceNum;
        IContent.getInstacne().deviceName = arraySource.get(position).getName();

        IContent.getInstacne().isBind = true;
        dismissProgressDialog();
        blueSearchAdapter.setLinking(false);
        blueSearchAdapter.notifyDataSetChanged();
        mOperateDBUtils.saveBlueToothIsBindToDB(true, deviceNum);
        babyInfoEntity.setBluetoothDeviceId(deviceNum);
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
                            IContent.getInstacne().addressList.add(new DeviceCarBean(name, address, deviceType, functionType,company));
                        }
                    }
                } else {
                    checkNetWork();
                }
            }
        });
        Toast.makeText(BluetoothlinkActivity.this, getString(R.string.bind_success), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {
    }

    @Override
    public void RFstarBLEManageListener(BluetoothDevice device, int rssi, byte[] scanRecord) {

        for (BluetoothLinkBean bean : IContent.getInstacne().bluetoothLinkBeen){
            if (bean.getName().equals(device.getName()) && !arraySource.contains(device)) {
                arraySource.add(device);
            }
        }

        swipeRefreshLayout.setRefreshing(false);
        blueSearchAdapter.setArrayList(arraySource);
        blueSearchAdapter.notifyDataSetChanged();
    }


    @Override
    public void RFstarBLEManageStartScan() {
        this.arraySource.clear();
        if (content.deviceName != null && content.address != null) {
            linking_layout.setVisibility(View.VISIBLE);
            blueSearchAdapter.setLinking(true);
        } else {
            linking_layout.setVisibility(View.GONE);
            blueSearchAdapter.setLinking(false);
        }

    }

    @Override
    public void RFstarBLEManageStopScan() {
        myAlarmView.stop();
        myAlarmView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void click(int position) {
        this.position = position;
        linking_layout.setVisibility(View.GONE);

        if (!NetUtils.isConnectInternet(this)){
            ToastUtil.showToast(this,getResources().getString(R.string.net_exception));
            return;
        }
        BluetoothDevice bluetoothDevice = arraySource.get(position);
        if (!bluetoothDevice.getAddress().equals(IContent.getInstacne().address)) {
            IContent.isSended = false;
            showProgressDialogDis(getString(R.string.connecting), 25000, null);
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.disconnectedDevice();
            }
            app.manager.bluetoothDevice = bleAdapter.getRemoteDevice(bluetoothDevice.getAddress());
            app.manager.isEnabled(this);
            app.manager.cubicBLEDevice = new CubicBLEDevice(
                    getApplicationContext(), app.manager.bluetoothDevice);
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
        if (IContent.getInstacne().isBind && bluetoothDevice.getAddress().equals(IContent.getInstacne().address)) {
            //Intent intent = new Intent(this, DeviceDetailActivity.class);
           Intent intent = new Intent(this, DeviceDetailActivity.class);
            intent.putExtra(IContent.getInstacne().clickPositionAddress, bluetoothDevice.getAddress());
            intent.putExtra(IContent.getInstacne().clickPositionName, bluetoothDevice.getName());
            intent.putExtra(IContent.getInstacne().clickPositionFunction, functionType);
            IContent.getInstacne().clickPositionType = deviceIndentifier;
            IContent.getInstacne().functionType = functionType;
            IContent.getInstacne().company = company;
            startActivityForResult(intent, 110);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (app.manager != null){
            app.manager.stopScanBluetoothDevice();
        }

    }
}
