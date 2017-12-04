/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.findtech.threePomelos.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    public static final String SONG_SORT_ORDER = "song_sort_order";
    private static final String FAVRIATE_MUSIC_PLAYLIST = "favirate_music_playlist";

    private static final String CURRENT_DATE = "currentdate";
    private static final String NOTIFY_BLUETOOTH = "notifyBluetooth";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setNotifyBluetooth(boolean lean){
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(NOTIFY_BLUETOOTH,lean);
        editor.apply();
    }

    public boolean getNotifyBoolean(){

        return mPreferences.getBoolean(NOTIFY_BLUETOOTH, true);
    }



    public void setCurrentDate(String str){
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(CURRENT_DATE, str);
        editor.apply();
    }

    public boolean getFavriateMusicPlaylist() {
        return mPreferences.getBoolean(FAVRIATE_MUSIC_PLAYLIST, false);
    }

    public void setFavriateMusicPlaylist(boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FAVRIATE_MUSIC_PLAYLIST, b);
        editor.apply();
    }



    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }


}