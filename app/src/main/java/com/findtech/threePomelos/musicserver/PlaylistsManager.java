package com.findtech.threePomelos.musicserver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;


public class PlaylistsManager {


    private static PlaylistsManager sInstance = null;

    private MusicDB mMusicDatabase = null;
    private long favPlaylistId = IConstants.FAV_PLAYLIST;

    public PlaylistsManager(final Context context) {
        mMusicDatabase = MusicDB.getInstance(context);
    }

    public static final synchronized PlaylistsManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PlaylistsManager(context.getApplicationContext());
        }
        return sInstance;
    }

    //建立播放列表表设置播放列表id和歌曲id为复合主键
    public void onCreate(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + PlaylistsColumns.NAME + " ("
                + PlaylistsColumns.PLAYLIST_ID + " LONG NOT NULL," + PlaylistsColumns.TRACK_ID + " LONG NOT NULL,"
                + PlaylistsColumns.TRACK_NAME + " CHAR NOT NULL," + PlaylistsColumns.ALBUM_ID + " LONG,"
                + PlaylistsColumns.ALBUM_NAME + " CHAR," + PlaylistsColumns.ALBUM_ART + " CHAR,"
                + PlaylistsColumns.ARTIST_ID + " LONG," + PlaylistsColumns.ARTIST_NAME + " CHAR,"
                + PlaylistsColumns.IS_LOCAL + " BOOLEAN ," + PlaylistsColumns.PATH + " CHAR," + PlaylistsColumns.LRC + " CHAR,"
                + PlaylistsColumns.TRACK_ORDER + " LONG NOT NULL, primary key ( " + PlaylistsColumns.PLAYLIST_ID
                + ", " + PlaylistsColumns.TRACK_ID + "));");

    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistsColumns.NAME);
        onCreate(db);
    }



    public synchronized void insertMusic(Context context, long playlistid,
                                         MusicInfo info) {

        L.e("QQQ",playlistid+"=="+getPlaylist(playlistid).size());
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(11);
            values.put(PlaylistsColumns.PLAYLIST_ID, playlistid);
            values.put(PlaylistsColumns.TRACK_ID, info.songId);
            values.put(PlaylistsColumns.TRACK_ORDER, getPlaylist(playlistid).size());
            values.put(PlaylistsColumns.TRACK_NAME, info.musicName);
            values.put(PlaylistsColumns.ALBUM_ID, info.albumId);
            values.put(PlaylistsColumns.ALBUM_NAME, info.albumName);
            values.put(PlaylistsColumns.ALBUM_ART, info.albumData);
            values.put(PlaylistsColumns.ARTIST_NAME, info.artist);
            values.put(PlaylistsColumns.ARTIST_ID, info.artistId);
            values.put(PlaylistsColumns.PATH, info.data);
            values.put(PlaylistsColumns.IS_LOCAL, info.islocal);
            values.put(PlaylistsColumns.LRC, info.lrc);
            L.e("QQQ",info.musicName+"=="+info.lrc);
            database.insertWithOnConflict(PlaylistsColumns.NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        PlaylistInfo playlistInfo = PlaylistInfo.getInstance(context);
        String albumart = info.albumData;
        if (info.islocal) {
            if (albumart.equals(MusicUtils.getAlbumdata(MyApplication.mContext, info.songId))) {
                playlistInfo.update(playlistid, getPlaylist(playlistid).size(), info.albumData);
                L.e("QQQQQQ","albumart.equals(");
            } else {
                playlistInfo.update(playlistid, getPlaylist(playlistid).size());
                L.e("QQQQQQ","else");
            }
        } else if (  albumart != null &&!albumart.isEmpty()) {
            playlistInfo.update(playlistid, getPlaylist(playlistid).size(), info.albumData);
        } else {
            playlistInfo.update(playlistid, getPlaylist(playlistid).size());
        }
    }
    public synchronized void update(long playlistid, long id, int order) {

        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(1);
            values.put(PlaylistsColumns.TRACK_ORDER, order);
            database.update(PlaylistsColumns.NAME, values, PlaylistsColumns.PLAYLIST_ID + " = ?" + " AND " +
                    PlaylistsColumns.TRACK_ID + " = ?", new String[]{playlistid + "", id + ""});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }




    public synchronized void update(long playlistid, long[] ids, int[] order) {

        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            for (int i = 0; i < order.length; i++) {
                ContentValues values = new ContentValues(1);
                values.put(PlaylistsColumns.TRACK_ORDER, order[i]);
                database.update(PlaylistsColumns.NAME, values, PlaylistsColumns.PLAYLIST_ID + " = ?" + " AND " +
                        PlaylistsColumns.TRACK_ID + " = ?", new String[]{playlistid + "", ids[i] + ""});
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }

    public void removeItem(Context context, final long playlistId, long songId) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.delete(PlaylistsColumns.NAME, PlaylistsColumns.PLAYLIST_ID + " = ?" + " AND " + PlaylistsColumns.TRACK_ID + " = ?", new String[]{
                String.valueOf(playlistId), String.valueOf(songId)
        });

        PlaylistInfo playlistInfo = PlaylistInfo.getInstance(context);
        playlistInfo.update(playlistId, getPlaylist(playlistId).size());

    }

    public void delete(final long PlaylistId) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.delete(PlaylistsColumns.NAME, PlaylistsColumns.PLAYLIST_ID + " = ?", new String[]
                {String.valueOf(PlaylistId)});
    }



    public long[] getPlaylistIds(final long playlistid) {
        long[] results = null;

        Cursor cursor = null;
        try {
            cursor = mMusicDatabase.getReadableDatabase().query(PlaylistsColumns.NAME, null,
                    PlaylistsColumns.PLAYLIST_ID + " = " + String.valueOf(playlistid), null, null, null, PlaylistsColumns.TRACK_ORDER + " ASC ", null);
            if (cursor != null) {
                int len = cursor.getCount();
                results = new long[len];
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < len; i++) {
                        results[i] = cursor.getLong(1);
                        cursor.moveToNext();
                    }

                }
            }

            return results;

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public ArrayList<MusicTrack> getPlaylist(final long playlistid) {
        ArrayList<MusicTrack> results = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = mMusicDatabase.getReadableDatabase().query(PlaylistsColumns.NAME, null,
                    PlaylistsColumns.PLAYLIST_ID + " = " + String.valueOf(playlistid), null, null, null, PlaylistsColumns.TRACK_ORDER + " ASC ", null);
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());

                do {
                    results.add(new MusicTrack(cursor.getLong(1), cursor.getInt(0)));
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

    public ArrayList<MusicInfo> getMusicInfos(final long playlistid) {
        ArrayList<MusicInfo> results = new ArrayList<>();

        L.e("=====","playlistid===="+playlistid);

        Cursor cursor = null;
        try {
            cursor = mMusicDatabase.getReadableDatabase().query(PlaylistsColumns.NAME, null,
                    PlaylistsColumns.PLAYLIST_ID + " = " + String.valueOf(playlistid), null, null, null, PlaylistsColumns.TRACK_ORDER + " ASC ", null);
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());

                IContent.getInstacne().collectedList.clear();
                do {
                    MusicInfo info = new MusicInfo();
                    info.songId = cursor.getLong(1);
                    info.musicName = cursor.getString(2);
                    info.albumId = cursor.getInt(3);
                    info.albumName = cursor.getString(4);
                    info.albumData = cursor.getString(5);
                    info.artistId = cursor.getLong(6);
                    info.artist = cursor.getString(7);
                    info.islocal = cursor.getInt(8) > 0;
                    info.lrc = cursor.getString(cursor.getColumnIndex(PlaylistsColumns.LRC));
                    results.add(info);
                    IContent.getInstacne().collectedList.add(info.musicName);

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

    public interface PlaylistsColumns {
        /* Table name */
            String NAME = "playlists";

        /* Album IDs column */
        String PLAYLIST_ID = "playlist_id";

        /* Time played column */
        String TRACK_ID = "track_id";

        String TRACK_ORDER = "track_order";

        String TRACK_NAME = "track_name";

        String ARTIST_NAME = "artist_name";

        String ARTIST_ID = "artist_id";

        String ALBUM_NAME = "album_name";

        String ALBUM_ID = "album_id";

        String IS_LOCAL = "is_local";

        String IS_FAV = "is_fav";

        String PATH = "path";

        String ALBUM_ART = "album_art";

        String LRC = "lrc";
    }
}
