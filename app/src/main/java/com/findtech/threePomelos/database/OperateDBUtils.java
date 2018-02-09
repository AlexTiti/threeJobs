package com.findtech.threePomelos.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhi.zhang on 1/3/16.
 */
public class OperateDBUtils {
    private final String TAG = "OperateDBUtils";
    private Context mContext;
    private final Object object = new Object();
    private final Object queryTraveLock = new Object();
    public static final String DATABASE_NAME = "threePomelos.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_BABY_INFO = "babyInfo";
    public static final String TABLE_WEIGHT = "weight";
    public static final String TABLE_HEIGHT = "height";
    public static final String TABLE_TRAVEL_INFO = "travelInfo";
    public static final String ID = "_id";
    public static final String AUTOHORITY = "com.findtech.threePomelos.database.MyContentProvider";

    public static Uri BABYINFO_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_BABY_INFO);
    public static Uri HEIGHT_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_HEIGHT);
    public static Uri WEIGHT_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_WEIGHT);
    public static Uri TABLE_TRAVEL_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_TRAVEL_INFO);

    public static String USERNAME = "username";
    public static String USERPASSWORD = "password";

    public static String BABYNAME = "name";
    public static String BABYSEX = "sex";
    public static String BIRTHDAY = "birthday";
    public static String HEADIMG = "header";
    public static String IS_BIND = "bluetoothBind";
    public static String BLUETOOTH_DEVICE_ID = "bluetoothDeviceId";
    public static String BLUETOOTH_UUID = "bluetoothUUID";
    public static String BABY_INFO_OBJECT_ID = "babyInfoObjectId";

    public static String LOCATION = "location";
    public static String BABYNATIVE = "native";
    public static String USER_ID = "uid";
    public static String TIME = "time";
    public static String DATE = "date";
    public static String HEIGHT = "height";
    public static String WEIGHT = "weight";
    public static String TOTAL_MILEAGE = "totalMileage";
    public static String TOTAL_CALOR = "totalCalor";
    public static String TODAY_CALOR = "todayCalor";
    public static String ADULT_WEIGHT = "adult_weight";
    public static String TODAY_MILEAGE = "todayMileage";

    public static String AVERAGE_SPEED = "averageSpeed";
    public static final String QUERY_FINISH = "com.findtech.threePomelos.database.query.finish";
    public static final String QUERY_DATA = "query_data";

    private ArrayList<PersonDataEntity> timeHeightDataArray = new ArrayList<>();
    private ArrayList<PersonDataEntity> timeWeightDataArray = new ArrayList<>();
    private SaveBabyInfoFinishListener mSaveBabyInfoFinishListener;

    public interface SaveBabyInfoFinishListener {
        void saveBabyInfoFinish();
    }

    public void setSaveBabyInfoFinishListener(SaveBabyInfoFinishListener saveBabyInfoFinishListener) {
        mSaveBabyInfoFinishListener = saveBabyInfoFinishListener;
    }

    public OperateDBUtils(Context context) {
        mContext = context;
    }

    public void queryUserHeightData() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String uid = AVUser.getCurrentUser().getObjectId();
                        String columns[] = new String[]{TIME, USER_ID, HEIGHT};
                        String selection = USER_ID + "=?";
                        cursor = mContext.getContentResolver().query(HEIGHT_URI, columns, selection,
                                new String[]{uid}, null);
                        timeHeightDataArray.clear();
                        Tools.SortArrayList sort = new Tools.SortArrayList();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String timePoint = cursor.getString(cursor.getColumnIndex(TIME));
                                String heightNum = cursor.getString(cursor.getColumnIndex(HEIGHT));
                                timePoint = Tools.getYearFromDataPicker(timePoint) + "-" +
                                        Tools.getMonthFromDataPicker(timePoint) + "-" +
                                        Tools.getDayFromDataPicker(timePoint);
                                PersonDataEntity personDataEntity = new PersonDataEntity();
                                personDataEntity.setTime(timePoint);
                                personDataEntity.setHeight(heightNum);
                                timeHeightDataArray.add(personDataEntity);
                            }
                            Collections.sort(timeHeightDataArray, sort);
                        }
                        if (timeHeightDataArray.size() > 0) {
                            //保存最近的身高
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.HEIGHT,
                                    timeHeightDataArray.get(timeHeightDataArray.size() - 1).getHeight()).commit();
                        } else {
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.HEIGHT,
                                    "0").commit();
                        }
                        MyApplication.getInstance().setUserHeightData(timeHeightDataArray);
                        sendBroadcast(TABLE_HEIGHT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }

    public void saveHeightToDB(String height, String time) {
        String columns[] = new String[]{OperateDBUtils.TIME, OperateDBUtils.HEIGHT};
        Uri uri = OperateDBUtils.HEIGHT_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateHeight(height, time);
            } else {
                insertHeight(height, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void insertHeight(String height, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.HEIGHT, height);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.HEIGHT_URI, values);
    }

    public void updateHeight(String height, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " +OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.HEIGHT, height);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.HEIGHT_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    public void queryTravelInfoDataFromDB() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (queryTraveLock) {
                    Cursor cursor = null;
                    try {
                        String uid = AVUser.getCurrentUser().getObjectId();
                        String columns[] = new String[]{TIME, USER_ID, TODAY_MILEAGE, AVERAGE_SPEED};
                        String selection = OperateDBUtils.TIME + " = ? " + " AND " + USER_ID + " = ?";
                        String time = Tools.getCurrentTime();
                        cursor = mContext.getContentResolver().query(TABLE_TRAVEL_URI, columns, selection,
                                new String[]{time, uid}, null);
                        TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String todayMileage = cursor.getString(cursor.getColumnIndex(TODAY_MILEAGE));
                                String averageSpeed = cursor.getString(cursor.getColumnIndex(AVERAGE_SPEED));
                                travelInfoEntity.setTodayMileage(todayMileage);
                                travelInfoEntity.setAverageSpeed(averageSpeed);
                            }
                        }
                        sendBroadcast(TABLE_TRAVEL_INFO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }

    public void saveTravelInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        String columns[] = new String[]{OperateDBUtils.TIME, OperateDBUtils.TODAY_MILEAGE, OperateDBUtils.AVERAGE_SPEED};
        Uri uri = OperateDBUtils.TABLE_TRAVEL_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateTravelInfo(travelInfoEntity, time);
            } else {
                insertTravelInfo(travelInfoEntity, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateTravelInfo(TravelInfoEntity travelInfoEntity, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " +OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.TABLE_TRAVEL_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    private void insertTravelInfo(TravelInfoEntity travelInfoEntity, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.TABLE_TRAVEL_URI, values);
    }

    public void queryUserWeightData() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String columns[] = new String[]{TIME, USER_ID, WEIGHT};
                        String selection = USER_ID + "=?";
                        cursor = mContext.getContentResolver().query(WEIGHT_URI, columns, selection,
                                new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                        timeWeightDataArray.clear();
                        Tools.SortArrayList sort = new Tools.SortArrayList();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String timePoint = cursor.getString(cursor.getColumnIndex(TIME));
                                String weightNum = cursor.getString(cursor.getColumnIndex(WEIGHT));
                                timePoint = Tools.getYearFromDataPicker(timePoint) + "-" +
                                        Tools.getMonthFromDataPicker(timePoint) + "-" +
                                        Tools.getDayFromDataPicker(timePoint);
                                PersonDataEntity personDataEntity = new PersonDataEntity();
                                personDataEntity.setTime(timePoint);
                                personDataEntity.setWeight(weightNum);
                                timeWeightDataArray.add(personDataEntity);
                            }
                            Collections.sort(timeWeightDataArray, sort);
                        }
                        if (timeWeightDataArray.size() > 0) {
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.WEIGHT,
                                    timeWeightDataArray.get(timeWeightDataArray.size() - 1).getWeight()).commit();
                        } else {
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.WEIGHT,
                                    "0").commit();
                        }

                        MyApplication.getInstance().setUserWeightData(timeWeightDataArray);
                        sendBroadcast(TABLE_WEIGHT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }

    public void saveWeightToDB(String weight, String time) {
        String columns[] = new String[]{OperateDBUtils.TIME, OperateDBUtils.USER_ID, OperateDBUtils.WEIGHT};
        Uri uri = OperateDBUtils.WEIGHT_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateWeightToDB(weight, time);
            } else {
                insertWeightToDB(weight, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void insertWeightToDB(String weight, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.WEIGHT, weight);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.WEIGHT_URI, values);
    }

    public void updateWeightToDB(String weight, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.WEIGHT, weight);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.WEIGHT_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    public void saveMileageInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        String columns[] = new String[]{OperateDBUtils.TIME, OperateDBUtils.USER_ID, OperateDBUtils.TOTAL_MILEAGE,
                OperateDBUtils.TODAY_MILEAGE, OperateDBUtils.AVERAGE_SPEED};
        Uri uri = OperateDBUtils.TABLE_TRAVEL_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateMileageInfoToDB(travelInfoEntity, time);
            } else {
                insertMileageInfoToDB(travelInfoEntity, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void insertMileageInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.TABLE_TRAVEL_URI, values);
    }

    private void updateMileageInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.TABLE_TRAVEL_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    public void saveBabyInfoDataToDB(final List<AVObject> AVObjects) {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    AVObject avObject = null;
                    if (AVObjects != null) {
                        if (AVObjects.size() > 0) {
                            avObject = AVObjects.get(0);
                            AVFile avFile = avObject.getAVFile(HEADIMG);//获取头像
                            if (avFile != null) {
                                avFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, AVException e) {
                                        if (e == null) {
                                            if (PicOperator.bytes2Bitmap(data) != null) {
                                                PicOperator.saveToData(mContext, PicOperator.bytes2Bitmap(data));
                                                sendBroadcast(TABLE_BABY_INFO);
                                            }
                                        }
                                    }
                                });
                            }

                        } else {
                            avObject = null;
                        }
                        String selection = USER_ID + " = ? ";
                        Cursor cursor = null;
                        try {
                            cursor = mContext.getContentResolver().query(BABYINFO_URI, null, selection,
                                    new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                updateBabyInfo(avObject);
                            } else {

                                insertBabyInfo(avObject);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor != null) {
                                queryBabyInfoDataFromDB();
                                cursor.close();
                            }
                        }
                    }
                }
            }
        });
    }

    private void insertBabyInfo(AVObject avObject) {
        ContentValues values = new ContentValues();
        values.put(USER_ID, AVUser.getCurrentUser().getObjectId());
        if (avObject != null) {
            values.put(BABY_INFO_OBJECT_ID, avObject.getObjectId());
            values.put(BABYNAME, avObject.getString(BABYNAME));
            values.put(BABYSEX, avObject.getString(BABYSEX));
            values.put(BIRTHDAY, avObject.getString(BIRTHDAY));
            values.put(HEADIMG, avObject.getAVFile(HEADIMG) == null ? "" : avObject.getAVFile(HEADIMG).getUrl());
            values.put(IS_BIND, avObject.getBoolean(IS_BIND) ? 1 : 0);
            values.put(BLUETOOTH_DEVICE_ID, avObject.getString(BLUETOOTH_DEVICE_ID));
            values.put(BABYNATIVE, avObject.getString(BABYNATIVE));
        }
        mContext.getContentResolver().insert(BABYINFO_URI, values);
    }

    private void updateBabyInfo(AVObject avObject) {
        String where = USER_ID + " = ? ";
        ContentValues values = new ContentValues();
        values.put(USER_ID, AVUser.getCurrentUser().getObjectId());
        if (avObject != null) {
            values.put(BABY_INFO_OBJECT_ID, avObject.getObjectId());
            values.put(BABYNAME, avObject.getString(BABYNAME));
            values.put(BABYSEX, avObject.getString(BABYSEX));
            values.put(BIRTHDAY, avObject.getString(BIRTHDAY));
            values.put(HEADIMG, avObject.getAVFile(HEADIMG) == null ? "" : avObject.getAVFile(HEADIMG).getUrl());
            values.put(IS_BIND, avObject.getBoolean(IS_BIND) ? 1 : 0);
            values.put(BLUETOOTH_DEVICE_ID, avObject.getString(BLUETOOTH_DEVICE_ID));
            values.put(BABYNATIVE, avObject.getString(BABYNATIVE));
        }
        mContext.getContentResolver().update(BABYINFO_URI, values, where,
                new String[]{AVUser.getCurrentUser().getObjectId()});
    }

    public void queryBabyInfoDataFromDB() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String selection = USER_ID + "=?";
                        cursor = mContext.getContentResolver().query(BABYINFO_URI, null, selection,
                                new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                        BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                babyInfoEntity.setBabyName(cursor.getString(cursor.getColumnIndex(BABYNAME)), mContext.getString(R.string.baby_niName));
                                babyInfoEntity.setBabySex(cursor.getString(cursor.getColumnIndex(BABYSEX)),  mContext.getString(R.string.input_sex_baby));
                                babyInfoEntity.setBirthday(cursor.getString(cursor.getColumnIndex(BIRTHDAY)),  mContext.getString(R.string.input_birth_baby));
                                babyInfoEntity.setBabyNative(cursor.getString(cursor.getColumnIndex(BABYNATIVE)),  mContext.getString(R.string.input_address_baby));
                                babyInfoEntity.setImage(cursor.getString(cursor.getColumnIndex(HEADIMG)), "");
                                babyInfoEntity.setIsBind(cursor.getInt(cursor.getColumnIndex(IS_BIND)));
                                babyInfoEntity.setBluetoothDeviceId(cursor.getString(cursor.getColumnIndex(BLUETOOTH_DEVICE_ID)));
                                babyInfoEntity.setBabyInfoObjectId(cursor.getString(cursor.getColumnIndex(BABY_INFO_OBJECT_ID)));
                            }
                        }
                        if (mSaveBabyInfoFinishListener != null) {
                            mSaveBabyInfoFinishListener.saveBabyInfoFinish();
                        }
                        sendBroadcast(TABLE_BABY_INFO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }

    public void saveBlueToothIsBindToDB(final boolean isBind, final String deviceNum) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    try {
                        String where = OperateDBUtils.USER_ID + " = ?";
                        ContentValues values = new ContentValues();
                        values.put(OperateDBUtils.IS_BIND, isBind);
                        values.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
                        mContext.getContentResolver().update(OperateDBUtils.BABYINFO_URI, values, where,
                                new String[]{AVUser.getCurrentUser().getObjectId()});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void sendBroadcast(String data) {
        Intent intent = new Intent(QUERY_FINISH);
        intent.putExtra(QUERY_DATA, data);
        mContext.sendBroadcast(intent);
    }

    public void clearDBTable(Uri tableUri) {
        String where = USER_ID + "=?";
        mContext.getContentResolver().delete(tableUri, where, new String[]{AVUser.getCurrentUser().getObjectId()});
    }

    public void deleteTimeDBTable(Uri tableUri, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + USER_ID + " = ?";
        mContext.getContentResolver().delete(tableUri, where, new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }
}
