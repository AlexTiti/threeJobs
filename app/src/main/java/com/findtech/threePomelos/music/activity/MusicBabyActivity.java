package com.findtech.threePomelos.music.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.music.adapter.ShowMusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.present.ShowMusicPresent;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.view.MusicViewIn;
import com.findtech.threePomelos.musicserver.MusicPlayer;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicBabyActivity extends MyActionBarActivity implements ItemClickListtener,MusicViewIn<MusicInfo> {

    private RecyclerView recyclerView;
    private ShowMusicPresent present;
    ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    private ShowMusicAdapter showMusicAdapter;
    private LinearLayout nodata_layout;
    PlayMusic playMusic;
    Handler handler;
    private int position;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music);
        setToolbar(getResources().getString(R.string.baby_like),true,null);
        registerMusicBroadcast();
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        recyclerView = (RecyclerView) findViewById(R.id.show_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        present = new ShowMusicPresent(this,this);
        //present.getMusic();
        showMusicAdapter = new ShowMusicAdapter();
        showMusicAdapter.setItemCliclListener(this);
        recyclerView.setAdapter(showMusicAdapter);
        handler = HandlerUtil.getInstance(this);
    }

    @Override
    public void click(int position) {
        this.position = position;
        if (IContent.getInstacne().SD_Mode) {
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BLUEMODE);
            }
        }
            goMusic();
    }
    @Override
    public void successful(ArrayList<MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
        showMusicAdapter.setMusicInfos(musicInfos);
        showMusicAdapter.notifyDataSetChanged();
    }
    @Override
    public void onError() {
        nodata_layout.setVisibility(View.VISIBLE);
        ImageView image = (ImageView) findViewById(R.id.net_fail_image);
        image.setImageResource(R.drawable.babay_like);
        TextView textView = (TextView) findViewById(R.id.net_fail_text);
        textView.setText(getResources().getString(R.string.network_babylike));
    }

    @Override
    public void updateTrack() {
        super.updateTrack();
        showMusicAdapter.notifyDataSetChanged();
    }
    public void goMusic(){
        if (playMusic != null)
            handler.removeCallbacks(playMusic);
        if (position > -1) {
            playMusic = new PlayMusic(position);
            handler.postDelayed(playMusic, 70);
        }
        startFloat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        present.getMusic();
    }

    class PlayMusic implements Runnable{
        int position;
        public PlayMusic(int position){
            this.position = position;
        }
        @Override
        public void run() {
            L.e("走没走？");
            long[] list = new long[musicInfos.size()];
            //保存音乐的id和对应的音乐类型文件
            HashMap<Long, MusicInfo> infos = new HashMap();
            for (int i = 0; i < musicInfos.size(); i++) {
                MusicInfo info = musicInfos.get(i);
                //存储MusicInfo的id
                list[i] = info.songId;
                infos.put(list[i], musicInfos.get(i));
            }
            if (position > -1)
                //参数 ： infos hashMAp ,list ： 保存歌曲的id，positon : 播放的曲目，false：强制？？
                MusicPlayer.playAll(infos, list, position, false);
        }
    }
}
