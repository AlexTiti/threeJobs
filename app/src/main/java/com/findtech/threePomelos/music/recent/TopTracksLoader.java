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

package com.findtech.threePomelos.music.recent;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;


import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.RecentStore;

import java.util.ArrayList;

public class TopTracksLoader extends SongLoader {

    public static final int NUMBER_OF_SONGS = 99;
    public static QueryType mQueryType;
    private static Context mContext;

    public TopTracksLoader(final Context context, QueryType type) {
        mContext = context;
        mQueryType = type;
    }

    public static Cursor getCursor(Context context, QueryType queryType) {
        mQueryType = queryType;
        mContext = context;
        return getCursor();
    }

    public static int getCount(Context context, QueryType queryType) {
        Cursor cursor = getCursor(context, queryType);
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    public static Cursor getCursor() {
        SortedCursor retCursor = null;
        if (mQueryType == QueryType.TopTracks) {
            retCursor = makeTopTracksCursor(mContext);
        } else if (mQueryType == QueryType.RecentSongs) {
            retCursor = makeRecentTracksCursor(mContext);
            L.e("===================","retCursor.getCount()=================="+retCursor.getCount());
        }

        if (retCursor != null) {
            ArrayList<Long> missingIds = retCursor.getMissingIds();
            if (missingIds != null && missingIds.size() > 0) {
                for (long id : missingIds) {
                    if (mQueryType == QueryType.TopTracks) {
                        SongPlayCount.getInstance(mContext).removeItem(id);
                    } else if (mQueryType == QueryType.RecentSongs) {
                        RecentStore.getInstance(mContext).removeItem(id);
                    }
                }
            }
        }

        return retCursor;
    }

    public static final SortedCursor makeTopTracksCursor(final Context context) {

        Cursor songs = SongPlayCount.getInstance(context).getTopPlayedResults(NUMBER_OF_SONGS);

        try {
            return makeSortedCursor(context, songs,
                    songs.getColumnIndex(SongPlayCount.SongPlayCountColumns.ID));
        } finally {
            if (songs != null) {
                songs.close();
            }
        }
    }

    public static final SortedCursor makeRecentTracksCursor(final Context context) {

        Cursor songs = RecentStore.getInstance(context).queryRecentIds(null);
        L.e("==============","makeRecentTracksCursor+=================="+songs.getCount());

        try {
            return makeSortedCursor(context, songs,
                    songs.getColumnIndex(SongPlayCount.SongPlayCountColumns.ID));
        } finally {
            if (songs != null) {
                songs.close();
            }
        }
    }

    public static final SortedCursor makeSortedCursor(final Context context, final Cursor cursor,
                                                      final int idColumn) {
        if (cursor != null && cursor.moveToFirst()) {

            StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID);
            selection.append(" IN (");

            long[] order = new long[cursor.getCount()];
            long id = cursor.getLong(idColumn);
            L.e("=====================",cursor.getPosition()+"makeSortedCursor"+cursor.getCount()+"==========================="+id);
            selection.append(id);
            order[cursor.getPosition()] = id;

            while (cursor.moveToNext()) {
                selection.append(",");
                id = cursor.getLong(idColumn);
                L.e("=====================",cursor.getPosition()+"while (cursor.moveToNext())==========================="+id);
                order[cursor.getPosition()] = id;
                selection.append(String.valueOf(id));
            }
            selection.append(")");
            L.e("=====================","while (cursor.moveToNext())==========================="+selection.toString());
            Cursor songCursor = makeSongCursor(context, selection.toString(), null);
            L.e("=====================","while (cursor.moveToNext())==========================="+songCursor.getCount());
            if (songCursor != null) {
                /**
                 *
                 */
                SortedCursor sortedCursor = new SortedCursor(songCursor, order, BaseColumns._ID, null);
                L.e("=====================","while (cursor.moveToNext())==========================="+sortedCursor.getCount());
                return  sortedCursor ;
            }
        }

        return null;
    }


    public enum QueryType {
        TopTracks,
        RecentSongs,
    }
}
