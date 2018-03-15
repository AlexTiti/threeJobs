/*
* Copyright (C) 2014 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.findtech.threePomelos.musicserver.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.MusicDB;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;

public class RecentStore {

    private static final int MAX_ITEMS_IN_DB = 100;

    private static RecentStore sInstance = null;

    private MusicDB mMusicDatabase = null;
    private static String WHERE_ID_EQUALS = PlaylistsManager.PlaylistsColumns.TRACK_NAME + "=?";

    public RecentStore(final Context context) {
        mMusicDatabase = MusicDB.getInstance(context);
    }

    public static final synchronized RecentStore getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new RecentStore(context.getApplicationContext());
        }
        return sInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        L.e("version", "onCreat");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + RecentStoreColumns.NAME + " ("
                + RecentStoreColumns.ID + " LONG NOT NULL,"
                + RecentStoreColumns.TIMEPLAYED + " LONG NOT NULL,"
                + RecentStoreColumns.PLAYCOUNT + " INT DEFAULT 0,"
                + PlaylistsManager.PlaylistsColumns.TRACK_ID + " LONG NOT NULL,"
                + PlaylistsManager.PlaylistsColumns.TRACK_NAME + " CHAR NOT NULL,"
                + PlaylistsManager.PlaylistsColumns.ALBUM_ID + " LONG,"
                + PlaylistsManager.PlaylistsColumns.ALBUM_NAME + " CHAR,"
                + PlaylistsManager.PlaylistsColumns.ALBUM_ART + " CHAR,"
                + PlaylistsManager.PlaylistsColumns.ARTIST_ID + " LONG,"
                + PlaylistsManager.PlaylistsColumns.ARTIST_NAME + " CHAR,"
                + PlaylistsManager.PlaylistsColumns.IS_LOCAL + " BOOLEAN ,"
                + PlaylistsManager.PlaylistsColumns.PATH + " CHAR,"
                + PlaylistsManager.PlaylistsColumns.LRC + " CHAR,"
                + PlaylistsManager.PlaylistsColumns.TRACK_ORDER + " LONG , primary key ( "
                + PlaylistsManager.PlaylistsColumns.TRACK_NAME + "));");


//        db.execSQL("CREATE TABLE IF NOT EXISTS " + RecentStoreColumns.NAME + " ("
//                + RecentStoreColumns.ID + " LONG NOT NULL," + RecentStoreColumns.TIMEPLAYED
//                + " LONG NOT NULL);");

    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        L.e("version", oldVersion + "===" + newVersion);
        for (int i = oldVersion; i < newVersion; i++) {
            switch (oldVersion) {
                case 4:
                    onDowngrade(db, oldVersion, newVersion);
                    break;
            }
        }

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        L.e("version", oldVersion + "===" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + RecentStoreColumns.NAME);
        onCreate(db);
    }

    public synchronized void update(int count_first, String name) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(1);
            values.put(RecentStoreColumns.PLAYCOUNT, count_first);
            database.update(RecentStoreColumns.NAME, values, WHERE_ID_EQUALS, new String[]{name});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

//    public synchronized void addSongId(final long songId) {
//
//
//        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
//        database.beginTransaction();
//
//        try {
//            Cursor mostRecentItem = null;
//            try {
//                mostRecentItem = queryRecentIds("1");
//                if (mostRecentItem != null && mostRecentItem.moveToFirst()) {
//                    if (songId == mostRecentItem.getLong(0)) {
//                        return;
//                    }
//                }
//            } finally {
//                if (mostRecentItem != null) {
//                    mostRecentItem.close();
//                    mostRecentItem = null;
//                }
//            }
//
//            /**
//             *  网络歌曲添加进最近播放
//             */
//
//           L.e("=====",songId+"====");
//
//
//            if (songId >=0  && MusicPlayer.getPlayinfos() != null) {
//                MusicInfo info = MusicPlayer.getPlayinfos().get(songId);
//                L.e("=====",info.musicName);
//                final ContentValues values = new ContentValues();
//                values.put(RecentStoreColumns.ID, songId);
//                values.put(RecentStoreColumns.TIMEPLAYED, System.currentTimeMillis());
//                values.put(PlaylistsManager.PlaylistsColumns.TRACK_ID, songId);
//                //values.put(PlaylistsManager.PlaylistsColumns.TRACK_ID, info.songId);
//                values.put(PlaylistsManager.PlaylistsColumns.TRACK_NAME, info.musicName);
//                values.put(PlaylistsManager.PlaylistsColumns.ALBUM_ID, info.albumId);
//                values.put(PlaylistsManager.PlaylistsColumns.ALBUM_NAME, info.albumName);
//                values.put(PlaylistsManager.PlaylistsColumns.ALBUM_ART, info.albumData);
//                values.put(PlaylistsManager.PlaylistsColumns.ARTIST_NAME, info.artist);
//                values.put(PlaylistsManager.PlaylistsColumns.ARTIST_ID, info.artistId);
//                values.put(PlaylistsManager.PlaylistsColumns.PATH, info.data);
//                values.put(PlaylistsManager.PlaylistsColumns.IS_LOCAL, info.islocal);
//                values.put(PlaylistsManager.PlaylistsColumns.LRC, info.lrc);
//                database.insert(RecentStoreColumns.NAME, null, values);
//            }
//
//
//            Cursor oldest = null;
//            try {
//                oldest = database.query(RecentStoreColumns.NAME,
//                        new String[]{RecentStoreColumns.TIMEPLAYED}, null, null, null, null,
//                        RecentStoreColumns.TIMEPLAYED + " ASC");
//
//                if (oldest != null && oldest.getCount() > MAX_ITEMS_IN_DB) {
//                    oldest.moveToPosition(oldest.getCount() - MAX_ITEMS_IN_DB);
//                    long timeOfRecordToKeep = oldest.getLong(0);
//
//                    database.delete(RecentStoreColumns.NAME,
//                            RecentStoreColumns.TIMEPLAYED + " < ?",
//                            new String[]{String.valueOf(timeOfRecordToKeep)});
//
//                }
//            } finally {
//                if (oldest != null) {
//                    oldest.close();
//                    oldest = null;
//                }
//            }
//        } finally {
//            database.setTransactionSuccessful();
//            database.endTransaction();
//        }
//    }

    /**
     * 将歌曲统计和最近播放放在一起,因为无法获取MusicPlayer实例；进程通信会闪退
     *
     * @param songId
     */

    public synchronized void addSongId(final long songId) {


        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            Cursor mostRecentItem = null;
            try {
                mostRecentItem = queryRecentIds("1");
                if (mostRecentItem != null && mostRecentItem.moveToFirst()) {
                    if (songId == mostRecentItem.getLong(0)) {
                        if (mostRecentItem != null) {
                            if (songId >=0  && MusicPlayer.getPlayinfos() != null) {
                                MusicInfo info = MusicPlayer.getPlayinfos().get(songId);
                                if (IContent.MUSIC_NAME != info.musicName) {
                                    IContent.MUSIC_NAME = info.musicName;
                                    mostRecentItem.getColumnIndex(RecentStoreColumns.PLAYCOUNT);
                                    int count_first = mostRecentItem.getInt(mostRecentItem.getColumnIndex(RecentStoreColumns.PLAYCOUNT));
                                    count_first++;
                                    L.e("8.1", count_first + "====" + info.musicName);
                                    update(count_first, info.musicName);
                                }
                            }
                        }
                        return;
                    }
                }
            } finally {
                if (mostRecentItem != null) {
                    mostRecentItem.close();
                }
            }
            if (songId >= 0  && MusicPlayer.getPlayinfos() != null) {
                MusicInfo info = MusicPlayer.getPlayinfos().get(songId);

                if (info == null ){
                    L.e("FFFFFFE","info == null");
                    return;
                }
                L.e("FFFFFFE","MusicPlayer.getPlayinfos()==="+info.musicName);
                final Cursor cursor = database.query(RecentStoreColumns.NAME, null, WHERE_ID_EQUALS,
                        new String[]{info.musicName}, null, null, null);
                int count_pre = 0;
                if (cursor != null && cursor.moveToFirst()) {
                    L.e("count_pre", cursor.getLong(0) + "=" + cursor.getLong(1) + "=" + cursor.getInt(2) + "=" + cursor.getLong(3) + "=" + cursor.getString(4));
                    count_pre = cursor.getInt(cursor.getColumnIndex(RecentStoreColumns.PLAYCOUNT));
//                    count_pre++;
                    L.e("count_pre", "count_pre = " + count_pre);
                    ContentValues values = new ContentValues(3);
                    values.put(RecentStoreColumns.ID, songId);
                    values.put(RecentStoreColumns.TIMEPLAYED, System.currentTimeMillis());
                    values.put(RecentStoreColumns.PLAYCOUNT, count_pre);
                    L.e("count_pre", "count_pre = " + count_pre);
                    database.update(RecentStoreColumns.NAME, values, WHERE_ID_EQUALS, new String[]{info.musicName});
                    NetWorkRequest.setPlayCount(info.musicName, count_pre);
                } else {
                    final ContentValues values = new ContentValues(12);
                    values.put(RecentStoreColumns.ID, songId);
                    values.put(RecentStoreColumns.TIMEPLAYED, System.currentTimeMillis());
                    values.put(RecentStoreColumns.PLAYCOUNT, 1);
                    values.put(PlaylistsManager.PlaylistsColumns.TRACK_ID, info.songId);
                    values.put(PlaylistsManager.PlaylistsColumns.TRACK_NAME, info.musicName);
                    values.put(PlaylistsManager.PlaylistsColumns.ALBUM_ID, info.albumId);
                    values.put(PlaylistsManager.PlaylistsColumns.ALBUM_NAME, info.albumName);
                    values.put(PlaylistsManager.PlaylistsColumns.ALBUM_ART, info.albumData);
                    values.put(PlaylistsManager.PlaylistsColumns.ARTIST_NAME, info.artist);
                    values.put(PlaylistsManager.PlaylistsColumns.ARTIST_ID, info.artistId);
                    values.put(PlaylistsManager.PlaylistsColumns.PATH, info.data);
                    values.put(PlaylistsManager.PlaylistsColumns.IS_LOCAL, info.islocal);
                    values.put(PlaylistsManager.PlaylistsColumns.LRC, info.lrc);
                    database.insert(RecentStoreColumns.NAME, null, values);
                    NetWorkRequest.setPlayCount(info.musicName, 1);
                }
            }
            Cursor oldest = null;
            try {
                oldest = database.query(RecentStoreColumns.NAME,
                        new String[]{RecentStoreColumns.TIMEPLAYED}, null, null, null, null,
                        RecentStoreColumns.TIMEPLAYED + " ASC");
                if (oldest != null && oldest.getCount() > MAX_ITEMS_IN_DB) {
                    oldest.moveToPosition(oldest.getCount() - MAX_ITEMS_IN_DB);
                    long timeOfRecordToKeep = oldest.getLong(0);

                    database.delete(RecentStoreColumns.NAME,
                            RecentStoreColumns.TIMEPLAYED + " < ?",
                            new String[]{String.valueOf(timeOfRecordToKeep)});
                }
            } finally {
                if (oldest != null) {
                    oldest.close();
                    oldest = null;
                }
            }
        } catch (Exception e){
            L.e("count_pre",e.toString());

        }
        finally {
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }
    public synchronized void removeItem(final long songId) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.delete(RecentStoreColumns.NAME, RecentStoreColumns.ID + " = ?", new String[]{
                String.valueOf(songId)
        });

    }

    public synchronized Cursor queryRecentIds(final String limit) {
        final SQLiteDatabase database = mMusicDatabase.getReadableDatabase();
        Cursor cursor = database.query(RecentStoreColumns.NAME,
                new String[]{RecentStoreColumns.ID,RecentStoreColumns.PLAYCOUNT}, null, null, null, null,
                RecentStoreColumns.TIMEPLAYED + " DESC", limit);
        return cursor;
    }

    public ArrayList<MusicInfo> getMusicInfos() {
        ArrayList<MusicInfo> results = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = mMusicDatabase.getReadableDatabase().query(RecentStoreColumns.NAME, null,
                    null, null, null, null, RecentStoreColumns.TIMEPLAYED + " DESC ", null);
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());
                L.e("SixItem", cursor.getLong(1) + "==" + cursor.getString(2) + "==" + cursor.getInt(3) + "==" + cursor.getInt(4) + "==" + cursor.getString(5) + "==" + cursor.getInt(6) + "==" + cursor.getInt(7));
                do {
                    MusicInfo info = new MusicInfo();
                    info.songId = cursor.getLong(3);
                    info.musicName = cursor.getString(4);
                    info.albumId = cursor.getInt(5);
                    info.albumName = cursor.getString(6);
                    info.albumData = cursor.getString(7);
                    info.artistId = cursor.getLong(8);
                    info.artist = cursor.getString(9);
                    info.islocal = cursor.getInt(10) > 0;
                    info.lrc = cursor.getString(cursor.getColumnIndex(PlaylistsManager.PlaylistsColumns.LRC));
                    results.add(info);
                } while (cursor.moveToNext());
            }

            return results;

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }


    public interface RecentStoreColumns {
        /* Table name */
        String NAME = "recenthistory";

        /* Album IDs column */
        String ID = "songid";

        /* Time played column */
        String TIMEPLAYED = "timeplayed";

        String PLAYCOUNT = "playcount";
    }
}
