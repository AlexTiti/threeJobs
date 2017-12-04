/**
 * Copyright (lrc_arrow) www.longdw.com
 */
package com.findtech.threePomelos.music.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Files.FileColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.utils.IContent;
import com.github.promeg.pinyinhelper.Pinyin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MusicUtils implements IConstants {

    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟


    private static String[] proj_music = new String[]{
            Media._ID, Media.TITLE,
            Media.DATA, Media.ALBUM_ID,
            Media.ALBUM, Media.ARTIST,
            Media.ARTIST_ID, Media.DURATION, Media.SIZE};
    private static String[] proj_album = new String[]{Albums._ID, Albums.ALBUM_ART,
            Albums.ALBUM, Albums.NUMBER_OF_SONGS, Albums.ARTIST};


    /**
     * @param context
     * @param from    不同的界面进来要做不同的查询
     * @return
     */
    public static List<MusicInfo> queryMusic( Context context, int from) {
        return queryMusic(context, null, from);
    }

    public static ArrayList<MusicInfo> queryMusic(Context context, String id, int from) {

        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();
        switch (from) {
            case START_FROM_LOCAL:
                ArrayList<MusicInfo> list3 = getMusicListCursor(cr.query(uri, proj_music,
                        null, null,
                        songSortOrder));
                return list3;

            case START_FROM_FOLDER:
                ArrayList<MusicInfo> list1 = new ArrayList<>();
                ArrayList<MusicInfo> list = getMusicListCursor(cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj_music,
                        null, null,
                        null));
                IContent.getInstacne().map.clear();
                for (MusicInfo music : list) {
                    if (music.data.substring(0, music.data.lastIndexOf(File.separator)).equals(id)) {
                        list1.add(music);
                        IContent.getInstacne().map.put(music.musicName,music);
                    }
                }
                return list1;
            default:
                return null;
        }

    }




    public static ArrayList<MusicInfo> getMusicListCursor(Cursor cursor) {
        if (cursor == null) {
            L.e("===========","===========+cursor == null");
            return null;
        }
        L.e("===========","===========+cursor"+cursor.getCount());
        String url ;
        ArrayList<MusicInfo> musicList = new ArrayList<>();
        while (cursor.moveToNext()) {
            url = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA));
            MusicInfo music = new MusicInfo();
            music.songId = cursor.getInt(cursor
                    .getColumnIndex(Media._ID));
            music.albumId = cursor.getInt(cursor
                    .getColumnIndex(Media.ALBUM_ID));
            music.albumName = cursor.getString(cursor
                    .getColumnIndex(Albums.ALBUM));
            music.albumData = getAlbumArtUri(music.albumId) + "";
            music.duration = cursor.getInt(cursor
                    .getColumnIndex(Media.DURATION));
            music.musicName = cursor.getString(cursor
                    .getColumnIndex(Media.TITLE));
            music.artist = cursor.getString(cursor
                    .getColumnIndex(Media.ARTIST));
            music.artistId = cursor.getLong(cursor.getColumnIndex(Media.ARTIST_ID));
            String filePath = cursor.getString(cursor
                    .getColumnIndex(Media.DATA));
            music.data = filePath;
            music.folder = filePath.substring(0, filePath.lastIndexOf(File.separator));
            music.size = cursor.getInt(cursor
                    .getColumnIndex(Media.SIZE));
            music.islocal = true;
            music.sort = Pinyin.toPinyin(music.musicName.charAt(0)).substring(0, 1).toUpperCase();
            musicList.add(music);

        }
        cursor.close();
        return musicList;
    }


    public static String makeTimeString(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long m = milliSecs / (60 * 1000);
        sb.append(m < 10 ? "0" + m : m);
        sb.append(":");
        long s = (milliSecs % (60 * 1000)) / 1000;
        sb.append(s < 10 ? "0" + s : s);
        return sb.toString();
    }


    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }



    public static String getAlbumdata(Context context, long musicid) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, proj_music, "_id = " + String.valueOf(musicid), null, null);
        if (cursor == null) {
            return null;
        }
        long albumId = -1;
        if (cursor.moveToNext()) {
            albumId = cursor.getLong(cursor.getColumnIndex(Media.ALBUM_ID));
        }

        if (albumId != -1) {
            cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, proj_album, Albums._ID + " = " + String.valueOf(albumId), null, null);
        }
        if (cursor == null) {
            return null;
        }
        String data = "";
        if (cursor.moveToNext()) {
            data = cursor.getString(cursor.getColumnIndex(Albums.ALBUM_ART));
        }
        cursor.close();

        return data;
    }




    public static String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }


}

