package com.findtech.threePomelos.bluetooth;import java.io.ByteArrayOutputStream;import java.io.OutputStream;import java.io.UnsupportedEncodingException;import java.text.DateFormat;import java.text.SimpleDateFormat;import java.util.Arrays;import java.util.Calendar;import java.util.Date;import java.util.List;import java.util.Timer;import java.util.TimerTask;import com.avos.avoscloud.AVException;import com.avos.avoscloud.AVObject;import com.avos.avoscloud.DeleteCallback;import com.avos.avoscloud.FindCallback;import com.avos.avoscloud.SaveCallback;import com.findtech.threePomelos.R;import com.findtech.threePomelos.base.BaseActivity;import com.findtech.threePomelos.base.MyApplication;import com.findtech.threePomelos.database.OperateDBUtils;import com.findtech.threePomelos.entity.TravelInfoEntity;import com.findtech.threePomelos.music.utils.L;import com.findtech.threePomelos.net.NetWorkRequest;import com.findtech.threePomelos.service.RFStarBLEService;import com.findtech.threePomelos.service.ReceiveWeightShowDialogService;import com.findtech.threePomelos.utils.IContent;import com.findtech.threePomelos.utils.RequestUtils;import com.findtech.threePomelos.utils.ToastUtil;import com.findtech.threePomelos.utils.Tools;import com.findtech.threePomelos.view.dialog.CustomDialog;import android.Manifest;import android.app.ActivityManager;import android.app.AlertDialog;import android.app.Service;import android.bluetooth.BluetoothAdapter;import android.bluetooth.BluetoothDevice;import android.bluetooth.BluetoothGattCharacteristic;import android.bluetooth.BluetoothGattService;import android.bluetooth.BluetoothManager;import android.content.BroadcastReceiver;import android.content.ComponentName;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.IntentFilter;import android.content.ServiceConnection;import android.content.pm.PackageManager;import android.os.Build;import android.os.IBinder;import android.os.Message;import android.os.SystemClock;import android.provider.Settings;import android.util.Log;import android.view.WindowManager;import android.widget.Toast;/** * @author Administrator */ /* * 蓝牙设备的基类 *         功能： *           1）保存设备属性 *           2）获取设备属性 *           3）结束服务，断开连接 *           4）获取服务 *           5）监视广播的属性 *            * *  */public abstract class BLEDevice {    Intent serviceIntent;    protected Context context = null;    public String deviceName = null, deviceMac = null;    protected RFStarBLEService bleService = null;    public BluetoothDevice device = null;    public RFStarBLEBroadcastReceiver delegate = null;    private NetWorkRequest netWorkRequest;    private OperateDBUtils operateDBUtils;    private TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();    private int count;    private boolean isToast = false;    public BLEDevice(Context context, BluetoothDevice device) {        this.device = device;        this.deviceName = this.device.getName();        this.deviceMac = this.device.getAddress();        this.context = context;        registerReceiver();        if (serviceIntent == null) {            serviceIntent = new Intent(this.context, RFStarBLEService.class);            if (!isServiceRunning()) {                try {                    this.context.bindService(serviceIntent, serviceConnection,                            Service.BIND_AUTO_CREATE);                } catch (Exception e) {                }            }        }        netWorkRequest = new NetWorkRequest(context);        operateDBUtils = new OperateDBUtils(context);    }    public boolean isServiceRunning() {        boolean isBind = false;        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {            if ("com.findtech.threePomelos.service.RFStarBLEService".equals(service.service.getClassName())) {                isBind =  true;            }        }        return isBind;    }    /**     * 设置连接，绑定服务     */    public void setBLEBroadcastDelegate(RFStarBLEBroadcastReceiver delegate) {        this.delegate = delegate;    }    /**     * 连接服务     */    private ServiceConnection serviceConnection = new ServiceConnection() {        @Override        public void onServiceDisconnected(ComponentName name) {            // TODO Auto-generated method stub           bleService = null;            Log.w(MyApplication.TAG, "bbbbbbbbbbb gatt is onServiceDisconnected");        }        @Override        public void onServiceConnected(ComponentName name, IBinder service) {            // TODO Auto-generated method stub            // Log.d(BLEApp.KTag, "55 serviceConnected :   服务启动 ");            bleService = ((RFStarBLEService.LocalBinder) service).getService();            //绑定蓝牙            bleService.initBluetoothDevice(device);        }    };    /**     * 获取特征值     *     * @param characteristic     */    public void readValue(BluetoothGattCharacteristic characteristic) {        if (characteristic == null) {            Log.w(MyApplication.TAG, "55555555555 readValue characteristic is null");        } else {            if (bleService != null) {                bleService.readValue(this.device, characteristic);            }        }    }    /**     * 根据特征值写入数据     *     * @param characteristic     */    public void writeValue(BluetoothGattCharacteristic characteristic) {        if (characteristic == null) {            Log.w(MyApplication.TAG, "55555555555 writeValue characteristic is null");        } else {            if (bleService != null) {                bleService.writeValue(this.device, characteristic);            }        }    }    /**     *     * @param serviceUUID     * @param characteristicUUID     * @param     */    public void writeValu(String serviceUUID, String characteristicUUID,                           byte[] value) {        if (bleService == null) {            return;        }        // TODO Auto-generated method stub        for (BluetoothGattService bluetoothGattService : bleService                .getSupportedGattServices(device)) {            String gattServiceUUID = Long.toHexString(                    bluetoothGattService.getUuid().getMostSignificantBits())                    .substring(0, 4);            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService                    .getCharacteristics()) {                String characterUUID = Long.toHexString(                        bluetoothGattCharacteristic.getUuid()                                .getMostSignificantBits()).substring(0, 4);                if (gattServiceUUID.equals(serviceUUID)                        && characteristicUUID.equals(characterUUID)) {                    L.e("currentTime===", Tools.byte2Hex(value)+"id "+bluetoothGattService.getUuid());                    bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_NOTIFY);                    bluetoothGattCharacteristic.setValue(value);                    this.writeValue(bluetoothGattCharacteristic);                }            }        }    }    public void writeValueOnlyForG(String serviceUUID, String characteristicUUID,                          byte[] value) {        if (bleService == null) {            return;        }        for (BluetoothGattService bluetoothGattService : bleService                .getSupportedGattServices(device)) {            String gattServiceUUID = Long.toHexString(                    bluetoothGattService.getUuid().getMostSignificantBits())                    .substring(0, 4);            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService                    .getCharacteristics()) {                String characterUUID = Long.toHexString(                        bluetoothGattCharacteristic.getUuid()                                .getMostSignificantBits()).substring(0, 4);                if (gattServiceUUID.equals(serviceUUID)                        && characteristicUUID.equals(characterUUID)) {                    L.e("currentTime===", Tools.byte2Hex(value)+"id "+bluetoothGattService.getUuid());                   // bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_NOTIFY);                    bluetoothGattCharacteristic.setValue(value);                    this.writeValue(bluetoothGattCharacteristic);                }            }        }    }    public void readValue(String serviceUUID, String characteristicUUID,                           byte[] value) {        if (bleService == null) {            return;        }        // TODO Auto-generated method stub        for (BluetoothGattService bluetoothGattService : bleService                .getSupportedGattServices(device)) {            String gattServiceUUID = Long.toHexString(                    bluetoothGattService.getUuid().getMostSignificantBits())                    .substring(0, 4);            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService                    .getCharacteristics()) {                String characterUUID = Long.toHexString(                        bluetoothGattCharacteristic.getUuid()                                .getMostSignificantBits()).substring(0, 4);                if (gattServiceUUID.equals(serviceUUID)                        && characteristicUUID.equals(characterUUID)) {                    L.e("AAAAA",gattServiceUUID+"=="+characteristicUUID);                    bluetoothGattCharacteristic.setValue(value);                    this.readValue(bluetoothGattCharacteristic);                }            }        }    }    /**     * 消息使能     *     * @param characteristic     * @param enable     */    public void setCharacteristicNotification(            BluetoothGattCharacteristic characteristic, boolean enable) {        if (characteristic == null) {            Log.w(MyApplication.TAG, "55555555555 Notification characteristic is null");        } else {            if (bleService != null) {                bleService.setCharacteristicNotification(this.device,                        characteristic, enable);            }        }    }    /**     * 断开连接     */    public void disconnectedDevice() {        if (bleService != null) {            bleService.disconnect(device);        }        try {            Log.d("ZZ", "BLEDevice unregisterReceiver");            Log.d("ZZ", "BLEDevice unbindService");            context.unregisterReceiver(gattUpdateRecevice);            context.unbindService(serviceConnection);        } catch (Exception e) {            Log.d("ZZ", "BLEDevice disconnectedDevice e = " + e);            e.printStackTrace();        }        bleService = null;        IContent.getInstacne().isBind= false;        IContent.getInstacne().address = null;    }    public void closeDevice() {        try {            this.ungisterReceiver();            this.context.unbindService(serviceConnection);        } catch (Exception e) {            e.printStackTrace();        }    }    /**     * 获取服务     *     * @return     */    public List<BluetoothGattService> getBLEGattServices() {        return this.bleService.getSupportedGattServices(this.device);    }    /**     * 监视广播的属性     *     * @return     */    protected IntentFilter bleIntentFilter() {        final IntentFilter intentFilter = new IntentFilter();        intentFilter.addAction(RFStarBLEService.ACTION_GATT_CONNECTED);        intentFilter.addAction(RFStarBLEService.ACTION_GATT_DISCONNECTED);        intentFilter.addAction(RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED);        intentFilter.addAction(RFStarBLEService.ACTION_DATA_AVAILABLE);        intentFilter.addAction(RFStarBLEService.ACTION_GAT_RSSI);        intentFilter.addAction(RFStarBLEService.ACTION_WRITE_DONE);        intentFilter.addAction(RFStarBLEService.DESCRIPTOR_WRITER_DONE);        intentFilter.addAction(RFStarBLEService.NOTIFY_WRITE_DONE);        intentFilter.addAction(RFStarBLEService.ACTION_DATA_AVAILABLE_READ);        intentFilter.addAction(RFStarBLEService.ACTION_GATT_CONNECTING);        intentFilter.setPriority(1000);        return intentFilter;    }    public interface RFStarBLEBroadcastReceiver {        /**         * 监视蓝牙状态的广播 macData蓝牙地址的唯一识别码         */        void onReceive(Context context, Intent intent, String macData,                       String uuid);        void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time);    }    /**     * 注册监视蓝牙设备（返回数据的）广播     */    public void registerReceiver() {        this.context.registerReceiver(gattUpdateRecevice,                this.bleIntentFilter());    }    /**     * 注销监视蓝牙返回的广播     */    public void ungisterReceiver() {        try {            this.context.unregisterReceiver(gattUpdateRecevice);        } catch (Exception e) {            e.printStackTrace();        }    }    /**     * 初始化服务中的特征     */    protected abstract void discoverCharacteristicsFromService();    private StringBuffer allMessageStr = new StringBuffer();    /**     * 接收蓝牙广播     */    private BroadcastReceiver gattUpdateRecevice = new BroadcastReceiver() {        @Override        public void onReceive(final Context context, Intent intent) {            // TODO Auto-generated method stub            String characteristicUUID = intent                    .getStringExtra(RFStarBLEService.RFSTAR_CHARACTERISTIC_ID);            String action = intent.getAction();            if (action.equals(RFStarBLEService.ACTION_WRITE_DONE)) {                byte[] data = intent                        .getByteArrayExtra(RFStarBLEService.EXTRA_DATA);                L.e("currentTime", action);                L.e("currentTime",Tools.byte2Hex(data));            }            if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {                RequestUtils.getSharepreferenceEditor(context).                        putString(RequestUtils.DEVICE, deviceMac).commit();            } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {                IContent.getInstacne().isBind = false;                IContent.getInstacne().address = null;                IContent.getInstacne().deviceName = null;            } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED                    .equals(action)) {                    setNotification(true);            }  else if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {                byte[] data = intent                        .getByteArrayExtra(RFStarBLEService.EXTRA_DATA);                L.e("currentTime",Tools.byte2Hex(data));                if (IContent.isBLE){                    if (data[3] == (byte) 0x81 && data[4] == 0x02) {                        if (data[5] == 0x03) {                            ToastUtil.showToast(context, context.getString(R.string.change_bluetooth_mode));                            IContent.getInstacne().SD_Mode = false;                        }                    }                }                if (characteristicUUID.contains("ffe4") && !IContent.isBLE) {                    if (data == null) {                        Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_SHORT).show();                        return;                    }                    try {                        String receiveData = new String(data, "GB2312");                        String primaryData = null;                        L.e("currentTime",receiveData);                        allMessageStr.append(receiveData);                        int size = allMessageStr.length();                        if (size > 500) {                            primaryData = allMessageStr.substring(size - 500, size);                        } else {                            primaryData = allMessageStr.toString();                        }                        if (Tools.lastCharIsLineBreak(primaryData)) {                            allMessageStr.delete(0, allMessageStr.length());                            final Date currentDate = Tools.getCurrentDate();                            if (primaryDataIsValid(primaryData)) {                                String newDate = formatPrimaryData(Tools.primaryDataType(primaryData), primaryData);                                newDate = Tools.replaceTabs(newDate);                                if (Tools.primaryDataType(primaryData) == Tools.RECEIVE_TEMPERATURE_ELECTRIC_MSG) {                                    if (!Tools.isNumericOrDotOrMinus(newDate)) {                                        return;                                    }                                } else {                                    if (!Tools.isNumericOrDot(newDate)) {                                        return;                                    }                                }                                switch (Tools.primaryDataType(primaryData)) {                                    case Tools.WEIGHT_MSG://体重                                        //判断是否是安全数据                                        String weight = Tools.extractStringFirstPoint(newDate);                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||                                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&                                                        Settings.canDrawOverlays(context))) {                                            Intent intentService = new Intent(context, ReceiveWeightShowDialogService.class);                                            intentService.putExtra("weight", weight);                                            intentService.putExtra("date", Tools.getSystemTimeInChina("yyyy-MM-dd"));                                            intentService.setPackage(context.getPackageName());                                            context.startService(intentService);                                        } else {                                            saveWeightDataToServer(weight, currentDate);                                        }                                        break;                                    case Tools.MILEAGE_INFO_MSG://路程信息                                        String[] mileageInfoArray = newDate.split(",");                                        if (travelInfoEntity == null) {                                            return;                                        }                                        travelInfoEntity.setTodayMileage(Tools.extractStringFirstPoint(mileageInfoArray[0]));                                        travelInfoEntity.setTotalMileage(Tools.extractStringFirstPoint(mileageInfoArray[1]));                                        travelInfoEntity.setAverageSpeed(Tools.extractStringFirstPoint(mileageInfoArray[2]));                                        netWorkRequest.isExistTheTimeOnTable(NetWorkRequest.TRAVEL_INFO, currentDate, deviceMac,new FindCallback<AVObject>() {                                            @Override                                            public void done(List list, AVException e) {                                                if (list == null || (list != null && list.size() == 0)) {                                                    addTravelInfoToServer(travelInfoEntity, currentDate);                                                } else if (list.size() == 1) {                                                    updateTravelInfoToServer(travelInfoEntity, currentDate);                                                } else if (list.size() > 1) {                                                    AVObject.deleteAllInBackground(list, new DeleteCallback() {                                                        @Override                                                        public void done(AVException e) {                                                            if (e == null) {                                                                addTravelInfoToServer(travelInfoEntity, currentDate);                                                            } else {                                                                Toast.makeText(context,  context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                                                            }                                                        }                                                    });                                                }                                            }                                        });                                        break;                                    case Tools.REQUEST_TIME_MSG://请求时间                                        String currentTime = Tools.getSystemTimeInChina("HHmmss");                                        currentTime = "CMD:TM" + currentTime + "\r\n";                                        L.e("currentTime",currentTime + currentTime.getBytes().length);                                        writeValueOnlyForG("ffe5", "ffe9", currentTime.getBytes());                                        break;                                    case Tools.RECEIVE_FIRMWARE_VERSION_MSG://收到固件版本                                        RequestUtils.getSharepreferenceEditor(context).putString(RequestUtils.FIRMWARE_VERSION, newDate).commit();                                        break;                                    case Tools.RECEIVE_TEMPERATURE_ELECTRIC_MSG://收到温度和电量                                        String[] newDateArray = newDate.split(",");                                        String temperature = newDateArray[0];//温度                                        String electric = newDateArray[1];//电量                                        L.e("CCCCCCDDD", "BLEDevice --> temperature = " + temperature);                                        Intent intentReceive = new Intent(RequestUtils.RECEIVE_TEMPERATURE_ELECTRIC_ACTION);                                        intentReceive.putExtra(RequestUtils.TEMPERATURE, temperature);                                        intentReceive.putExtra(RequestUtils.CURRENT_ELECTRIC, electric);                                        travelInfoEntity.setElectric_rate(electric);                                        travelInfoEntity.setText_tempurature(temperature);                                        context.sendBroadcast(intentReceive);                                        RequestUtils.getSharepreferenceEditor(context).putString(RequestUtils.CURRENT_ELECTRIC, electric).commit();                                        break;                                    case -1:                                }                            }                        }                    } catch (UnsupportedEncodingException e) {                        // TODO Auto-generated catch block                        e.printStackTrace();                    }                }            }            delegate.onReceive(context, intent, device.getAddress(),                    characteristicUUID);        }    };    public void setNotification( boolean enable) {        if (bleService == null) {            return;        }        for (BluetoothGattService bluetoothGattService : bleService                .getSupportedGattServices(device)) {            String gattServiceUUID = Long.toHexString(                    bluetoothGattService.getUuid().getMostSignificantBits())                    .substring(0, 4);            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService                    .getCharacteristics()) {                String characterUUID = Long.toHexString(                        bluetoothGattCharacteristic.getUuid()                                .getMostSignificantBits()).substring(0, 4);                    if (gattServiceUUID.equals(IContent.SERVERUUID_BLE)                            && IContent.CHARACTERUUID_BLE.equals(characterUUID)) {                        IContent.isBLE = true;                        this.setCharacteristicNotification(                                bluetoothGattCharacteristic, enable);                        return;                    }                    if (gattServiceUUID.equals(IContent.SERVERUUID_PRE)                            && IContent.CHARACTERUUID_PRE.equals(characterUUID)) {                        IContent.isBLE = false;                        L.e("currentTime",characterUUID);                        this.setCharacteristicNotification(                                bluetoothGattCharacteristic, enable);                        return;                    }            }        }    }    private boolean primaryDataIsValid(String data) {        if (data.contains("DAT:")) {            return true;        }        return false;    }    private void saveWeightDataToServer(final String weight, final Date date) {        netWorkRequest.isExistTheTimeOnTable(NetWorkRequest.BABY_WEIGHT, date, new FindCallback<AVObject>() {            @Override            public void done(List<AVObject> list, AVException e) {                if (e == null) {                    if (list == null || (list != null && list.size() == 0)) {                        addWeightToServer(weight, date);                    } else if (list.size() == 1) {                        updateWeightToServer(weight, date);                    } else if (list.size() > 1) {                        AVObject.deleteAllInBackground(list, new DeleteCallback() {                            @Override                            public void done(AVException e) {                                if (e == null) {                                    addWeightToServer(weight, date);                                } else {                                    L.e("SSSSSS","============");                                    Toast.makeText(context,  context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                                }                            }                        });                    }                } else {                    Toast.makeText(context,  context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }    /**     * 上传baby体重     * @param weight     * @param date     */    private void addWeightToServer(final String weight, final Date date) {        netWorkRequest.addWeightAndTimeToServer(weight, date, new SaveCallback() {            @Override            public void done(AVException e) {                if (e == null) {                    operateDBUtils.saveWeightToDB(weight, Tools.getTimeFromDate(date));                    delegate.onReceiveDataAvailable(RequestUtils.WEIGHT, weight, null, Tools.getTimeFromDate(date));                } else {                    L.e("SSSSSS","============");                    Toast.makeText(context,  context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }    private void updateWeightToServer(final String weight, final Date date) {        netWorkRequest.updateWeightAndTimeToServer(weight, date, new SaveCallback() {            @Override            public void done(AVException e) {                if (e == null) {                    operateDBUtils.saveWeightToDB(weight, Tools.getTimeFromDate(date));                    delegate.onReceiveDataAvailable(RequestUtils.WEIGHT, weight, null, Tools.getTimeFromDate(date));                } else {                    Toast.makeText(context,  context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }    private String formatPrimaryData(int msgType, String primaryData) {        String newData = "0.0";        switch (msgType) {            case Tools.WEIGHT_MSG:                newData = primaryData.replace(Tools.WEIGHT_PRE, "");                break;            case Tools.MILEAGE_INFO_MSG:                newData = primaryData.replace(Tools.MILEAGE_INFO_PRE, "");                break;            case Tools.RECEIVE_FIRMWARE_VERSION_MSG:                newData = primaryData.replace(Tools.RECEIVE_FIRMWARE_VERSION_PRE, "");                break;            case Tools.RECEIVE_TEMPERATURE_ELECTRIC_MSG:                newData = primaryData.replace(Tools.RECEIVE_TEMPERATURE_ELECTRIC_PRE, "");                break;            case -1:        }        return newData;    }    private void addTravelInfoToServer(final TravelInfoEntity travelInfoEntity, final Date date) {        netWorkRequest.addTravelInfoAndTimeToServer(travelInfoEntity, date, new SaveCallback() {            @Override            public void done(AVException e) {                if (e == null) {                    operateDBUtils.saveMileageInfoToDB(travelInfoEntity, Tools.getTimeFromDate(date));                    delegate.onReceiveDataAvailable(RequestUtils.TRAVEL_INFO, null, travelInfoEntity, Tools.getTimeFromDate(date));                } else {                    Toast.makeText(context,  context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }    private void updateTravelInfoToServer(final TravelInfoEntity travelInfoEntity, final Date date) {        netWorkRequest.updateTravelInfoAndTimeToServer(travelInfoEntity, date, new SaveCallback() {            @Override            public void done(AVException e) {                if (e == null) {                    operateDBUtils.saveMileageInfoToDB(travelInfoEntity, Tools.getTimeFromDate(date));                    delegate.onReceiveDataAvailable(RequestUtils.TRAVEL_INFO, null, travelInfoEntity, Tools.getTimeFromDate(date));                    if (!isToast) {                        isToast = true;                        Toast.makeText(context, context.getString(R.string.data_update_sucess), Toast.LENGTH_SHORT).show();                    }                } else {                    Toast.makeText(context, context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }    private void addTotalMileageToServer(final String totalMileage) {        netWorkRequest.addTotalMileageAndTimeToServer(totalMileage, new SaveCallback() {            @Override            public void done(AVException e) {                if (e == null) {                    RequestUtils.getSharepreferenceEditor(context).putString(OperateDBUtils.TOTAL_MILEAGE, totalMileage).commit();                    delegate.onReceiveDataAvailable(RequestUtils.TOTALE_MILEAGE, totalMileage, null, null);                } else {                    Toast.makeText(context, context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }    private void updateTotalMileageToServer(final String totalMileage) {        netWorkRequest.updateTotalMileageAndTimeToServer(totalMileage, new SaveCallback() {            @Override            public void done(AVException e) {                if (e == null) {                    RequestUtils.getSharepreferenceEditor(context).putString(OperateDBUtils.TOTAL_MILEAGE, totalMileage).commit();                    delegate.onReceiveDataAvailable(RequestUtils.TOTALE_MILEAGE, totalMileage, null, null);                } else {                    Toast.makeText(context, context.getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();                }            }        });    }}