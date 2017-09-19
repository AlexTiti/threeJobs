package com.findtech.threePomelos.music.activity;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.DownMusicBean;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.musicserver.MediaService;
import com.findtech.threePomelos.musicserver.MusicPlayer;
import com.findtech.threePomelos.musicserver.PlaylistsManager;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.PlayerSeekBar;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayDetailActivity extends MyActionBarActivity implements View.OnClickListener , BaseActivity.MusicInterface {
    private WeakReference<ObjectAnimator> animatorWeakReference;
    private TextView mTimePlayed, mDuration;
    private ObjectAnimator animator;
    private RelativeLayout Rela_round_play,music_link_layout;
    private ImageView imageView_control;
    private ImageView imageView_next;
    private ImageView imageView_prev;
    private ImageView play_mode;
    private ImageView image_collection;
    private ImageView image_voice;
    private TextView textView_music;
    private PlayerSeekBar playerSeekBar;
    private Handler mHandler;
    private boolean isCollection = false;
    private PlaylistsManager mPlaylistsManager;
    private ImageView image_download;
    private TextView textView;
    private SeekBar seekBarVoice;
    private AudioManager mAudiomanger;
    private int maxVoice, currentVoice;
    private boolean isVoice = false;
    private ImageView back ,share_playDetail;
    private ImageView img_down;
    private CircleImageView img_round_detail;
    PreferencesUtility utility ;
    Animation animation;
    BluetoothAdapter bleAdapter;
    BluetoothManager manager;
    private   NetWorkRequest netWorkRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_detail);
        setToolBarDiss();
        Rela_round_play = (RelativeLayout) findViewById(R.id.Rela_round_play);
        img_down = (ImageView) findViewById(R.id.img_down);
        animatorWeakReference = new WeakReference(ObjectAnimator.ofFloat(Rela_round_play, "rotation", new float[]{0.0F, 360.0F}));
        animator = animatorWeakReference.get();
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator.setDuration(25000L);
        animator.setInterpolator(new LinearInterpolator());
        mPlaylistsManager = PlaylistsManager.getInstance(this);
        if (app.manager.cubicBLEDevice != null) {
//            app.manager.cubicBLEDevice.registerReceiver();
//            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
        animation = new ScaleAnimation(0.5f,1.3f,0.5f,1.3f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        animation.setInterpolator(new LinearInterpolator());
        init();
        setSeekBarListener();
        setVoiceSeekBar();
        setMusicInterface(this);
        mHandler = HandlerUtil.getInstance(this);
        utility = PreferencesUtility.getInstance(this);

        manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();

        netWorkRequest = new NetWorkRequest(this);



    }
    private void init() {
        View view1 = findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(this);
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);
        back = (ImageView) findViewById(R.id.back);
        imageView_control = (ImageView) findViewById(R.id.image_control);
        imageView_next = (ImageView) findViewById(R.id.image_next);
        imageView_prev = (ImageView) findViewById(R.id.image_prev);
        textView_music = (TextView) findViewById(R.id.music_name);
        playerSeekBar = (PlayerSeekBar) findViewById(R.id.play_seek);
        mTimePlayed = (TextView) findViewById(R.id.time_play);
        mDuration = (TextView) findViewById(R.id.mduration);
        play_mode = (ImageView) findViewById(R.id.play_mode);
        image_collection = (ImageView) findViewById(R.id.image_collection);
        image_download = (ImageView) findViewById(R.id.image_down);
        img_round_detail = (CircleImageView) findViewById(R.id.img_round_detail);
        music_link_layout = (RelativeLayout) findViewById(R.id.music_link_layout);
        music_link_layout.setAnimation(animation);
        animation.start();

        if (!IContent.getInstacne().SD_Mode) {
            if (MusicPlayer.isPlaying()) {
                imageView_control.setImageResource(R.drawable.button_pause);
                IContent.isModePlay = true;
            } else {
                imageView_control.setImageResource(R.drawable.button_play);
                IContent.isModePlay = false;
            }
        }
        imageView_next.setOnClickListener(this);
        imageView_control.setOnClickListener(this);
        play_mode.setOnClickListener(this);
        image_collection.setOnClickListener(this);
        image_download.setOnClickListener(this);
        imageView_prev.setOnClickListener(this);
        music_link_layout.setOnClickListener(this);
        back.setOnClickListener(this);
        if (IContent.getInstacne().SD_Mode) {
            image_download.setAlpha(0.3f);
            image_download.setEnabled(false);
            image_collection.setAlpha(0.3f);
            image_collection.setEnabled(false);
            music_link_layout.setAlpha(0.3f);
            music_link_layout.setEnabled(false);
            textView_music.setText(getString(R.string.car_music));
            if (IContent.isModePlay) {
                if (animator != null && !animator.isRunning()) {
                    animator.start();
                }
                imageView_control.setImageResource(R.drawable.button_pause);
            }else {
                imageView_control.setImageResource(R.drawable.button_play);
            }
            play_mode.setAlpha(0.3f);
            play_mode.setEnabled(false);
        } else {
            music_link_layout.setAlpha(1f);
            music_link_layout.setEnabled(true);
            MusicInfo info = MusicPlayer.getPlayinfos().get(MusicPlayer.getCurrentAudioId());

            if (info.islocal) {
                image_download.setAlpha(0.3f);
                image_collection.setAlpha(1f);
                image_collection.setEnabled(true);
                image_download.setEnabled(false);
                image_download.setImageResource(R.drawable.icon_downloaded);
                img_round_detail.setImageResource(R.drawable.face_music_car_a);
            } else {
                changeUI();
                upImageView(info.songId);
            }
            if (!NetUtils.isConnectInternet(this)) {
                image_download.setAlpha(0.3f);
                image_download.setEnabled(false);
                image_collection.setAlpha(0.3f);
                image_collection.setEnabled(false);
            }
        }
        textView = (TextView) findViewById(R.id.text_progress);
        seekBarVoice = (SeekBar) findViewById(R.id.voice_seekbar);
        image_voice = (ImageView) findViewById(R.id.image_voice);
        image_voice.setOnClickListener(this);
        playerSeekBar.setIndeterminate(false);
        playerSeekBar.setProgress(1);
        playerSeekBar.setMax(1000);
    }

    private void setModeView() {
        if (MusicPlayer.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
            play_mode.setImageResource(R.drawable.icon_random_play);

        } else {
            switch (MusicPlayer.getRepeatMode()) {
                case MediaService.REPEAT_ALL:
                    play_mode.setImageResource(R.drawable.icon_playorder);
                    break;
                case MediaService.REPEAT_CURRENT:
                    play_mode.setImageResource(R.drawable.icon_single_play);
                    break;
            }
        }

    }

    private void changeUI(){
        MusicInfo mode = MusicPlayer.getPlayinfos().get(MusicPlayer.getCurrentAudioId());
        if (mode == null)
            return;

        if (mode.islocal) {
            image_download.setAlpha(0.3f);
            image_download.setEnabled(false);
            image_download.setImageResource(R.drawable.icon_downloaded);
            return;
        }

        for ( int i=0;i<IContent.getInstacne().downList.size();i++){
            DownMusicBean bean = IContent.getInstacne().downList.get(i);
            if (bean.getMusicName().equals(mode.musicName)){
                L.e("=============","============"+mode.musicName);
                image_download.setAlpha(0.3f);
                image_download.setEnabled(false);
                image_download.setImageResource(R.drawable.icon_downloaded);
                return;
            }
        }
        L.e("=============","============"+mode.musicName);
        if ("downed".equals(mode.artist)) {
            image_download.setAlpha(0.3f);
            image_download.setEnabled(false);
            image_download.setImageResource(R.drawable.icon_downloaded);
        }else {
            L.e("=============","============"+mode.musicName);
            image_download.setAlpha(1f);
            image_download.setEnabled(true);
            image_download.setImageResource(R.drawable.icon_down_play);
        }
    }
    private void updatePlaymode() {
        if (MusicPlayer.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
            play_mode.setImageResource(R.drawable.icon_random_play);
        } else {
            switch (MusicPlayer.getRepeatMode()) {
                case MediaService.REPEAT_ALL:
                    play_mode.setImageResource(R.drawable.icon_playorder);
                    break;
                case MediaService.REPEAT_CURRENT:
                    play_mode.setImageResource(R.drawable.icon_single_play);
                    break;
            }
        }

    }

    private void setVoiceSeekBar() {
        mAudiomanger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVoice = mAudiomanger.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVoice = mAudiomanger.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
        seekBarVoice.setMax(maxVoice);
        seekBarVoice.setProgress(currentVoice);

    }

    private void setVoice() {
        if (IContent.getInstacne().SD_Mode) {
            if (!isVoice) {
                seekBarVoice.setMax(32);
                if (app.manager.cubicBLEDevice != null) {
                    byte bytes[] = {0x55, (byte) 0xAA, 0x00, 0x04, 0x04, (byte) 0xF8};
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
                }
            } else {
                seekBarVoice.setVisibility(View.GONE);
                textView_music.setVisibility(View.VISIBLE);
                image_voice.setImageResource(R.drawable.icon_voice);
            }
        } else {
            if (!isVoice) {
                currentVoice = mAudiomanger.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
                seekBarVoice.setMax(maxVoice);
                seekBarVoice.setProgress(currentVoice);
                seekBarVoice.setVisibility(View.VISIBLE);
                textView_music.setVisibility(View.INVISIBLE);
                image_voice.setImageResource(R.drawable.icon_voice_sec);
            } else {
                seekBarVoice.setVisibility(View.GONE);
                textView_music.setVisibility(View.VISIBLE);
                image_voice.setImageResource(R.drawable.icon_voice);
            }
        }
        isVoice = !isVoice;

        seekBarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (IContent.getInstacne().SD_Mode) {
                    if (app.manager.cubicBLEDevice != null) {
                        byte bytes[] = {0x55, (byte) 0xAA, 0x01, 0x04, 0x03, (byte) progress, (byte) (0 - (0x01 + 0x04 + 0x03 + (byte) progress))};
                        app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
                    }

                } else {
                    mAudiomanger.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    currentVoice = mAudiomanger.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
                    seekBarVoice.setProgress(currentVoice);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshVoice();
                    }
                },1000);

            }
        });

    }

    private void refreshVoice(){
        seekBarVoice.setVisibility(View.GONE);
        textView_music.setVisibility(View.VISIBLE);
        image_voice.setImageResource(R.drawable.icon_voice);
    }

    /**
     * to collect music for baby like
     */
    private void collectionForBaby() {
        final MusicInfo info = MusicPlayer.getPlayinfos().get(MusicPlayer.getCurrentAudioId());
        if (isCollection) {
            image_collection.setImageResource(R.drawable.icon_baby_like);
            mPlaylistsManager.removeItem(PlayDetailActivity.this, IConstants.FAV_PLAYLIST,
                    MusicPlayer.getCurrentAudioId());


            netWorkRequest.deleteMusicCollecting(info.musicName, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    for (int i=0;i<IContent.getInstacne().collection_array.size();i++){
                        DownMusicBean bean = IContent.getInstacne().collection_array.get(i);
                        if (info.musicName.equals(bean.getMusicName()) )
                            IContent.getInstacne().collection_array.remove(i);
                    }
                }
            });



        } else {
            L.e(Log_TAG, info.musicName + info.islocal + "---------------------------" + info.lrc + info.data + info.albumData);
//            NetWorkRequest.sendMusicCollect(info, new SaveCallback() {
//                @Override
//                public void done(AVException e) {
//                    if (e == null){
//                        mPlaylistsManager.insertMusic(PlayDetailActivity.this, IConstants.FAV_PLAYLIST, info);
//                        image_collection.setImageResource(R.drawable.icon_baby_like_seclected);
//                        IContent.getInstacne().collection_array.add(new DownMusicBean(info.musicName,info.type));
//                    }else{
//                        L.e("======",e.toString());
//                        checkNetWork();
//                    }
//                }
//            });


            netWorkRequest.sendMusicCollecting(info.musicName, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        mPlaylistsManager.insertMusic(PlayDetailActivity.this, IConstants.FAV_PLAYLIST, info);
                        image_collection.setImageResource(R.drawable.icon_baby_like_seclected);
                        IContent.getInstacne().collection_array.add(new DownMusicBean(info.musicName,info.type));
                    }else{
                        L.e("======",e.toString());
                        checkNetWork();
                    }
                }
            });

        }
        Intent intent = new Intent(IConstants.PLAYLIST_COUNT_CHANGED);
        sendBroadcast(intent);
        isCollection = !isCollection;
    }

    /**
     * to down music
     */
    private void downloadForBaby() {

        final MusicInfo info = MusicPlayer.getPlayinfos().get(MusicPlayer.getCurrentAudioId());
        NetWorkRequest.downMusicFromNet(this, info, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                image_download.setVisibility(View.GONE);
                textView.setText(integer + "%");
                if (integer == 100) {
                    textView.setVisibility(View.GONE);
                    image_download.setAlpha(0.3f);
                    image_download.setEnabled(false);
                    image_download.setImageResource(R.drawable.icon_downloaded);
                    image_download.setVisibility(View.VISIBLE);
                    netWorkRequest.sendMusicDown(info.musicName, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                IContent.getInstacne(). downList.add( new DownMusicBean(info.musicName,info.type) );
                            }else{
                                checkNetWork();
                            }
                        }
                    });

//                    NetWorkRequest.sendMusicDownLoad(info, new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            if (e == null){
//                                IContent.getInstacne(). downList.add( new DownMusicBean(info.musicName,info.type) );
//                            }else{
//                                checkNetWork();
//                            }
//                        }
//                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本

                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File file = DownFileUtils.creatFile(PlayDetailActivity.this, IContent.FILEM_USIC, info.musicName + ".mp3");
                        Uri contentUri = Uri.fromFile(file); //指定SD卡路径
                        mediaScanIntent.setData(contentUri);
                        L.e("=============","===========contentUri");
                        PlayDetailActivity.this.sendBroadcast(mediaScanIntent);
                        L.e("QQQ", "CODES.KITKAT=" + integer);
                    } else {
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"
                                        + Environment.getExternalStorageDirectory())));

                    }


                }

            }
        });
    }

    private void setSeekBarListener() {
        if (playerSeekBar != null)
            playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progress = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    i = (int) (i * MusicPlayer.duration() / 1000);
                    if (b) {
                        MusicPlayer.seek((long) i);
                        mTimePlayed.setText(MusicUtils.makeTimeString(i));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        setModeView();
        if (MusicPlayer.isTrackLocal())
            updateBuffer(100);
        else {
            updateBuffer(MusicPlayer.secondPosition());
        }
        mHandler.postDelayed(mUpdateProgress, 0);

        if (IContent.getInstacne().SD_Mode) {
            playerSeekBar.setEnabled(false);
            playerSeekBar.setProgress(0);
            image_collection.setClickable(false);
            image_download.setEnabled(false);
            play_mode.setEnabled(false);
            mDuration.setText(null);
            playerSeekBar.setVisibility(View.INVISIBLE);
            textView_music.setText(getString(R.string.car_music));
        }else {
            playerSeekBar.setVisibility(View.VISIBLE);
        }

        if (!NetUtils.isConnectInternet(this)) {
            image_download.setAlpha(0.3f);
            image_download.setEnabled(false);
            image_collection.setAlpha(0.3f);
            image_collection.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (app.manager.cubicBLEDevice != null)
//            app.manager.cubicBLEDevice.ungisterReceiver();
        playerSeekBar.removeCallbacks(mUpdateProgress);
        stopAnim();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopAnim();
        playerSeekBar.removeCallbacks(mUpdateProgress);
    }

    private void stopAnim() {
        if (animator != null) {
            animator.end();
            animator = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_control:
                if (!IContent.getInstacne().SD_Mode) {
                    if (MusicPlayer.isPlaying()) {
                        imageView_control.setImageResource(R.drawable.button_pause);
                    } else {
                        imageView_control.setImageResource(R.drawable.button_play);
                    }
                    if (MusicPlayer.getQueueSize() != 0) {
                        MusicPlayer.playOrPause();
                    }
                } else {
                    if (app.manager.cubicBLEDevice != null) {
                        byte bytes[] = {0x55, (byte) 0xAA, 0x00, 0x03, 0x01, (byte) 0xFC};
                        app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
                    }
                }
                break;
            case R.id.image_next:
                if (IContent.getInstacne().SD_Mode) {
                    if (app.manager.cubicBLEDevice != null) {
                            byte bytes[] = {0x55, (byte) 0xAA, 0x00, 0x03, 0x04, (byte) 0xF9};
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = NEXT_MUSIC;
                    mPlayHandler.sendMessage(msg);
                }
                break;
            case R.id.image_prev:
                if (IContent.getInstacne().SD_Mode) {
                    if (app.manager.cubicBLEDevice != null) {
                        byte bytes[] = {0x55, (byte) 0xAA, 0x00, 0x03, 0x05, (byte) 0xF8};
                        app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = PRE_MUSIC;
                    mPlayHandler.sendMessage(msg);
                }
                break;
            case R.id.play_mode:
                MusicPlayer.cycleRepeat();
                updatePlaymode();
                break;
            case R.id.image_collection:
                collectionForBaby();
                break;
            case R.id.image_down:
                downloadForBaby();
                break;
            case R.id.image_voice:
                setVoice();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.music_link_layout:
                  showNotifyDialog();
                break;
        }
    }

        public void showNotifyDialog(){
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.notice));
            if (bleAdapter.isEnabled()) {
                builder.setShowBindInfo(getResources().getString(R.string.music_link_bluetooth_open));
            }else{
                builder.setShowBindInfo(getResources().getString(R.string.music_link_bluetooth));
            }
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.go_link), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(intent,111);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    @Override
    public void musicReciver( Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(RFStarBLEService.ACTION_WRITE_DONE)) {
            if (IContent.getInstacne().WRITEVALUE != null)
                app.manager.cubicBLEDevice.readValue(IContent.SERVERUUID_BLE, IContent.READUUID_BLE, IContent.getInstacne().WRITEVALUE);
        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);


            if (data[3] == (byte) 0x83 && data[4] == 0x01) {
                if (!IContent.getInstacne().SD_Mode) {
                    if (MusicPlayer.getQueueSize() != 0) {
                        L.e("MusicPlayer.getQu","==================="+MusicPlayer.getQueueSize());
                        MusicPlayer.playOrPause();
                    }
                    if (MusicPlayer.isPlaying()) {
                        imageView_control.setImageResource(R.drawable.button_pause);
                        IContent.isModePlay = true;
                    } else {
                        imageView_control.setImageResource(R.drawable.button_play);
                        IContent.isModePlay = false;
                    }

                    if (!IContent.isModePlay) {
                        imageView_control.setImageResource(R.drawable.button_pause);
                        if (animator != null && animator.isRunning()) {
                            animator.cancel();
                            float valueAvatar = (float) animator.getAnimatedValue();
                            animator.setFloatValues(valueAvatar, 360f + valueAvatar);
                        }
                    }
                }else{
                    IContent.isModePlay = !IContent.isModePlay;
                    if (IContent.isModePlay){
                        imageView_control.setImageResource(R.drawable.button_pause);
                        if (animator != null && !animator.isRunning()) {
                            animator.start();
                        }
                    }else {
                        imageView_control.setImageResource(R.drawable.button_play);
                        if (animator != null && animator.isRunning()) {
                            animator.cancel();
                            float valueAvatar = (float) animator.getAnimatedValue();
                            animator.setFloatValues(valueAvatar, 360f + valueAvatar);
                        }
                    }
                }
            }

            if (data[3] == (byte) 0x83 && data[4] == 0x04) {
                if (!IContent.getInstacne().SD_Mode) {
                    Message msg = new Message();
                    msg.what = NEXT_MUSIC;
                    mPlayHandler.sendMessage(msg);
                }else {
                    if (animator != null && !animator.isRunning()) {
                        animator.start();
                    }
                }
            }
            if (data[3] == (byte) 0x83 && data[4] == 0x05) {
                if (!IContent.getInstacne().SD_Mode) {
                    Message msg = new Message();
                    msg.what = PRE_MUSIC;
                    mPlayHandler.sendMessage(msg);
                }else {
                    if (animator != null && !animator.isRunning()) {
                        animator.start();
                    }
                }
            }

        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE_READ)) {
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            L.e("AAAAA==================", Tools.byte2Hex(data) + "==" + data.length);
            if (data[3] == (byte) 0x84 && data[4] == 0x04) {
                seekBarVoice.setProgress(data[5]);
                seekBarVoice.setVisibility(View.VISIBLE);
                textView_music.setVisibility(View.INVISIBLE);
            }
            if (data[3] == (byte) 0x83 && data[4] == 0x01) {

                IContent.isModePlay = !IContent.isModePlay;
                if (IContent.isModePlay){
                imageView_control.setImageResource(R.drawable.button_pause);
                if (animator != null && !animator.isRunning()) {
                    animator.start();
                }
                }else {
                    imageView_control.setImageResource(R.drawable.button_play);
                    if (animator != null && animator.isRunning()) {
                        animator.cancel();
                        float valueAvatar = (float) animator.getAnimatedValue();
                        animator.setFloatValues(valueAvatar, 360f + valueAvatar);
                    }
                }
            }
            if (data[3] == (byte) 0x83 && data[4] == 0x04) {
                if (animator != null && !animator.isRunning()) {
                    animator.start();
                }
            }
            if (data[3] == (byte) 0x83 && data[4] == 0x05) {
                if (animator != null && !animator.isRunning()) {
                    animator.start();
                }
            }
        }
    }


    @Override
    public void updateTrackInfo() {
        super.updateTrackInfo();
        if (MusicPlayer.getQueueSize() == 0) {
            return;
        }
        if (IContent.getInstacne().SD_Mode) {
            mDuration.setText(null);
            mTimePlayed.setText(null);
            playerSeekBar.setVisibility(View.INVISIBLE);
            playerSeekBar.removeCallbacks(mUpdateProgress);
            playerSeekBar.setEnabled(false);
            playerSeekBar.setProgress(0);
            image_collection.setClickable(false);
            image_download.setEnabled(false);
            play_mode.setEnabled(false);
            textView_music.setText(getString(R.string.car_music));
            return;
        }
        textView_music.setText(MusicPlayer.getTrackName());
        playerSeekBar.setVisibility(View.VISIBLE);

        if (MusicPlayer.isPlaying()) {
            playerSeekBar.removeCallbacks(mUpdateProgress);
            playerSeekBar.postDelayed(mUpdateProgress, 200);
            imageView_control.setImageResource(R.drawable.button_pause);
            if (animator != null && !animator.isRunning()) {
                animator.start();
            }
            Intent intent = new Intent(IContent.ACTION_PLAY_OR_PAUSE);
            intent.putExtra("isPlay",true);
            sendBroadcast(intent);
        } else {
            playerSeekBar.removeCallbacks(mUpdateProgress);
            imageView_control.setImageResource(R.drawable.button_play);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
                float valueAvatar = (float) animator.getAnimatedValue();
                animator.setFloatValues(valueAvatar, 360f + valueAvatar);
            }
            Intent intent = new Intent(IContent.ACTION_PLAY_OR_PAUSE);
            intent.putExtra("isPlay",false);
            sendBroadcast(intent);
        }
    }
    @Override
    public void updateBuffer(int p) {
        playerSeekBar.setSecondaryProgress(p * 10);
    }
    @Override
    public void loading(boolean l) {
        playerSeekBar.setLoading(l);
    }
    private Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            if (playerSeekBar != null) {
                long position = MusicPlayer.position();
                long duration = MusicPlayer.duration();
                mDuration.setText(MusicUtils.makeShortTimeString(PlayDetailActivity.this.getApplication(), MusicPlayer.duration() / 1000));
                if (duration > 0 && duration < 627080716) {
                    playerSeekBar.setProgress((int) (1000 * position / duration));
                    mTimePlayed.setText(MusicUtils.makeTimeString(position));
                }
                if (MusicPlayer.isPlaying()) {
                    playerSeekBar.postDelayed(mUpdateProgress, 200);
                } else {
                    playerSeekBar.removeCallbacks(mUpdateProgress);
                }
            }
        }
    };

    @Override
    public void updateTrack() {
        super.updateTrack();
        if (IContent.getInstacne().SD_Mode)
            return;
        isCollection = false;
        long[] favlists = mPlaylistsManager.getPlaylistIds(IConstants.FAV_PLAYLIST);
        long currentid = MusicPlayer.getCurrentAudioId();
        for (long i : favlists) {
            if (currentid == i) {
                isCollection = true;
                break;
            }
        }
        MusicInfo mode = MusicPlayer.getPlayinfos().get(MusicPlayer.getCurrentAudioId());
        if (!isCollection) {
            for (int i = 0; i < IContent.getInstacne().collection_array.size(); i++) {
                if (mode.musicName.equals(IContent.getInstacne().collection_array.get(i).getMusicName() )) {
                    isCollection = true;
                    break;
                }
            }
        }
        changeUI();
        if (!NetUtils.isConnectInternet(this)) {
            image_download.setAlpha(0.3f);
            image_download.setEnabled(false);
            image_collection.setAlpha(0.3f);
            image_collection.setEnabled(false);
        }
        upImageView(mode.songId);
        updateFav(isCollection);
    }
        private void upImageView(long songId ){

            switch ((int) (songId/1000)){
                case 0 :
                    img_round_detail.setImageResource(R.drawable.face_image_rhyme_a);
                    break;
                case 1 :
                    img_round_detail.setImageResource(R.drawable.face_image_poetry_a);
                    break;
                case 2 :
                    img_round_detail.setImageResource(R.drawable.face_child_story_a);
                    break;
                case 3 :
                    img_round_detail.setImageResource(R.drawable.face_english_a);
                    break;
                case 4 :
                    img_round_detail.setImageResource(R.drawable.face_image_three_character_a);
                    break;
                default:
                    img_round_detail.setImageResource(R.drawable.face_music_car_a);
                    break;
            }
    }
    private void updateFav(boolean b) {
        if (b) {
            image_collection.setImageResource(R.drawable.icon_baby_like_seclected);
        } else {
            image_collection.setImageResource(R.drawable.icon_baby_like);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 111){
          if (!bleAdapter.isEnabled())
              ToastUtil.showToast(this,getString(R.string.bluetooth_unopen));

      }
    }
}
