package com.findtech.threePomelos.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 *
 * @author zhi.zhang
 * @date 1/3/16
 */
public class MyContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper = null;
    private ContentResolver resolver = null;
    private Context mContext;

    private static final int WEIGHT = 1;
    private static final int HEIGHT = 2;
    private static final int BABY_INFO = 3;
    private static final int TRAVEL_INFO = 4;
    private static final UriMatcher sMatcher;

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_WEIGHT, WEIGHT);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_HEIGHT, HEIGHT);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_BABY_INFO, BABY_INFO);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_TRAVEL_INFO, TRAVEL_INFO);
    }


    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String table;
        int id ;
        switch (sMatcher.match(uri)) {
            case WEIGHT:
                table = OperateDBUtils.TABLE_WEIGHT;
                break;
            case HEIGHT:
                table = OperateDBUtils.TABLE_HEIGHT;
                break;
            case BABY_INFO:
                table = OperateDBUtils.TABLE_BABY_INFO;
                break;
            case TRAVEL_INFO:
                table = OperateDBUtils.TABLE_TRAVEL_INFO;
                break;
            default:
                throw new IllegalArgumentException("this is unkown uri:" + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        id = db.delete(table, selection, selectionArgs);
        if (id < 0) {
            throw new SQLiteException("Unable to delete " + selection + " for " + uri);
        }
        resolver.notifyChange(uri, null);
        return id;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        switch (sMatcher.match(uri)) {
            case WEIGHT:
                table = OperateDBUtils.TABLE_WEIGHT;
                break;
            case HEIGHT:
                table = OperateDBUtils.TABLE_HEIGHT;
                break;
            case BABY_INFO:
                table = OperateDBUtils.TABLE_BABY_INFO;
                break;
            case TRAVEL_INFO:
                table = OperateDBUtils.TABLE_TRAVEL_INFO;
                break;
            default:
                throw new IllegalArgumentException("this is unkown uri:" + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(table, OperateDBUtils.ID, values);
        if (id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public boolean onCreate() {

        mContext = getContext();
        resolver = mContext.getContentResolver();
        dbHelper = new DatabaseHelper(mContext);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;
        switch (sMatcher.match(uri)) {
            case WEIGHT:
                try {
                    sqlBuilder.setTables(OperateDBUtils.TABLE_WEIGHT);
                    cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
                    cursor.setNotificationUri(resolver, uri);
                } catch (Exception e) {

                }
                break;
            case HEIGHT:
                try {
                    sqlBuilder.setTables(OperateDBUtils.TABLE_HEIGHT);
                    cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
                    cursor.setNotificationUri(resolver, uri);
                } catch (Exception e) {

                }
                break;
            case BABY_INFO:
                try {
                    sqlBuilder.setTables(OperateDBUtils.TABLE_BABY_INFO);
                    cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
                    cursor.setNotificationUri(resolver, uri);
                } catch (Exception e) {

                }
                break;
            case TRAVEL_INFO:
                try {
                    sqlBuilder.setTables(OperateDBUtils.TABLE_TRAVEL_INFO);
                    cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
                    cursor.setNotificationUri(resolver, uri);
                } catch (Exception e) {

                }
                break;
            default:
                throw new IllegalArgumentException("this is unkown uri:" + uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id;
        switch (sMatcher.match(uri)) {
            case WEIGHT:
                id = db.update(OperateDBUtils.TABLE_WEIGHT, values, selection, selectionArgs);
                if (id < 0) {
                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
                }
                Uri wUri = ContentUris.withAppendedId(uri, id);
                resolver.notifyChange(wUri, null);
                return id;
            case HEIGHT:
                id = db.update(OperateDBUtils.TABLE_HEIGHT, values, selection, selectionArgs);
                if (id < 0) {
                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
                }
                Uri hUri = ContentUris.withAppendedId(uri, id);
                resolver.notifyChange(hUri, null);
                return id;
            case BABY_INFO:
                id = db.update(OperateDBUtils.TABLE_BABY_INFO, values, selection, selectionArgs);
                if (id < 0) {
                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
                }
                Uri babyInfoUri = ContentUris.withAppendedId(uri, id);
                resolver.notifyChange(babyInfoUri, null);
                return id;
            case TRAVEL_INFO:
                id = db.update(OperateDBUtils.TABLE_TRAVEL_INFO, values, selection, selectionArgs);
                if (id < 0) {
                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
                }
                Uri travelInfoUri = ContentUris.withAppendedId(uri, id);
                resolver.notifyChange(travelInfoUri, null);
                return id;
            default:
                throw new IllegalArgumentException("this is unkown uri:" + uri);
        }
    }

}
