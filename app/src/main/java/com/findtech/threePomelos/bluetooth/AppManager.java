package com.findtech.threePomelos.bluetooth;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import com.findtech.threePomelos.music.utils.L;

/**
 * 管理所有的 蓝牙设备
 * 功能:
 * 1)扫描所有的蓝牙设备
 * 2)判断蓝牙权限是否打开
 *
 * @author Administrator
 */
public class AppManager {
    /**
     * 扫描的时间为10秒
     */
    private static int SCAN_TIME = 10000;
    /**
     * 返回的唯一标识
     */
    public static final int REQUEST_CODE = 0x01;
    private Context context = null;
    public static BluetoothAdapter bleAdapter = null;

    private Handler handler = null;
    /**
     * 是否正在扫描
     */
    private boolean isScanning = false;
    private RFStarManageListener listener = null;
    /**
     * 扫描到的数据
     */
    private ArrayList<BluetoothDevice> scanBlueDeviceArray = new ArrayList<BluetoothDevice>();
    /**
     * 选中的设备
     */
    public BluetoothDevice bluetoothDevice = null;
    /**
     * 选中的cubicBLEDevice
     */
    public CubicBLEDevice cubicBLEDevice = null;

    public AppManager(Context context) {
        handler = new Handler();
        //检察系统是否包含蓝牙低功耗的jar包
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "设备不支持蓝牙4.0", Toast.LENGTH_SHORT)
                    .show();
        }
        this.context = context;
        BluetoothManager manager = (BluetoothManager) this.context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();
        //检察手机硬件是否支持蓝牙低功耗
        if (bleAdapter == null) {
            Toast.makeText(context, "无法使用蓝牙4.0", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否开启蓝牙权限
     *
     * @return
     */
    public boolean isEnabled(Activity activity) {
        if (bleAdapter == null) {
            return false;
        }
        if (!bleAdapter.isEnabled()) {
            if (!bleAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
                activity.startActivityForResult(enableBtIntent, REQUEST_CODE);
            }
            return true;
        }
        return false;
    }

    /**
     * 获取扫描到的设备
     *
     * @return
     */
    public ArrayList<BluetoothDevice> getScanBluetoothDevices() {
        return this.scanBlueDeviceArray;
    }

    /**
     * 扫描蓝牙设备
     */
    public void startScanBluetoothDevice() {
        if (scanBlueDeviceArray != null) {
            scanBlueDeviceArray = null;
        }
        scanBlueDeviceArray = new ArrayList<>();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanBluetoothDevice();
            }
        }, SCAN_TIME);

        isScanning = true;
        if (bleAdapter != null) {
            bleAdapter.startLeScan(bleScanCallback);
        }
        listener.RFstarBLEManageStartScan();

    }

    /**
     * 停止扫描蓝牙设备
     */
    public void stopScanBluetoothDevice() {
        if (isScanning) {
            isScanning = false;
            if (bleAdapter != null) {
                bleAdapter.stopLeScan(bleScanCallback);
            }
            listener.RFstarBLEManageStopScan();
        }
    }

    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!scanBlueDeviceArray.contains(device)) {
                        scanBlueDeviceArray.add(device);
                        listener.RFstarBLEManageListener(device, rssi,
                                scanRecord);
                    }
                }
            });
        }
    };


    /**
     * 每扫描到一个蓝牙设备调用一次
     *
     * @param listener
     */
    public void setRFstarBLEManagerListener(RFStarManageListener listener) {
        this.listener = listener;
    }

    /**
     * 用于处理，刷新到设备时更新界面
     */
    public interface RFStarManageListener {

        /**
         * 返回扫描数据
         * @param device 蓝牙设备
         * @param rssi
         * @param scanRecord
         */
        void RFstarBLEManageListener(BluetoothDevice device, int rssi,
                                     byte[] scanRecord);


        /**
         * 开始扫描
         */
        void RFstarBLEManageStartScan();

        /**
         * 扫描结束
         */
        void RFstarBLEManageStopScan();
    }
}
