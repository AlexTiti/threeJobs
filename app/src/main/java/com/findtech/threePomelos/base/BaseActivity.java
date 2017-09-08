package com.findtech.threePomelos.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.MediaAidlInterface;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.GetUserProtocolActivity;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.home.MainHomeActivity;
import com.findtech.threePomelos.home.musicbean.DeviceCarBean;
import com.findtech.threePomelos.music.activity.PlayDetailActivity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.FloatingService;
import com.findtech.threePomelos.musicserver.MediaService;
import com.findtech.threePomelos.musicserver.MusicPlayer;
import com.findtech.threePomelos.musicserver.MusicStateListener;
import com.findtech.threePomelos.musicserver.WatcherHome;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.findtech.threePomelos.musicserver.MusicPlayer.mService;


/**
 * activity基类
 */
public class BaseActivity extends AppCompatActivity implements ServiceConnection {
    protected MyApplication app = null;
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus; //receiver 接受播放状态变化等
    private ArrayList<MusicStateListener> mMusicListener = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static int activityNumber = 0;
    public Intent intent;
    WatcherHome mHomeWatcher;
    FloatViewReceiver floatViewReceiver;
    public CustomDialog.Builder builder;
    private DialogClick dialogClick;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public Handler mPlayHandler;
    public static final int NEXT_MUSIC = 0;
    public static final int PRE_MUSIC = 1;
    private PlayMusic mPlayThread;
    private MusicInterface musicInterface;
    public static String DEVICE_CLOSE_ONPAGE = "android.receive.device.close";

    public void showProgressDialog(String message , final String notice) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if (!TextUtils.isEmpty(notice))
                        ToastUtil.showToast(BaseActivity.this,notice);
                }
            }
        },10000);
    }

    public void showProgressDialog(String message, final long time, final String notice) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if (notice != null)
                    ToastUtil.showToast(BaseActivity.this,notice);
                }
            }
        },time);


    }


    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
        progressDialog.dismiss();
    }

    public void setMusicInterface(MusicInterface musicInterface){
        this.musicInterface = musicInterface;

    }

    public void setClickListening(DialogClick dialogClick){
        this.dialogClick = dialogClick;
    }
    public void showDialogConfirm(String title,String message) {
        builder.setTitle(title);
        builder.setShowBindInfo(message);
        builder.setShowButton(true);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogClick.configClick();
            }
        });
        builder.setNegativeButton(getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogClick.cancleClick();
                    }
                });

        builder.create().show();
    }

    public void checkNetWork(){
        if (!NetUtils.isConnectInternet(this)){
            ToastUtil.showToast(this,getResources().getString(R.string.net_exception));
        }
    }



    /**
     * 更新播放队列
     */
    public void updateQueue() {

    }

    /**
     * 更新歌曲状态信息
     */
    public void updateTrackInfo() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.reloadAdapter();
                listener.updateTrackInfo();
            }
        }

    }

    /**
     * fragment界面刷新
     */
    public void refreshUI() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.reloadAdapter();
            }
        }

    }

    public void updateTime() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.updateTime();
            }
        }
    }

    /**
     * 歌曲切换
     */
    public void updateTrack() {

    }


    public void updateLrc() {

    }

    /**
     * @param p 更新歌曲缓冲进度值，p取值从0~100
     */
    public void updateBuffer(int p) {

    }


    /**
     * @param l 歌曲是否加载中
     */
    public void loading(boolean l) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        mToken = MusicPlayer.bindToService(this, this);
        mPlaybackStatus = new PlaybackStatus(this);
        IntentFilter f = new IntentFilter();
        f.addAction(MediaService.PLAYSTATE_CHANGED);
        f.addAction(MediaService.META_CHANGED);
        f.addAction(MediaService.QUEUE_CHANGED);
        f.addAction(IConstants.MUSIC_COUNT_CHANGED);
        f.addAction(MediaService.TRACK_PREPARED);
        f.addAction(MediaService.BUFFER_UP);
        f.addAction(IConstants.EMPTY_LIST);
        f.addAction(MediaService.MUSIC_CHANGED);
        f.addAction(MediaService.LRC_UPDATED);
        f.addAction(IConstants.PLAYLIST_COUNT_CHANGED);
        f.addAction(MediaService.MUSIC_LODING);
        f.setPriority(1000);
        registerReceiver(mPlaybackStatus, new IntentFilter(f));

        floatViewReceiver = new FloatViewReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IContent.SINGLE_CLICK);
        intentFilter.addAction(IContent.DOUBLE_CLICK);
        registerReceiver(floatViewReceiver, intentFilter);
        intent = new Intent(BaseActivity.this, FloatingService.class);
        mHomeWatcher = new WatcherHome(this);
        mHomeWatcher.setOnHomePressedListener(new WatcherHome.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                stopService(intent);

            }

            @Override
            public void onHomeLongPressed() {
                stopService(intent);

            }
        });
        mPlayThread = new PlayMusic();
        mPlayThread.start();
        builder = new CustomDialog.Builder(this);


    }


    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = MediaAidlInterface.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (activityNumber == 0 && MusicPlayer.isPlaying()) {
            startService(intent);
        }
        activityNumber++;
    }

    public void startFloat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission();
        }else {
//            String packname = getPackageName();
//            PackageManager pm = getPackageManager();
//            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
//            L.e("KKKKKKK","=============="+permission);
//            if ( !permission)
//                ToastUtil.showToast(this,"未开启悬浮窗权限");
            startService(intent);

        }
    }


    public void toGoGetProtectActivity(final Context activity){
        NetWorkRequest netWorkRequest = new NetWorkRequest(this);
        netWorkRequest.getUserProtect(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        AVFile avFile = list.get(1).getAVFile("userProtocol");
                        if (avFile == null) {
                            ToastUtil.showToast(activity, getString(R.string.data_exception));
                            return;
                        }
                        Intent intent1 = new Intent(activity,GetUserProtocolActivity.class);
                        intent1.putExtra("protect_url",avFile.getUrl());
                        startActivity(intent1);
                    }
                }
            }
        });
    }



    @TargetApi(Build.VERSION_CODES.M)
    public void askForPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            startService(intent);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(BaseActivity.this, "授权失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                startService(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
        mHomeWatcher.startWatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
        mHomeWatcher.stopWatch();// 在onPause中停止监听，不然会报错的。
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (activityNumber > 0)
            activityNumber--;
        if (activityNumber == 0) {
            stopService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        unbindService();
        try {
            unregisterReceiver(mPlaybackStatus);
            unregisterReceiver(floatViewReceiver);
        } catch (final Throwable e) {
        }
   //   unregisterReceiver(scanSdReceiver);
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayHandler.getLooper().quit();
        mPlayHandler = null;
        mMusicListener.clear();

    }

    public void unbindService() {
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicListener.remove(status);
        }
    }

    //接收广播执行相应的操作
    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseActivity> mReference;


        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                //
                if (action.equals(MediaService.META_CHANGED)) {
                    baseActivity.updateTrackInfo();

                } else if (action.equals(MediaService.PLAYSTATE_CHANGED)) {

                } else if (action.equals(MediaService.TRACK_PREPARED)) {
                    baseActivity.updateTime();
                } else if (action.equals(MediaService.BUFFER_UP)) {
                    baseActivity.updateBuffer(intent.getIntExtra("progress", 0));
                } else if (action.equals(MediaService.MUSIC_LODING)) {
                    baseActivity.loading(intent.getBooleanExtra("isloading", false));
                } else if (action.equals(MediaService.REFRESH)) {

                } else if (action.equals(IConstants.MUSIC_COUNT_CHANGED)) {
                    baseActivity.refreshUI();
                } else if (action.equals(IConstants.PLAYLIST_COUNT_CHANGED)) {
                    baseActivity.refreshUI();
                } else if (action.equals(MediaService.QUEUE_CHANGED)) {
                    baseActivity.updateQueue();
                } else if (action.equals(MediaService.TRACK_ERROR)) {
                    final String errorMsg = "退出";
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                } else if (action.equals(MediaService.MUSIC_CHANGED)) {
                    baseActivity.updateTrack();
                } else if (action.equals(MediaService.LRC_UPDATED)) {
                    baseActivity.updateLrc();
                }

            }
        }
    }

    private class FloatViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent0) {
            final String action = intent0.getAction();
            if (action.equals(IContent.SINGLE_CLICK)) {
                startActivity(new Intent(BaseActivity.this, PlayDetailActivity.class));
            } else if (action.equals(IContent.DOUBLE_CLICK)) {
                stopService(intent);
            }

        }
    }




        public   interface DialogClick{
        void configClick();
        void cancleClick();
    }

    public class PlayMusic extends Thread {
        public void run() {
            if (Looper.myLooper() == null)
                Looper.prepare();
            mPlayHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case PRE_MUSIC:
                            MusicPlayer.previous(BaseActivity.this, true);
                            break;
                        case NEXT_MUSIC:
                            MusicPlayer.next();
                            break;
                        case 3:
                            MusicPlayer.setQueuePosition(msg.arg1);
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }
    public void registerMusicBroadcast(){

        if (app.manager.cubicBLEDevice != null) {

            app.manager.cubicBLEDevice.registerReceiver();
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(new BLEDevice.RFStarBLEBroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent, String macData, String uuid) {

                    String action = intent.getAction();

                    if (musicInterface != null) {
                        musicInterface.musicReciver(intent);
                        return;
                    }

                    if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {

                        byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);

                        if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {

                            Intent intent_close = new Intent(DEVICE_CLOSE_ONPAGE);
                            sendBroadcast(intent_close);

                            return;
                        }
                        doMusic(data);
                    }
                }
                @Override
                public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {
                }
            });
        }
    }


    public  void doMusic(byte data[]){
        L.e("====================BaseActivity","============="+ Tools.byte2Hex(data));
        if (data[3] == (byte) 0x83 && data[4] == 0x01) {
            if (!IContent.getInstacne().SD_Mode) {

                if (MusicPlayer.getQueueSize() != 0) {
                    L.e("MusicPlayer.getQu","==================="+MusicPlayer.getQueueSize());
                    MusicPlayer.playOrPause();
                }
                if (MusicPlayer.isPlaying()) {
                    IContent.isModePlay = true;
                } else {
                    IContent.isModePlay = false;
                }
            }else{
                IContent.isModePlay = !IContent.isModePlay;
            }
        }
        if (data[3] == (byte) 0x83 && data[4] == 0x04) {
            if (!IContent.getInstacne().SD_Mode) {
                Message msg = new Message();
                msg.what = NEXT_MUSIC;
                mPlayHandler.sendMessage(msg);
            }else {
                IContent.isModePlay = true;
            }
        }
        if (data[3] == (byte) 0x83 && data[4] == 0x05) {
            if (!IContent.getInstacne().SD_Mode) {
                Message msg = new Message();
                msg.what = PRE_MUSIC;
                mPlayHandler.sendMessage(msg);
            }else {
                IContent.isModePlay = true;
            }
        }

    }

  public   interface MusicInterface{
        void musicReciver(Intent intent);
    }




}
