package com.findtech.threePomelos.musicserver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.findtech.threePomelos.music.info.Playlist;
import com.findtech.threePomelos.music.utils.L;

import java.util.ArrayList;

/**
 * Created by wm on 2016/3/3.
 */
public class PlaylistInfo {

    private static PlaylistInfo sInstance = null;

    private MusicDB mMusicDatabase = null;

    public PlaylistInfo(final Context context) {
        mMusicDatabase = MusicDB.getInstance(context);
    }

    public static final synchronized PlaylistInfo getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PlaylistInfo(context.getApplicationContext());
        }
        return sInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PlaylistInfoColumns.NAME + " ("
                + PlaylistInfoColumns.PLAYLIST_ID + " LONG NOT NULL," + PlaylistInfoColumns.PLAYLIST_NAME + " CHAR NOT NULL,"
                + PlaylistInfoColumns.SONG_COUNT + " INT NOT NULL, " + PlaylistInfoColumns.ALBUM_ART + " CHAR, "
                + PlaylistInfoColumns.AUTHOR + " CHAR );");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistInfoColumns.NAME);
        onCreate(db);
    }


    public synchronized void addPlaylist(long playlistid, String name, int count, String albumart, String author) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(5);
            values.put(PlaylistInfoColumns.PLAYLIST_ID, playlistid);
            values.put(PlaylistInfoColumns.PLAYLIST_NAME, name);
            values.put(PlaylistInfoColumns.SONG_COUNT, count);
            values.put(PlaylistInfoColumns.ALBUM_ART, albumart);
            values.put(PlaylistInfoColumns.AUTHOR, author);

            database.insert(PlaylistInfoColumns.NAME, null, values);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }



    public synchronized void update(long playlistid, int count) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(2);
            values.put(PlaylistInfoColumns.PLAYLIST_ID, playlistid);
            //values.put(PlaylistInfoColumns.PLAYLIST_NAME, name);
            values.put(PlaylistInfoColumns.SONG_COUNT, count);
            database.update(PlaylistInfoColumns.NAME, values, PlaylistInfoColumns.PLAYLIST_ID + " = " + playlistid, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public synchronized void update(long playlistid, int count, String album) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(3);
            values.put(PlaylistInfoColumns.PLAYLIST_ID, playlistid);
            values.put(PlaylistInfoColumns.SONG_COUNT, count);
            values.put(PlaylistInfoColumns.ALBUM_ART, album);
            database.update(PlaylistInfoColumns.NAME, values, PlaylistInfoColumns.PLAYLIST_ID + " = " + playlistid, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }


    public synchronized ArrayList<Playlist> getPlaylist() {
        ArrayList<Playlist> results = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = mMusicDatabase.getReadableDatabase().query(PlaylistInfoColumns.NAME, null,
                    null, null, null, null, null);
            L.e("QQQQQQ==",String.valueOf(cursor == null));
            if (cursor != null && cursor.moveToFirst()) {
                results.ensureCapacity(cursor.getCount());
                L.e("QQQQQQ==",cursor.getCount()+"");
                do {
                    L.e("===========","0="+cursor.getLong(0)+"1="+cursor.getString(1)+"2="+cursor.getInt(2)+"3="+cursor.getString(3)+"4="+cursor.getString(4));
                    if (cursor.getString(4).equals("local"))
                        results.add(new Playlist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2),
                                cursor.getString(3), cursor.getString(4)));
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


    public interface PlaylistInfoColumns {
        /* Table name */
        String NAME = "playlist_info";

        /* Album IDs column */
        String PLAYLIST_ID = "playlist_id";

        /* Time played column */
        String PLAYLIST_NAME = "playlist_name";

        String SONG_COUNT = "count";

        String ALBUM_ART = "album_art";

        String AUTHOR = "author";
    }

}
