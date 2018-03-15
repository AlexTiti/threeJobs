package com.findtech.threePomelos.music.model;

import android.content.Context;
import android.os.AsyncTask;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.info.Playlist;
import com.findtech.threePomelos.musicserver.info.PlaylistInfo;
import com.findtech.threePomelos.musicserver.control.PlaylistsManager;


import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/15
 *   desc     :
 *   version  :   V 1.0.5
 */
public class CollecModelImp implements CollectModel {

    private ArrayList<MusicInfo> adapterList = new ArrayList<>();
    ArrayList playlists = new ArrayList();
    Playlist playlist;
    private int number;
    long i = 1;
    private ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    private PresentIn presentIn;
    private PlaylistInfo playlistInfo; //playlist 管理类
    private Context context;

    public CollecModelImp(PresentIn presentIn, Context context) {
        this.presentIn = presentIn;
        this.context = context;
        playlistInfo = PlaylistInfo.getInstance(context);
        playlists.addAll(playlistInfo.getPlaylist());

    }

    @Override
    public void toGetDAta() {
        if (playlists.size() >0) {
            playlist = (Playlist) playlists.get(0);
        }

        if (playlist != null) {
            LoadLocalPlaylistInfo loadLocalPlaylistInfo = new LoadLocalPlaylistInfo();
            loadLocalPlaylistInfo.execute();
        }else {
            presentIn.onError();
        }



    }
    public class LoadLocalPlaylistInfo extends AsyncTask<Void, Void, ArrayList<MusicInfo>> {
        @Override
        protected ArrayList<MusicInfo> doInBackground(Void... params) {
            adapterList = PlaylistsManager.getInstance(context).getMusicInfos(playlist.id);
            return adapterList;
        }
        @Override
        protected void onPostExecute(ArrayList<MusicInfo> musicInfos) {
            super.onPostExecute(musicInfos);
            if (musicInfos != null && musicInfos.size() > 0) {
                presentIn.setData(musicInfos);
            } else {
                presentIn.onError();
            }
        }
    }

}
