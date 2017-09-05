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

public class SearchHistory {

    private static final int MAX_ITEMS_IN_DB = 25;

    private static SearchHistory sInstance = null;

    private MusicDB mMusicDatabase = null;

    public SearchHistory(final Context context) {
        mMusicDatabase = MusicDB.getInstance(context);
    }

    public static final synchronized SearchHistory getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new SearchHistory(context.getApplicationContext());
        }
        return sInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SearchHistoryColumns.NAME + " ("
                + SearchHistoryColumns.SEARCHSTRING + " TEXT NOT NULL,"
                + SearchHistoryColumns.TIMESEARCHED + " LONG NOT NULL);");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchHistoryColumns.NAME);
        onCreate(db);
    }

    public interface SearchHistoryColumns {
        /* Table name */
        String NAME = "searchhistory";

        /* What was searched */
        String SEARCHSTRING = "searchstring";

        /* Time of search */
        String TIMESEARCHED = "timesearched";
    }
}
