package com.findtech.threePomelos.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhi.zhang on 3/9/16.
 */
public class NetWorkRequest {
    private final String TAG = "NetWorkRequest";

    /* 表名 */
    /**
     * 宝贝信息
     */
    public static final String BABY_INFO = "BabyInfo";
    /**
     * 宝贝体重
     */
    public static final String BABY_WEIGHT = "BabyWeight";
    /**
     * 宝贝身高
     */
    public static final String BABY_HEIGHT = "BabyHeight";
    /**
     * 推车今日里程、今日时速、总里程
     */
    public static final String TRAVEL_INFO = "TravelInfo";
    /**
     * 推车总里程
     */
    public static final String TOTAL_MILEAGE = "TotalMileage";
    /**
     * 健康合理区间表
     */
    public static final String HEALTH_STATE = "HealthState";
    /**
     * 健康贴士
     */
    public static final String HEALTH_TIPS = "HealthTips";
    /**
     * 用户协议
     */
    public static final String USER_PROTOCOL = "UserProtocol";


    /* 健康贴士表中的列，宝贝健康小贴士中的文件 */
    /**
     * 健康贴士:文件
     */
    public static final String FILE_HEALTH_TIPS = "HealthTipsFile";
    /**
     * 育儿要点:文件
     */
    public static final String FILE_CHILDCARE_POINTS = "ChildcarePoints";
    /**
     * 发育体征:文件
     */
    public static final String FILE_DEVELOPMENT_SIGNS = "DevelopmentSigns";
    /**
     * 科普提示:文件
     */
    public static final String FILE_SCIENCE_TIP = "ScienceTip";
    /**
     * 成长关注:文件
     */
    public static final String FILE_GROWN_CONCERN = "GrownConcern";
    /**
     * 亲子互动:文件
     */
    public static final String FILE_PARENTAL = "ParentalInteraction";
    /**
     * 育儿要点预览:字符串
     */
    public static final String TIPS_PREVIEW = "TipsPreview";

    public static final String HEIGHT_RANGE = "heightRange";
    public static final String WEIGHT_RANGE = "weightRange";

    /**
     * 设备列表
     */
    public static final String DEVICE_UUID = "DeviceUUIDList";
    public static final String CART_LIST_DETAILS = "Cart_List_Details";
    /**
     * 卡路里
     */
    public static final String Calor = "totalCalories";

    /**
     * 一键修复
     */
    public static final String Repair_Data = "DeviceHardwareInfo";
    /**
     * 从机数据
     */
    public static final String Device_Data = "DeviceData";

    /**
     * 使用说明
     */
    public static final String INSTRUCTIONS = "Instruction";

    public static String BLUETOOTH_NAME = "bluetoothName";
    public static String DEVICEIDENTIFITER = "deviceIdentifier";
    public static String FUNCTION_TYPE = "fuctionType";
    public static String COMPANY = "company";


    private boolean isSavedTheTime = false; //是否已经有这个时间的体重
    private List<AVObject> listAVObject;
    private Context mContext;
    private OperateDBUtils mOperateDBUtils;
    private ProgressDialog progressDialog;
    private AVObject postIsBind;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();

    public NetWorkRequest(Context context) {
        mContext = context;
        mOperateDBUtils = new OperateDBUtils(mContext);
        initProgressDialog();
    }


    public void getBabyInfoDataAndSaveToDB() {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {//如果服务器中没有这个用户数据，就创建一个
                        AVObject post = new AVObject(NetWorkRequest.BABY_INFO);
                        post.put("post", AVUser.getCurrentUser());
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Log.d(TAG, "SetBabyBirthdayActivity  ");
                                    getBabyInfoDataAndSaveToDB();
                                } else {
                                    mOperateDBUtils.queryBabyInfoDataFromDB();
                                    Log.d(TAG, "SetBabyBirthdayActivity AVException e = " + e);
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        mOperateDBUtils.saveBabyInfoDataToDB(list);
                        Log.d(TAG, "NetWorkRequest findInBackground done");
                    }
                } else {
                    mOperateDBUtils.queryBabyInfoDataFromDB();
                    Log.d(TAG, "NetWorkRequest findInBackground e = " + e);
                }
            }
        });
    }

    public void getBabyInfoDataAndSaveToDB(final QueryBabyInfoCallBack.QueryIsBind queryIsBind) {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {//如果服务器中没有这个用户数据，就创建一个
                        AVObject post = new AVObject(NetWorkRequest.BABY_INFO);
                        post.put("post", AVUser.getCurrentUser());
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Log.d(TAG, "SetBabyBirthdayActivity  ");
                                    getBabyInfoDataAndSaveToDB(queryIsBind);
                                } else {
                                    mOperateDBUtils.queryBabyInfoDataFromDB();
                                    Log.d(TAG, "SetBabyBirthdayActivity AVException e = " + e);
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        mOperateDBUtils.saveBabyInfoDataToDB(list);
                        if (list != null && list.size() > 0) {
                            queryIsBind.finishQueryIsBind(list.get(0).getBoolean(OperateDBUtils.IS_BIND),
                                    list.get(0).getString(OperateDBUtils.BLUETOOTH_DEVICE_ID));
                            Log.d(TAG, "finishQueryIsBind");
                        } else {
                            queryIsBind.finishQueryIsBind(false, "");
                            Log.d(TAG, "finishQueryIsBind 1");
                        }
                        Log.d(TAG, "NetWorkRequest findInBackground done 1");
                    }
                } else {
                    mOperateDBUtils.queryBabyInfoDataFromDB();
                    Log.d(TAG, "NetWorkRequest findInBackground e = " + e);
                }
            }
        });
    }

    public void getBabyWeightDataAndSaveToDB() {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_WEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.orderByAscending(OperateDBUtils.DATE);//按时间，升序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mOperateDBUtils.clearDBTable(OperateDBUtils.WEIGHT_URI);
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            String weight = avObject.getString(OperateDBUtils.WEIGHT);
                            Date date = avObject.getDate(OperateDBUtils.DATE);
                            String time = Tools.getTimeFromDate(date);
                            if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(time)) {
                                mOperateDBUtils.saveWeightToDB(weight, time);
                            }
                        }
                    }
                    mOperateDBUtils.queryUserWeightData();
                } else {
                    mOperateDBUtils.queryUserWeightData();
                    Log.d("ZZ", "getBabyWeightDataAndSaveToDB e = " + e);
                }
            }
        });
    }


    public void getAllHealthDate(FindCallback findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(HEALTH_TIPS);
        query.orderByAscending("createdAt");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        query.findInBackground(findCallback);
    }

    public void getMusicDownList(FindCallback findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("MusicDownLoad");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.findInBackground(findCallback);
    }


    public void sendAadultWeight(final String weight, final SaveCallback saveCallback) {
        progressDialog.show();
        AVQuery<AVObject> query = new AVQuery<>(Calor);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (list.size() != 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("adultWeight", weight);
                        avObject.saveInBackground(saveCallback);
                    } else {
                        AVObject avObject = new AVObject(Calor);
                        avObject.put("post", AVUser.getCurrentUser());
                        avObject.put("adultWeight", weight);
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public void sendDeleteDownMusic(final String name , final DeleteCallback deleteCallback){
        AVQuery<AVObject> query = new AVQuery<>("MusicDownLoad");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", name);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null && list.size() != 0){
                    for (AVObject avObject : list){
                        avObject.deleteInBackground(deleteCallback);
                    }
                }
            }
        });



    }


    public void sendNameToServer(final String name, final String address, final SaveCallback saveCallback) {
        progressDialog.show();
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                progressDialog.dismiss();
                if (e == null) {
                    boolean  b = list == null;
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        if ( avObject.getString("bluetoothDeviceId")!= null &&  avObject.getString("bluetoothDeviceId").equals(address)) {
                            avObject.put("bluetoothName", name);
                            avObject.saveInBackground(saveCallback);
                        }
                    }
                }else {
                    L.e("======",e.toString());
                }
            }
        });
    }

    public void updateUUIDCreatAt(final String address, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                progressDialog.dismiss();
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        if (address.equals(avObject.getString("bluetoothDeviceId"))) {
                            avObject.put("bluetoothDeviceId", address);
                            avObject.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void sendRepairMessage(String repairMessage, Date date, String address, String type, SaveCallback callback) {
        AVObject avObject = new AVObject(Repair_Data);
        avObject.put("user", AVUser.getCurrentUser());
        avObject.put(OperateDBUtils.DATE, date);
        avObject.put("deviceAddress", address);
        avObject.put(DEVICEIDENTIFITER, type);
        avObject.put("repairInfo", repairMessage);
        avObject.saveInBackground(callback);
    }

    public void sendDataMessage(String dataMessage, Date date, String address, String type, SaveCallback callback) {

        AVObject avObject = new AVObject(Device_Data);
        avObject.put("user", AVUser.getCurrentUser());
        avObject.put(OperateDBUtils.DATE, date);
        avObject.put("deviceAddress", address);
        avObject.put(DEVICEIDENTIFITER, type);
        avObject.put("deviceData", dataMessage);
        avObject.saveInBackground(callback);
    }

    public void getBabyHeightDataAndSaveToDB() {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_HEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.orderByAscending(OperateDBUtils.DATE);//按时间，升序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mOperateDBUtils.clearDBTable(OperateDBUtils.HEIGHT_URI);
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            String height = avObject.getString(OperateDBUtils.HEIGHT);
                            Date curDate = avObject.getDate(OperateDBUtils.DATE);
                            String time = Tools.getTimeFromDate(curDate);
                            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(time)) {
                                mOperateDBUtils.saveHeightToDB(height, time);
                            }
                        }
                    }
                    mOperateDBUtils.queryUserHeightData();
                } else {
                    mOperateDBUtils.queryUserHeightData();
                    Log.d("ZZ", "getBabyHeightDataAndSaveToDB e = " + e);
                }
            }
        });
    }

    public void getTotalMileageDataAndSaveToSP() {
        AVQuery<AVObject> query = AVQuery.getQuery(TOTAL_MILEAGE);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();

            @Override
            public void done(List<AVObject> list, AVException e) {
                String todayMileage = "0.0";
                if (e == null) {
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            todayMileage = avObject.getString(OperateDBUtils.TOTAL_MILEAGE);
                        }
                    }
                    RequestUtils.getSharepreferenceEditor(mContext).putString(OperateDBUtils.TOTAL_MILEAGE,
                            todayMileage).commit();
                } else {
                    todayMileage = RequestUtils.getSharepreference(mContext).getString(OperateDBUtils.TOTAL_MILEAGE, "0.0");
                    Log.d("ZZ", "getBabyHeightDataAndSaveToDB e = " + e);
                }
                travelInfoEntity.setTotalMileage(todayMileage);
                L.e("==============","=================="+travelInfoEntity.getTotalMileage());
                mHandle.sendEmptyMessage(0x99);
            }
        });
    }






    Handler mHandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Intent intent = new Intent(OperateDBUtils.QUERY_FINISH);
            mContext.sendBroadcast(intent);
            return true;
        }
    });

    public void getTravelInfoDataAndSaveToDB() {
        AVQuery<AVObject> query = AVQuery.getQuery(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        final Date curDate = Tools.getCurrentDate();
        query.whereEqualTo(OperateDBUtils.DATE, curDate);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    // 删除数据信息
                    mOperateDBUtils.deleteTimeDBTable(OperateDBUtils.TABLE_TRAVEL_URI, Tools.getTimeFromDate(curDate));
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            L.e("done", avObject.toString());
                            TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
                            travelInfoEntity.setTodayMileage(avObject.getString(OperateDBUtils.TODAY_MILEAGE));
                            travelInfoEntity.setAverageSpeed(avObject.getString(OperateDBUtils.AVERAGE_SPEED));
                            String todayMileage = avObject.getString(OperateDBUtils.TOTAL_MILEAGE);
                            travelInfoEntity.setTotalMileage(todayMileage);
                            Date date = avObject.getDate(OperateDBUtils.DATE);


                            RequestUtils.getSharepreferenceEditor(mContext).putString(OperateDBUtils.TOTAL_MILEAGE,
                                    todayMileage).commit();
                            mOperateDBUtils.saveTravelInfoToDB(travelInfoEntity, Tools.getTimeFromDate(date));
                        }
                    }
                    mHandle.sendEmptyMessage(0x99);
                } else {
                    mOperateDBUtils.queryTravelInfoDataFromDB();
                }
            }
        });
    }

    public void getTravelInfo(String address, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = AVQuery.getQuery(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo("bluetoothDeviceId", address);
        query.orderByDescending("updatedAt");
        query.findInBackground(findCallback);

    }


    /**
     * @param tableName BABY_WEIGHT
     *                  BABY_HEIGHT
     *                  TRAVEL_INFO
     * @param date
     * @return
     */

    public void isExistTheTimeOnTable(String tableName, final Date date, final String address, final FindCallback findCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery(tableName);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID, address);
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(findCallback);
    }

    public void isExistTheTimeOnTable(String tableName, final Date date, final FindCallback findCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery(tableName);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(findCallback);
    }

    public void isExistTheUserOnTable(String tableName, final FindCallback<AVObject> findCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery(tableName);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(findCallback);
    }

    public void updateWeightAndTimeToServer(final String weight, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(weight) || date == null) {
            Log.d(TAG, "updateWeightAndTimeToServer weight =  " + weight + " , date = " + date);
            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(BABY_WEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.WEIGHT, weight);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addWeightAndTimeToServer(final String weight, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(weight) || date == null) {
            Log.d(TAG, "addWeightAndTimeToServer weight =  " + weight + " , date = " + date);
            return;
        }

        AVObject postWeight = new AVObject(BABY_WEIGHT);
        postWeight.put("post", AVUser.getCurrentUser());
        postWeight.put(OperateDBUtils.DATE, date);
        postWeight.put(OperateDBUtils.WEIGHT, weight);
        postWeight.saveInBackground(saveCallback);
    }

    public void updateHeightAndTimeToServer(final String height, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(height) || date == null) {
            Log.d(TAG, "updateHeightAndTimeToServer height =  " + height + " , date = " + date);
            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(BABY_HEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.HEIGHT, height);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addHeightAndTimeToServer(final String height, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(height) || date == null) {
            Log.d(TAG, "addHeightAndTimeToServer height =  " + height + " , date = " + date);
            return;
        }

        AVObject postHeight = new AVObject(BABY_HEIGHT);
        postHeight.put("post", AVUser.getCurrentUser());
        postHeight.put(OperateDBUtils.DATE, date);
        postHeight.put(OperateDBUtils.HEIGHT, height);
        postHeight.saveInBackground(saveCallback);
    }

    public void updateTotalMileageAndTimeToServer(final String totalMileage, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(totalMileage)) {
            Log.d(TAG, "updateTotalMileageAndTimeToServer totalMileage =  " + totalMileage);
            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(TOTAL_MILEAGE);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.TOTAL_MILEAGE, totalMileage);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addTotalMileageAndTimeToServer(final String totalMileage, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(totalMileage)) {
            Log.d(TAG, "addTotalMileageAndTimeToServer totalMileage =  " + totalMileage);
            return;
        }
        AVObject postTotalMileage = new AVObject(TOTAL_MILEAGE);
        postTotalMileage.put("post", AVUser.getCurrentUser());
        postTotalMileage.put(OperateDBUtils.TOTAL_MILEAGE, totalMileage);
        postTotalMileage.saveInBackground(saveCallback);
    }

    public void updateTravelInfoAndTimeToServer(final TravelInfoEntity travelInfoEntity, final Date date, final SaveCallback saveCallback) {
        if (travelInfoEntity == null || date == null) {
            Log.d(TAG, "updateTravelInfoAndTimeToServer travelInfoEntity =  " + travelInfoEntity + " , date = " + date);
            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
                            avObjects.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
                            avObjects.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
                            avObjects.put(DEVICEIDENTIFITER,  IContent.getInstacne().clickPositionType);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addTravelInfoAndTimeToServer(final TravelInfoEntity travelInfoEntity, final Date date, final SaveCallback saveCallback) {
        if (travelInfoEntity == null || date == null) {
            Log.d(TAG, "addTravelInfoAndTimeToServer travelInfoEntity =  " + travelInfoEntity + " , date = " + date);
            return;
        }

        AVObject postTotalMileage = new AVObject(TRAVEL_INFO);
        postTotalMileage.put("post", AVUser.getCurrentUser());
        postTotalMileage.put(OperateDBUtils.DATE, date);
        postTotalMileage.put(DEVICEIDENTIFITER,  IContent.getInstacne().clickPositionType);
        postTotalMileage.put("bluetoothDeviceId", IContent.getInstacne().address);
        postTotalMileage.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        postTotalMileage.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        postTotalMileage.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        postTotalMileage.saveInBackground(saveCallback);
    }


    public void selectDeviceTypeAndIdentifier(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(CART_LIST_DETAILS);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 *1000);
        query.findInBackground(findCallback);


    }

    public void thisBlueToothIsBinded(  final String deviceNum, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID,deviceNum);
        query.findInBackground(findCallback);

    }

    public void updateBlueTooth(final boolean isBind, final String deviceNum, final String deviceName, final String functype, final String deviceIndentifier, final SaveCallback saveCallback){


    }

    public void addBlueTooth(final boolean isBind, final String deviceNum, final String deviceName, final String functype, final String deviceIndentifier,String company, final SaveCallback saveCallback){
                         AVObject object_bind = new AVObject(DEVICE_UUID);
                        object_bind.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
                        object_bind.put("bluetoothName", deviceName);
                        object_bind.put("bluetoothBind", isBind);
                        object_bind.put(DEVICEIDENTIFITER, deviceIndentifier);
                        object_bind.put(FUNCTION_TYPE, functype);
                        object_bind.put(COMPANY, company);
                        object_bind.put("post", AVUser.getCurrentUser());
                        object_bind.saveInBackground(saveCallback);
    }

    public void deleteBlueToothIsBind(final String deviceNum, final FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID,deviceNum);
        query.findInBackground(findCallback);
    }

    /**
     * 上传Server device 的运动信息
     *
     * @param today
     * @param total
     * @param speed
     */

    public void saveDataToServer(final String address, final String today, final String total, final String speed, final String type, final SaveCallback saveCallback) {
        L.e("BVBBBB", today + "=" + total + "=" + speed);
        AVQuery<AVObject> query = new AVQuery<>(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo("date", Tools.getCurrentDate());
        query.findInBackground(new FindCallback<AVObject>() {
            boolean isHere = false;

            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    L.e("AAAAAA", list.size() + "");
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObjects = list.get(i);
                        String deviceAddress = avObjects.getString(OperateDBUtils.BLUETOOTH_DEVICE_ID);
                        if (deviceAddress.equals(IContent.getInstacne().address)) {
                            avObjects.put(OperateDBUtils.AVERAGE_SPEED, speed);
                            avObjects.put(OperateDBUtils.TODAY_MILEAGE, today);
                            avObjects.put(OperateDBUtils.TOTAL_MILEAGE, total);
                            avObjects.put("date", Tools.getCurrentDate());
                            avObjects.put(DEVICEIDENTIFITER, type);
                            isHere = true;
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                    if (!isHere) {

                        AVObject avObjects = new AVObject(TRAVEL_INFO);
                        avObjects.put(OperateDBUtils.AVERAGE_SPEED, speed);
                        avObjects.put(OperateDBUtils.TODAY_MILEAGE, today);
                        avObjects.put(OperateDBUtils.TOTAL_MILEAGE, total);
                        avObjects.put(DEVICEIDENTIFITER, type);
                        avObjects.put("date", Tools.getCurrentDate());
                        avObjects.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, address);
                        avObjects.put("post", AVUser.getCurrentUser());
                        avObjects.saveInBackground(saveCallback);
                    }
                }

            }
        });

    }


    public void getHealthStateDataFromServer(String sex, String age, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = AVQuery.getQuery(HEALTH_STATE);
        query.whereEqualTo("sex", sex);
        query.whereEqualTo("age", age);
        query.findInBackground(findCallback);
    }

    public void getCalorFromServer(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = AVQuery.getQuery(Calor);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(findCallback);

    }

    public void sendFeedBackToServer(String content, Date date, String name, SaveCallback saveCallback) {
        AVObject avObject = new AVObject("FeedBack");
        avObject.put("userName", name);
        avObject.put("feedBackContent", content);
        avObject.put("date", date);
        avObject.saveInBackground(saveCallback);

    }

    public static void sendMusicCollect(MusicInfo info, SaveCallback saveCallback) {
        AVObject avObject = new AVObject("MusicPrefer");
        try {
//            AVObject avObject_info = AVObject.parseAVObject(info.avObject);
//            AVFile avFile_info = avObject_info.getAVFile("musicFiles");
            avObject.put("musicName", info.musicName);
            avObject.put("typeNumber", info.type);
            // avObject.put("musicFiles", avFile_info);
            avObject.put("user", AVUser.getCurrentUser());
            avObject.saveInBackground(saveCallback);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("======", e.toString());
        }
    }

    public void getMusicCollect(final FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("MusicPrefer");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.findInBackground(findCallback);

    }

    public void deleteMusicCollect(final String name, final FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("MusicPrefer");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", name);
        query.findInBackground(findCallback);


    }

    public static void downMusicFromNet(Context context, MusicInfo info, ProgressCallback progressCallback) {

        final File file = DownFileUtils.creatFile(context, IContent.FILEM_USIC, info.musicName + ".mp3");

        if (file.exists())
            file.delete();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AVFile avFile = new AVFile(info.musicName + ".mp3", info.lrc, new HashMap<String, Object>());
        avFile.getDataInBackground(new GetDataCallback() {

            FileOutputStream fileOutputStream;
            BufferedOutputStream outputStream;

            @Override
            public void done(byte[] bytes, AVException e) {
                try {
                    fileOutputStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fileOutputStream);
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (Exception e1) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


            }
        }, progressCallback);
    }

    public static void sendMusicDownLoad(final MusicInfo info, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>("MusicDownLoad");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", info.musicName);
        query.whereEqualTo("typeNumber", info.type);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    L.e("==============", "=============" + list.size());
                    if (list.size() > 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("musicName", info.musicName);
                        avObject.put("typeNumber", info.type);
                        avObject.put("user", AVUser.getCurrentUser());
                        avObject.saveInBackground();

                    } else {
                        AVObject avObject = new AVObject("MusicDownLoad");
                        try {
                            AVObject avObject_info = AVObject.parseAVObject(info.avObject);
                            //AVFile avFile_info = avObject_info.getAVFile("musicFiles");
                            avObject.put("musicName", info.musicName);
                            avObject.put("typeNumber", info.type);
                           // avObject.put("musicFiles", avFile_info);
                            avObject.put("user", AVUser.getCurrentUser());
                            avObject.saveInBackground(saveCallback);
                        } catch (Exception e0) {
                            e0.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    public void getInstruction(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(INSTRUCTIONS);
        query.findInBackground(findCallback);


    }

    public void downUpDateOr(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("DeviceUpdate");
        query.orderByDescending("createdAt");
        query.findInBackground(findCallback);
    }


    public static void getDeviceUser(FindCallback<AVObject> findCallback) {
        IContent.getInstacne().addressList.clear();
        AVQuery<AVObject> query = AVQuery.getQuery(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.orderByDescending("updatedAt");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        query.findInBackground(findCallback);

    }


    public  void getUserProtect(FindCallback<AVObject> findCallback) {

        AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.USER_PROTOCOL);
        query.findInBackground(findCallback);

    }

    public void downUpdateFile(AVFile avFile, final File file, ProgressCallback progressCallback) {

        avFile.getDataInBackground(new GetDataCallback() {
            FileOutputStream fileOutputStream;
            BufferedOutputStream outputStream;

            @Override
            public void done(byte[] bytes, AVException e) {
                if (e != null)
                    L.e("AAAA", e.toString());
                try {
                    fileOutputStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fileOutputStream);
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (Exception e1) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


            }

        }, progressCallback);


    }


    private void initProgressDialog() {
        if (mContext == null) {
            progressDialog = new ProgressDialog(MyApplication.getInstance().getApplicationContext());
        } else {
            progressDialog = new ProgressDialog(mContext);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.save_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();

                    ToastUtil.showToast(mContext, mContext.getString(R.string.save_data_failed));
                }
            }
        }, 8000);

    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }


    /**
     * 优化统计接口
     */
    private static  final  String MUSIC_USER = "Music_relate_user";


    public void  sendMusicDown( final String musicName, final SaveCallback saveCallback){
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user",AVUser.getCurrentUser());
        query.whereEqualTo("musicName",musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    if (list != null && list.size() > 0){
                        AVObject avObject = list.get(0);
                        avObject.put("is_down","1");
                        avObject.saveInBackground(saveCallback);
                    }else {
                        AVObject avObject = new AVObject(MUSIC_USER);
                        avObject.put("musicName",musicName);
                        avObject.put("post_user",AVUser.getCurrentUser());
                        avObject.put("is_down","1");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }
    public void sendMusicCollecting(final String musicName, final SaveCallback saveCallback){

        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user",AVUser.getCurrentUser());
        query.whereEqualTo("musicName",musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){

                    if (list != null && list.size() > 0){
                        AVObject avObject = list.get(0);
                        avObject.put("is_collected","1");
                        avObject.saveInBackground(saveCallback);
                    }else {

                        AVObject avObject = new AVObject(MUSIC_USER);
                        avObject.put("musicName",musicName);
                        avObject.put("post_user",AVUser.getCurrentUser());
                        avObject.put("is_collected","1");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public void deleteMusicCollecting(final String musicName, final SaveCallback saveCallback){

        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user",AVUser.getCurrentUser());
        query.whereEqualTo("musicName",musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    if (list != null && list.size() > 0){
                        AVObject avObject = list.get(0);
                        avObject.put("is_collected","0");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }


    public static void setPlayCount(final String musicName ,final int count){
        L.e("count_pre",count+"==================================="+musicName);
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user",AVUser.getCurrentUser());
        query.whereEqualTo("musicName",musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    if (list != null && list.size() > 0){
                        AVObject avObject = list.get(0);
                        avObject.put("play_count",count);
                        avObject.saveInBackground();
                    }else {

                        AVObject avObject = new AVObject(MUSIC_USER);
                        avObject.put("musicName",musicName);
                        avObject.put("post_user",AVUser.getCurrentUser());
                        avObject.put("play_count",count);
                        avObject.saveInBackground();
                    }
                }
            }
        });
    }


}
