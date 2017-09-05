package com.findtech.threePomelos.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

/**
 * Created by zhi.zhang on 1/3/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_WEIGHT_CREATE = "create table " + OperateDBUtils.TABLE_WEIGHT +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.TIME + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.WEIGHT + " text not null);";

    private static final String DB_HEIGHT_CREATE = "create table " + OperateDBUtils.TABLE_HEIGHT +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.TIME + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.HEIGHT + " text not null);";

    private static final String DB_BABY_INFO_CREATE = "create table " + OperateDBUtils.TABLE_BABY_INFO +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.BABY_INFO_OBJECT_ID + " text, " +
            OperateDBUtils.BABYNAME + " text, " +
            OperateDBUtils.BABYSEX + " text, " +
            OperateDBUtils.BIRTHDAY + " text, " +
            OperateDBUtils.HEADIMG + " text, " +
            OperateDBUtils.IS_BIND + " integer, " +
            OperateDBUtils.BLUETOOTH_DEVICE_ID + " text, " +
            OperateDBUtils.BABYNATIVE + " text);";

    private static final String DB_TRAVEL_INFO_CREATE = "create table " + OperateDBUtils.TABLE_TRAVEL_INFO +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.TIME + " text  not null, " +
            OperateDBUtils.TOTAL_MILEAGE + " text, " +
            OperateDBUtils.TODAY_MILEAGE + " text, " +
            OperateDBUtils.AVERAGE_SPEED + " text);";

    public DatabaseHelper(Context context) {
        super(context, OperateDBUtils.DATABASE_NAME, null, OperateDBUtils.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_WEIGHT_CREATE);
        sqLiteDatabase.execSQL(DB_HEIGHT_CREATE);
        sqLiteDatabase.execSQL(DB_BABY_INFO_CREATE);
        sqLiteDatabase.execSQL(DB_TRAVEL_INFO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        onCreate(sqLiteDatabase);

        if (1 == oldVersion) {
            //TODO
            upgradeToVersion2(sqLiteDatabase);
            oldVersion = 2;
        }
        if (2 == oldVersion) {
            //TODO
            //oldVersion = 3;
        }

        if (oldVersion != newVersion) {
            throw new IllegalStateException(
                    "error upgrading the database to version " + newVersion);
        }
    }

    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        SQLiteDatabase db;
        Cursor cursor;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    public String dropTable(String tableName) {
        if (tableName == null) {
            return null;
        }
        SQLiteDatabase db = null;
        db = this.getReadableDatabase();
        String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(DROP_TABLE);
        return tableName;
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + OperateDBUtils.TABLE_BABY_INFO
                + " ADD " + OperateDBUtils.BABYNATIVE + " text;");
    }
}