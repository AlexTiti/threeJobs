package com.findtech.threePomelos.music.model;

import android.content.Context;

import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.recent.Song;
import com.findtech.threePomelos.music.recent.SongLoader;
import com.findtech.threePomelos.music.recent.TopTracksLoader;

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

    public RencentModelImp(PresentIn presentIn, Context mContext) {
        this.presentIn = presentIn;
        this.mContext = mContext;
    }

    @Override
    public void toGetDAta() {
      TopTracksLoader recentloader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
        ArrayList<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());

        L.e("QQQ================",recentsongs.size()+"=recentsongs");
        if (recentsongs.size() > 0)
            presentIn.setData(recentsongs);
        else
            presentIn.onError();

    }
}
