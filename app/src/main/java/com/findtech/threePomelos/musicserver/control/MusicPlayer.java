/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.findtech.threePomelos.musicserver.control;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.findtech.threePomelos.MediaAidlInterface;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.MediaService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.WeakHashMap;

public class MusicPlayer {

    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;
    private static final long[] sEmptyList;
    public static MediaAidlInterface mService = null;
    private static ContentValues[] mContentValuesCache = null;

    static {
        mConnectionMap = new WeakHashMap<Context, ServiceBinder>();
        sEmptyList = new long[0];
    }

    /**
     * bind service at base
     * @param context
     * @param callback
     * @return
     */
    public static final ServiceToken bindToService(final Context context,
                                                   final ServiceConnection callback) {

        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        contextWrapper.startService(new Intent(contextWrapper, MediaService.class));
        final ServiceBinder binder = new ServiceBinder(callback,
                contextWrapper.getApplicationContext());
        if (contextWrapper.bindService(
                new Intent().setClass(contextWrapper, MediaService.class), binder, 0)) {
            mConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }
        return null;
    }

    /**
     * unbind service at base
     * @param token
     */
    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper mContextWrapper = token.mWrappedContext;
        final ServiceBinder mBinder = mConnectionMap.remove(mContextWrapper);
        if (mBinder == null) {
            return;
        }
        mContextWrapper.unbindService(mBinder);
        if (mConnectionMap.isEmpty()) {
            mService = null;
        }
    }

    public static final boolean isPlaybackServiceConnected() {
        return mService != null;
    }

    public static void next() {
        try {
            if (mService != null) {
                mService.next();
            }
        } catch (final RemoteException ignored) {
        }
    }

    public static void initPlaybackServiceWithSettings(final Context context) {
        setShowAlbumArtOnLockscreen(true);
    }

    public static void setShowAlbumArtOnLockscreen(final boolean enabled) {
        try {
            if (mService != null) {
                mService.setLockscreenAlbumArt(enabled);
            }
        } catch (final RemoteException ignored) {
        }
    }



    public static void previous(final Context context, final boolean force) {
        final Intent previous = new Intent(context, MediaService.class);
        if (force) {
            previous.setAction(MediaService.PREVIOUS_FORCE_ACTION);
        } else {
            previous.setAction(MediaService.PREVIOUS_ACTION);
        }
        context.startService(previous);
    }

    public static void playOrPause() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.play();
                }
            }
        } catch (final Exception ignored) {
        }
    }
    public static void pause(){
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                }
            }
        } catch (final Exception ignored) {
        }

    }



    public static boolean isTrackLocal() {
        try {
            if (mService != null) {
                return mService.isTrackLocal();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cycleRepeat() {
        try {
            if (mService != null) {
                if (mService.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
                    mService.setShuffleMode(MediaService.SHUFFLE_NONE);
                    mService.setRepeatMode(MediaService.REPEAT_CURRENT);
                    return;
                } else {

                    switch (mService.getRepeatMode()) {
                        case MediaService.REPEAT_CURRENT:
                            mService.setRepeatMode(MediaService.REPEAT_ALL);
                            break;
                        case MediaService.REPEAT_ALL:
                            mService.setShuffleMode(MediaService.SHUFFLE_NORMAL);
//                        if (mService.getShuffleMode() != MediaService.SHUFFLE_NONE) {
//                            mService.setShuffleMode(MediaService.SHUFFLE_NONE);
//                        }
                            break;

                    }
                }

            }
        } catch (final RemoteException ignored) {
        }
    }



    public static final boolean isPlaying() {
        if (mService != null) {
            try {
                return mService.isPlaying();
            } catch (final RemoteException ignored) {
            }
        }
        return false;
    }

    public static final int getShuffleMode() {
        if (mService != null) {
            try {
                return mService.getShuffleMode();
            } catch (final RemoteException ignored) {
            }
        }
        return 0;
    }


    public static final int getRepeatMode() {
        if (mService != null) {
            try {
                return mService.getRepeatMode();
            } catch (final RemoteException ignored) {
            }
        }
        return 0;
    }

    public static final String getTrackName() {
        if (mService != null) {
            try {
                return mService.getTrackName();
            } catch (final RemoteException ignored) {
            }
        }
        return null;
    }

    public static final long getCurrentAudioId() {
        if (mService != null) {
            try {
                return mService.getAudioId();
            } catch (final RemoteException ignored) {
            }
        }
        return -1;
    }


    public static final long[] getQueue() {
        try {
            if (mService != null) {
                return mService.getQueue();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return sEmptyList;
    }

    public static final HashMap<Long, MusicInfo> getPlayinfos() {
        try {
            if (mService != null) {

                return (HashMap<Long, MusicInfo>) mService.getPlayinfos();
            } else {

            }
        } catch (final RemoteException ignored) {
        }
        return null;
    }


    public static final int getQueueSize() {
        try {
            if (mService != null) {
                return mService.getQueueSize();
            } else {
            }
        } catch (final RemoteException ignored) {
        }
        return 0;
    }

    public static final int getQueuePosition() {
        try {
            if (mService != null) {
                return mService.getQueuePosition();
            }
        } catch (final RemoteException ignored) {
        }
        return 0;
    }

    public static void setQueuePosition(final int position) {
        if (mService != null) {
            try {
                mService.setQueuePosition(position);
            } catch (final RemoteException ignored) {
            }
        }
    }





    public static final int removeTrack(final long id) {
        try {
            if (mService != null) {
                return mService.removeTrack(id);
            }
        } catch (final RemoteException ingored) {
        }
        return 0;
    }

    /**
     *
     * @param infos  hashMap
     * @param list   songIds
     * @param position   potision
     * @param forceShuffle
     */
    public static synchronized void playAll(final HashMap<Long, MusicInfo> infos, final long[] list, int position, final boolean forceShuffle) {
        if (list == null || list.length == 0 || mService == null) {
            return;
        }
        try {
            //如果强制的话
            if (forceShuffle) {
                mService.setShuffleMode(MediaService.SHUFFLE_NORMAL);
            }
            //to Get Current Music Id
            final long currentId = mService.getAudioId();
            //selected music id
            long playId = list[position];
            L.e("1", currentId + "playAll"+"position="+position);
            //get current music queue;
            final int currentQueuePosition = getQueuePosition();
            L.e("2", currentQueuePosition + "playAll");
            if (position != -1) {
                //get play music list
                L.e("3", "currentQueuePosition + position != -1");
                final long[] playlist = getQueue();
                //is the same  music list
                if (Arrays.equals(list, playlist)) {
                    // is the music which is playing
                    if (currentQueuePosition == position && currentId == list[position]) {
                        L.e("SixItem", "currentQueuePosition + playAll"+position);
                        mService.play();
                        return;
                    } else {
                        //change music
                        mService.setQueuePosition(position);
                        L.e("FFFFFFF","setQueuePosition"+position);
                        return;
                    }

                }
            }
            if (position < 0) {
                position = 0;
            }

            mService.open(infos, list, forceShuffle ? -1 : position);
            mService.play();
           // L.e("SixItem", System.currentTimeMillis() + "playAll");
        } catch (final RemoteException ignored) {
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static String getPath() {
        if (mService == null) {
            return null;
        }
        try {
            return mService.getPath();

        } catch (Exception e) {

        }
        return null;
    }

    public static void stop() {
        try {
            mService.stop();
        } catch (Exception e) {

        }
    }



    public static void seek(final long position) {
        if (mService != null) {
            try {
                mService.seek(position);
            } catch (final RemoteException ignored) {
            }
        }
    }


    public static final long position() {
        if (mService != null) {
            try {
                return mService.position();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ex) {

            }
        }
        return 0;
    }

    public static final int secondPosition() {
        if (mService != null) {
            try {
                return mService.secondPosition();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ex) {

            }
        }
        return 0;
    }

    public static final long duration() {
        if (mService != null) {
            try {
                return mService.duration();
            } catch (final RemoteException ignored) {
            } catch (final IllegalStateException ignored) {

            }
        }
        return 0;
    }

    public static void timing(int time) {
        if (mService == null) {
            return;
        }
        try {
            mService.timing(time);
        } catch (Exception e) {

        }
    }

    public static final class ServiceBinder implements ServiceConnection {
        private final ServiceConnection mCallback;
        private final Context mContext;


        public ServiceBinder(final ServiceConnection callback, final Context context) {
            mCallback = callback;
            mContext = context;
        }


        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            mService = MediaAidlInterface.Stub.asInterface(service);
            if (mCallback != null) {
                //绑定服务后回调注册的接口BaseActivity
                mCallback.onServiceConnected(className, service);
            }
            initPlaybackServiceWithSettings(mContext);
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            mService = null;
        }
    }

    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;

        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }


}
