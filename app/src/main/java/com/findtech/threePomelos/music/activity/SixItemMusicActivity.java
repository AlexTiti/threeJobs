package com.findtech.threePomelos.music.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.music.adapter.MusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.musicserver.control.MusicPlayer;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.BitmapUtil;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.view.BounceScrollView;
import com.findtech.threePomelos.view.MyListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SixItemMusicActivity extends BaseActivity implements ItemClickListtener, BounceScrollView.ScrollingListenering, BounceScrollView.LoadListenering, MusicAdapter.downClick {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    MyListView listView;
    private ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    private MusicAdapter musicAdapter;
    Handler handler;
    PlayMusic playMusic;
    private String title;
    private int number;
    private TextView title_below;
    private RelativeLayout back_relative;
    private ImageView image_pic;
    private ImageView image_bac;
    long i = 1;
    private int preTop;
    private int preLeft;
    private int preBottom;
    private int preTop_text;
    private TextView textView_mode;
    private boolean isFirst = true;
    private BounceScrollView mScrollView;
    private TextView text_titlt_top;
    private int left;
    private int bot;
    private int right;
    View view;
    private int times = 0;
    private MyApplication app;
    boolean isTop = false;
    private ImageView image_back_six;
    private int position;
    int down_position;
    private String content;
    private IContent icontent = IContent.getInstacne();

    private String[] proj_music = new String[]{
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_story);
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(IContent.TITLE);
            number = intent.getIntExtra(IContent.NUMBER, 1);
            content = intent.getStringExtra(IContent.CONTENT);
        }
        app = (MyApplication) getApplication();
        mScrollView = (BounceScrollView) findViewById(R.id.id_scrollView);
        mScrollView.setScrLis(this);
        mScrollView.setLoadListenering(this);
        View view1 = findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        bot = ScreenUtils.getStatusBarHeight(this);
        layoutParams.height = bot;
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);
        back_relative = (RelativeLayout) findViewById(R.id.back_relative);
        text_titlt_top = (TextView) findViewById(R.id.text_titlt_top);
        text_titlt_top.setPadding(0, ScreenUtils.getStatusBarHeight(this), 0, 0);
        title_below = (TextView) findViewById(R.id.title_below);
        title_below.setText(content);
        image_bac = (ImageView) findViewById(R.id.image_bac);
        image_pic = (ImageView) findViewById(R.id.image_pic);
        image_pic.setImageResource(IContent.MUSIC_SEC_IMAGE[number]);
        textView_mode = (TextView) findViewById(R.id.textView_mode);
        image_back_six = (ImageView) findViewById(R.id.image_back_six);
        image_back_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_titlt_top.setText(title);
        back_relative.setBackgroundResource(IContent.MUSIC_BACKCOLOR[number]);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IContent.MUSIC_SEC_IMAGE_O[number]);
        image_bac.setImageBitmap(BitmapUtil.blurBitmap(bitmap, this));

        listView = (MyListView) findViewById(R.id.music_recycle);
        musicAdapter = new MusicAdapter();
        listView.setAdapter(musicAdapter);
        Nammu.init(this);
        musicAdapter.setItemCliclListener(this, this);
        refreshData();
        handler = HandlerUtil.getInstance(this);
        registerMusicBroadcast();
    }

    private void refreshData() {
        showProgressDialog(getResources().getString(R.string.getMessage_fromNet), getString(R.string.getMessage_fromNet_fail));
        listView.removeHeaderView(view);
        AVQuery<AVObject> query = new AVQuery<>("MusicList");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600);
        query.whereEqualTo("typeNumber", number + 1);
        query.skip(times * 15);
        query.limit(15);
        query.orderByAscending("musicOrder");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                dismissProgressDialog();
                if (e == null) {
                    for (AVObject avObject : list) {

                        String musicName = avObject.getString("musicName");
                        if (icontent.map.containsKey(musicName)) {
                            AVFile imageFile = avObject.getAVFile("musicImage");
                            icontent.map.get(musicName).faceImage = imageFile.getUrl();
                            musicInfos.add(icontent.map.get(musicName));
                        } else {
                            MusicInfo musicInfo = new MusicInfo();
                            musicInfo.musicName = musicName;
                            musicInfo.artist = null;
                            musicInfo.type = avObject.getNumber("typeNumber");
                            AVFile avFile = avObject.getAVFile("musicFiles");
                            musicInfo.lrc = avFile.getUrl();
                            AVFile imageFile = avObject.getAVFile("musicImage");
                            musicInfo.faceImage = imageFile.getUrl();
                            musicInfo.avObject = avObject.toString();
                            musicInfo.islocal = false;
                            musicInfo.songId = number * 1000 + i;
                            musicInfos.add(musicInfo);
                        }
                        i++;
                    }
                    musicAdapter.setAvObjectList(musicInfos);
                    musicAdapter.notifyDataSetChanged();
                    times++;

                } else {
                    if (!NetUtils.isConnectInternet(SixItemMusicActivity.this)) {
                        view = LayoutInflater.from(SixItemMusicActivity.this).inflate(R.layout.net_fail_layout, null);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refreshData();
                            }
                        });

                        listView.addHeaderView(view);
                    }
                }
            }
        });
    }

    @Override
    public void click(int position) {
        this.position = position;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Nammu.requestPermission(this,Nammu.PERMISSIONS_STORAGE,1,Manifest.permission.WRITE_EXTERNAL_STORAGE,getResources().getString(R.string.primess_notice));
        }else {
            goMusic();
        }

        if (IContent.getInstacne().SD_Mode) {
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BLUEMODE);
            }
        }

    }

    @MainThread
    public void goMusic() {
        if (playMusic != null) {
            handler.removeCallbacks(playMusic);
        }
        if (position > -1) {
            playMusic = new PlayMusic(position);
            handler.postDelayed(playMusic, 70);
        }
        startFloat();
    }


    @Override
    public void loadData() {
        refreshData();
    }

    @Override
    public void scroll(int dt, int t, int top) {
        if (isFirst) {
            preTop = text_titlt_top.getTop() - textView_mode.getTop();
            preLeft = text_titlt_top.getLeft() - textView_mode.getLeft();
            preBottom = text_titlt_top.getBottom();
            preTop_text = text_titlt_top.getTop();
            right = text_titlt_top.getRight();
            left = text_titlt_top.getLeft();
            isFirst = false;
        }
        if (t > preBottom - 150 && t < preBottom - 50) {
            if (!isTop && dt > 0) {
                Animation animation = new TranslateAnimation(0, -preLeft, 0, -preTop);
                animation.setDuration(100);
                animation.setFillAfter(true);
                text_titlt_top.startAnimation(animation);
                isTop = true;
            }
            if (isTop && dt < 0) {
                Animation animation = new TranslateAnimation(0, 1, 0, 1);
                animation.setDuration(100);
                animation.setFillAfter(true);
                text_titlt_top.startAnimation(animation);
                text_titlt_top.layout(left, preTop_text, right, preBottom);
                isTop = false;
            }
        }
    }


    @Override
    public void downclick(int position) {
        down_position = position;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Nammu.requestPermission(this,Nammu.PERMISSIONS_STORAGE,100,Manifest.permission.WRITE_EXTERNAL_STORAGE,getResources().getString(R.string.primess_notice));
        }else {
            downMusic(musicInfos.get(position));
        }


    }

    class PlayMusic implements Runnable {
        int position;

        public PlayMusic(int position) {
            this.position = position;
        }

        @Override
        public void run() {

            long[] list = new long[musicInfos.size()];
            HashMap<Long, MusicInfo> infos = new HashMap();
            for (int i = 0; i < musicInfos.size(); i++) {
                MusicInfo info = musicInfos.get(i);
                list[i] = info.songId;
                infos.put(list[i], musicInfos.get(i));
            }
            if (position > -1) {
                MusicPlayer.playAll(infos, list, position, false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void updateTrack() {
        super.updateTrack();
        musicAdapter.notifyDataSetChanged();
    }

    private void downMusic(final MusicInfo info) {


        musicInfos.get(down_position).artist = "downing";
        musicAdapter.setAvObjectList(musicInfos);
        musicAdapter.notifyDataSetChanged();


        final NetWorkRequest netWorkRequest = new NetWorkRequest(this);
        NetWorkRequest.downMusicFromNet(this, info, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                if (integer == 100) {
                    File file = DownFileUtils.creatFile(SixItemMusicActivity.this, IContent.FILEM_USIC, info.musicName + ".mp3");
                    if (!file.exists()) {
                        return;
                    }
                    netWorkRequest.sendMusicDown(info.musicName, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                            } else {
                                checkNetWork();
                            }
                        }
                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(file); //指定SD卡路径
                        mediaScanIntent.setData(contentUri);
                        SixItemMusicActivity.this.sendBroadcast(mediaScanIntent);
                    } else {
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"
                                        + Environment.getExternalStorageDirectory())));
                    }
                    CountDownTimer timer = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            File fileca = DownFileUtils.creatFileDir(SixItemMusicActivity.this, IContent.FILEM_USIC);
                            if (!fileca.exists()) {
                                return;
                            }
                            MusicInfo info = musicInfos.get(down_position);
                            ContentResolver cr = SixItemMusicActivity.this.getContentResolver();
                            Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj_music,
                                    MediaStore.Audio.Media.TITLE + "=?", new String[]{info.musicName},
                                    null);
                            L.e("===========", info.musicName + icontent.map.size());
                            ArrayList<MusicInfo> infos = MusicUtils.getMusicListCursor(cursor);
                            if (infos.size() > 0) {
                                for (MusicInfo info1 : infos) {
                                    icontent.map.put(info1.musicName, info1);
                                }
                                musicInfos.set(down_position, icontent.map.get(info.musicName));
                            }
                            musicAdapter.setAvObjectList(musicInfos);
                            musicAdapter.notifyDataSetChanged();

                        }
                    };
                    timer.start();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < musicInfos.size(); i++) {
            MusicInfo info = musicInfos.get(i);
            if (icontent.map.containsKey(info.musicName)) {
                musicInfos.set(i, icontent.map.get(info.musicName));
            }
        }
        musicAdapter.setAvObjectList(musicInfos);
        musicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.e("onActivityResult");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goMusic();
                }
                break;
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downMusic(musicInfos.get(down_position));
                }
                break;
            default:
                break;
        }
    }
}
