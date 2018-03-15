package com.findtech.threePomelos.music.activity;

import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;

import com.findtech.threePomelos.music.adapter.RecenetMusicAdapter;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.present.RecentMusicPresent;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.view.MusicViewIn;
import com.findtech.threePomelos.musicserver.control.MusicPlayer;
import com.findtech.threePomelos.utils.IContent;


import java.util.ArrayList;
import java.util.HashMap;

public class RencentMusicActivity extends MyActionBarActivity implements ItemClickListtener, MusicViewIn<MusicInfo> {

    private RecyclerView recyclerView;
    private RecenetMusicAdapter showMusicAdapter;
    private RecentMusicPresent recnetMusicPresent;
    private ArrayList<MusicInfo> songs = new ArrayList<>();
    PlayMusic playMusic;
    Handler handler;
    private LinearLayout nodata_layout;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music);
        setToolbar(getResources().getString(R.string.recent_play), true, null);
        registerMusicBroadcast();
        recyclerView = (RecyclerView) findViewById(R.id.show_music);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        showMusicAdapter = new RecenetMusicAdapter();
        showMusicAdapter.setItemCliclListener(this);
        recyclerView.setAdapter(showMusicAdapter);
        recnetMusicPresent = new RecentMusicPresent(this, this);
        recnetMusicPresent.getData();
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
    public void successful(ArrayList<MusicInfo> musicInfos) {
        songs = musicInfos;
        showMusicAdapter.setMusicInfos(musicInfos);
        showMusicAdapter.notifyDataSetChanged();

    }

    @Override
    public void onError() {
        nodata_layout.setVisibility(View.VISIBLE);
        ImageView image = (ImageView) findViewById(R.id.net_fail_image);
        image.setImageResource(R.drawable.recent_play);
        TextView textView = (TextView)findViewById(R.id.net_fail_text);
        textView.setText(getResources().getString(R.string.network_recentplay));
    }




    class PlayMusic implements Runnable {

        int position;

        public PlayMusic(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            long[] list = new long[songs.size()];
            HashMap<Long, MusicInfo> infos = new HashMap();
            for (int i = 0; i < songs.size(); i++) {

                MusicInfo info = songs.get(i);
                list[i] = info.songId;
                infos.put(list[i], info);
            }
            if (position > -1)
                MusicPlayer.playAll(infos, list, position, false);
        }
    }

    @Override
    public void updateTrack() {
        super.updateTrack();
        showMusicAdapter.notifyDataSetChanged();
    }

}
