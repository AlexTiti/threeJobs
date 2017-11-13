package com.findtech.threePomelos.music.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.music.adapter.ShowMusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.MusicComparator;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.music.utils.SideBar;
import com.findtech.threePomelos.music.utils.SortOrder;
import com.findtech.threePomelos.musicserver.MusicPlayer;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MusicLocalActivity extends MyActionBarActivity implements ItemClickListtener ,ShowMusicAdapter.LongClickListener {

    private ShowMusicAdapter musicAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private PreferencesUtility mPreferences;
    private boolean isFirstLoad = true;
    private SideBar sideBar;
    private TextView dialogText;
    private HashMap<String, Integer> positionMap = new HashMap<>();
    private boolean isAZSort = true;
    ArrayList<MusicInfo> songList ;
    PlayMusic playMusic;
    Handler handler;
    public Activity mContext = this;
    private ViewStub viewStub;
    private int position;
    private NetWorkRequest netWorkRequest;
    private void loadView() {
            dialogText = (TextView) findViewById(R.id.dialog_text);
            recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
             viewStub = (ViewStub) findViewById(R.id.viewstub);
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            musicAdapter = new ShowMusicAdapter();
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setHasFixedSize(true);
            musicAdapter.setItemCliclListener(this);
            musicAdapter.setLongClickListener(this);
            sideBar = (SideBar) findViewById(R.id.sidebar);
            sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
                @Override
                public void onTouchingLetterChanged(String s) {
                    dialogText.setText(s);
                    sideBar.setView(dialogText);
                    if (positionMap.get(s) != null) {
                        int i = positionMap.get(s);
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(i + 1, 0);
                    }
                }
            });
            reloadAdapter();
        }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                sideBar.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_framelayout);
        setToolbar(getResources().getString(R.string.local_music),true,null);
        registerMusicBroadcast();
        mPreferences = PreferencesUtility.getInstance(mContext);
        handler = HandlerUtil.getInstance(mContext);
        isFirstLoad = true;
        isAZSort = mPreferences.getSongSortOrder().equals(SortOrder.SongSortOrder.SONG_A_Z);
        file_music = DownFileUtils.creatFileDir(this, IContent.FILEM_USIC);
        netWorkRequest = new NetWorkRequest(this);
        loadView();
    }

    public void reloadAdapter() {
        if (musicAdapter == null) {
            return;
        }
        Task task = new Task();
        task.execute();

    }

    @Override
    public void click(int position) {

        this.position = position;
        if (IContent.getInstacne().SD_Mode) {
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BLUEMODE);
                ToastUtil.showToast(MusicLocalActivity.this, getResources().getString(R.string.change_bluetooth_mode));
                IContent.getInstacne().SD_Mode = false;
            }
        }
        goMusic();

    }

    public void goMusic(){
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
    public void longClick(final int position) {

        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setShowBindInfo(getResources().getString(R.string.delete_notice));
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                  final  MusicInfo info = songList.get(position);
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, info.songId);
                mContext.getContentResolver().delete(uri, null, null);
                final File file = DownFileUtils.creatFile(MusicLocalActivity.this, IContent.FILEM_USIC, info.musicName + ".mp3");
                L.e("=============",info.musicName+"==============="+file.getAbsolutePath());
                if (file.exists()) {
                    file.delete();
                }
                netWorkRequest.sendDeleteDownMusic(info.musicName, new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e==null){
                            Map map = IContent.getInstacne().map;
                            if (map.containsKey(info.musicName)){
                                IContent.getInstacne().map.remove(info.musicName);
                            }
                        }
                    }
                });
                songList.remove(position);

                if (songList == null || songList.size() <= 0){
                    View view = viewStub.inflate();
                    ImageView image = (ImageView) view.findViewById(R.id.net_fail_image);
                    image.setImageResource(R.drawable.down_list);
                    TextView textView = (TextView) view.findViewById(R.id.net_fail_text);
                    textView.setText(getResources().getString(R.string.network_local));
                }
                musicAdapter.setMusicInfos(songList);
                musicAdapter.notifyDataSetChanged();

                if (MusicPlayer.getCurrentAudioId() == info.songId) {
                    if (MusicPlayer.getQueueSize() == 0) {
                        MusicPlayer.stop();
                    } else {
                        MusicPlayer.next();
                    }
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();


    }


    public class Task extends AsyncTask<Void,Void,ArrayList<MusicInfo>>{
        @Override
        protected ArrayList<MusicInfo> doInBackground(final Void... unused) {
            isAZSort = mPreferences.getSongSortOrder().equals(SortOrder.SongSortOrder.SONG_A_Z);
            boolean hasFolder = false;
            File file = DownFileUtils.creatFileDir(mContext,IContent.FILEM_USIC);
            if(!file.exists()){
                hasFolder = file.mkdirs();
            }else {
                hasFolder = true;
            }
            if(hasFolder){
                L.e("======",file.getAbsolutePath());
                songList = MusicUtils.queryMusic(mContext, file.getAbsolutePath(), IConstants.START_FROM_FOLDER);
            }
            if (songList == null) {
                songList = new ArrayList<MusicInfo>();
            }
            if (isAZSort) {
                Collections.sort(songList, new MusicComparator());
                for (int i = 0; i < songList.size(); i++) {
                    if (positionMap.get(songList.get(i).sort) == null) {
                        positionMap.put(songList.get(i).sort, i);
                    }
                }
            }
            return songList;
        }
        @Override
        protected void onPostExecute(ArrayList<MusicInfo> aVoid) {
            if (aVoid == null || aVoid.size() <= 0){
                View view = viewStub.inflate();
                ImageView image = (ImageView) view.findViewById(R.id.net_fail_image);
                image.setImageResource(R.drawable.down_list);
                TextView textView = (TextView) view.findViewById(R.id.net_fail_text);
                textView.setText(getResources().getString(R.string.network_local));
                return;
            }
            viewStub.setVisibility(View.GONE);
            musicAdapter.setMusicInfos(aVoid);
            musicAdapter.notifyDataSetChanged();

            if (isAZSort) {
                recyclerView.addOnScrollListener(scrollListener);
            } else {
                sideBar.setVisibility(View.INVISIBLE);
                recyclerView.removeOnScrollListener(scrollListener);
            }

        }
    }

//    public void toSynchronousdata( ){
//        if (NetUtils.isConnectInternet(this)) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Map<String,MusicInfo> map = IContent.getInstacne().map;
//                    for (final Map.Entry<String,MusicInfo> entrySet  : map.entrySet()){
//                        L.e("entrySet",entrySet.getKey());
//                        netWorkRequest.sendDeleteDownMusic(entrySet.getKey(), new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (e==null){
//                                    IContent.getInstacne().map.remove(entrySet.getKey());
//                                }else {
//                                }
//                            }
//                        });
//
//                    }
//                }
//            }).start();
//
//        }
//    }




    public boolean isInDownList(String name){
        for (int i=0;i<songList.size();i++){
            MusicInfo info = songList.get(i);
            if (info == null || name == null) {
                return true;
            }
            if (name != null && name.equals(info.musicName)) {
                return true;
            }
        }
        return false;
    }



    class PlayMusic implements Runnable{
        int position;
        public PlayMusic(int position){
            this.position = position;
        }

        @Override
        public void run() {
            long[] list = new long[songList.size()];
            HashMap<Long, MusicInfo> infos = new HashMap();
            for (int i = 0; i < songList.size(); i++) {
                MusicInfo info = songList.get(i);
                list[i] = info.songId;
                info.islocal = true;
                infos.put(list[i], songList.get(i));
            }
            if (position > -1) {
                MusicPlayer.playAll(infos, list, position, false);
            }
        }
    }
    @Override
    public void updateTrack() {
        super.updateTrack();
        musicAdapter.notifyDataSetChanged();
    }





}
