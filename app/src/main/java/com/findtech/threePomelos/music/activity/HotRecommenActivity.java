package com.findtech.threePomelos.music.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.music.adapter.HostMusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.MusicPlayer;
import com.findtech.threePomelos.musicserver.PlaylistsManager;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HotRecommenActivity extends MyActionBarActivity implements ItemClickListtener ,HostMusicAdapter.CollectClick{

    private ListView list_host;
    int i = 0;
    private ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    private HostMusicAdapter adapter;
    PlayMusic playMusic;
    private LinearLayout net_fail_host;
    private PlaylistsManager mPlaylistsManager;
    private int position;
    Handler handler;
    private NetWorkRequest netWorkRequest;
    private IContent iContent = IContent.getInstacne();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_recommen);
        setToolbar(getString(R.string.listen_tegether), true, null);
        registerMusicBroadcast();
        list_host = (ListView) findViewById(R.id.list_host);
        mPlaylistsManager = PlaylistsManager.getInstance(this);
        adapter = new HostMusicAdapter(this,this);
        adapter.setItemCliclListener(this);
        list_host.setAdapter(adapter);
        list_host.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position0, long id) {

                position = position0;
                if (IContent.getInstacne().SD_Mode) {
                    if (app.manager.cubicBLEDevice != null) {
                        app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BLUEMODE);
                        ToastUtil.showToast(HotRecommenActivity.this, getResources().getString(R.string.change_bluetooth_mode));
                        IContent.getInstacne().SD_Mode = false;
                    }
                }
                goMusic();

            }
        });
        net_fail_host = (LinearLayout) findViewById(R.id.net_fail_host);
        handler = HandlerUtil.getInstance(this);
        netWorkRequest = new NetWorkRequest(this);
        refreshData();
    }

    private void refreshData() {
        net_fail_host.setVisibility(View.GONE);
        showProgressDialog(getResources().getString(R.string.getMessage_fromNet),getString(R.string.getMessage_fromNet_fail));
        AVQuery<AVObject> query = new AVQuery<>("MusicHotspot");
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                dismissProgressDialog();
                if (e == null) {
                    for (AVObject avObject : list) {
                        String musicName = avObject.getString("musicName");
                        if (iContent.map.containsKey(musicName)) {
                            AVFile file = avObject.getAVFile("musicImage");
                            iContent.map.get(musicName).faceImage = file.getUrl();
                            musicInfos.add(iContent.map.get(musicName));
                        }else {
                            MusicInfo musicInfo = new MusicInfo();
                            musicInfo.musicName = avObject.getString("musicName");
                            AVFile avFile = avObject.getAVFile("musicFiles");
                            musicInfo.lrc = avFile.getUrl();
                            musicInfo.avObject = avObject.toString();
                            musicInfo.typeName = avObject.getString("typeName");
                            musicInfo.type = avObject.getNumber("typeNumber");
                            musicInfo.islocal = false;
                            musicInfo.songId = 9 * 1000 + i;
                            AVFile file = avObject.getAVFile("musicImage");
                            musicInfo.faceImage = file.getUrl();
                            musicInfos.add(musicInfo);
                        }
                        i++;
                    }
                    adapter.setMusicInfos(musicInfos);
                    adapter.notifyDataSetChanged();
                } else {
                    if (!NetUtils.isConnectInternet(HotRecommenActivity.this)) {
                        net_fail_host.setVisibility(View.VISIBLE);
                        net_fail_host.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refreshData();
                            }
                        });
                    }
                }
            }
        });
    }

    @MainThread
    public void goMusic() {
        if (playMusic != null)
            handler.removeCallbacks(playMusic);
        if (position > -1) {
            playMusic = new PlayMusic(position);
            handler.postDelayed(playMusic, 70);
        }
        startFloat();
    }

    @Override
    public void click(int position) {

    }

    @Override
    public void collect(int position) {
        final MusicInfo info = musicInfos.get(position);

        netWorkRequest.sendMusicCollecting(info.musicName, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    mPlaylistsManager.insertMusic(HotRecommenActivity.this, IConstants.FAV_PLAYLIST, info);
                    IContent.getInstacne().collectedList.add(info.musicName);
                    adapter.notifyDataSetChanged();
                }else{
                    checkNetWork();
                }

            }
        });
    }

    @Override
    public void deleteCollect(int position) {
        final MusicInfo info = musicInfos.get(position);
        netWorkRequest.deleteMusicCollecting(info.musicName, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    mPlaylistsManager.removeItem(HotRecommenActivity.this, IConstants.FAV_PLAYLIST,
                            info.songId);
                    IContent.getInstacne().collectedList.remove(info.musicName);
                    adapter.notifyDataSetChanged();
                }else {
                    checkNetWork();
                }
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
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
                L.e("=============","==========="+infos.size()+"=="+list.length+"=="+position);
                MusicPlayer.playAll(infos, list, position, false);
            }
        }
    }


}
