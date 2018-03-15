package com.findtech.threePomelos.music.model;

import android.content.Context;
import android.os.AsyncTask;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.musicserver.control.RecentStore;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/17
 *   desc     :
 *   version  :   V 1.0.5
 */
public class RencentModelImp implements CollectModel {

    private PresentIn presentIn;
    private Context mContext;
    private ArrayList<MusicInfo> adapterList = new ArrayList<>();

    public RencentModelImp(PresentIn presentIn, Context mContext) {
        this.presentIn = presentIn;
        this.mContext = mContext;
    }

    @Override
    public void toGetDAta() {

       LoadLocalPlaylistInfo loadLocalPlaylistInfo = new LoadLocalPlaylistInfo();
        loadLocalPlaylistInfo.execute();
//      TopTracksLoader recentloader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
//        ArrayList<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
//
//        L.e("QQQ================",recentsongs.size()+"=recentsongs");
//        if (recentsongs.size() > 0)
//            presentIn.setData(recentsongs);
//        else
//            presentIn.onError();

    }


    public class LoadLocalPlaylistInfo extends AsyncTask<Void, Void, ArrayList<MusicInfo>> {
        @Override
        protected ArrayList<MusicInfo> doInBackground(Void... params) {
            adapterList = RecentStore.getInstance(mContext).getMusicInfos();
            return adapterList;
        }
        @Override
        protected void onPostExecute(ArrayList<MusicInfo> musicInfos) {
            super.onPostExecute(musicInfos);
            if (musicInfos != null && musicInfos.size() > 0)
                presentIn.setData(musicInfos);
            else
                presentIn.onError();
        }
    }
}
