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

package com.findtech.threePomelos.musicserver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.findtech.threePomelos.musicserver.control.MusicPlaybackState;
import com.findtech.threePomelos.musicserver.info.PlaylistInfo;
import com.findtech.threePomelos.musicserver.control.PlaylistsManager;
import com.findtech.threePomelos.musicserver.control.RecentStore;

public class MusicDB extends SQLiteOpenHelper {

    public static final String DATABASENAME = "musicdb.db";
    private static final int VERSION = 5;
    private static MusicDB sInstance = null;

    private final Context mContext;

    public MusicDB(final Context context) {
        super(context, DATABASENAME, null, VERSION);
        mContext = context;
    }
    public static final synchronized MusicDB getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new MusicDB(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MusicPlaybackState.getInstance(mContext).onCreate(db);
        RecentStore.getInstance(mContext).onCreate(db);
       // SongPlayCount.getInstance(mContext).onCreate(db);

        PlaylistInfo.getInstance(mContext).onCreate(db);
        PlaylistsManager.getInstance(mContext).onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion){
            case 5 :
                RecentStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
                break;
        }



    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
