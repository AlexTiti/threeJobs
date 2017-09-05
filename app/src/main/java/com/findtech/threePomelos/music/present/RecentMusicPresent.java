package com.findtech.threePomelos.music.present;

import android.content.Context;

import com.findtech.threePomelos.music.model.CollectModel;
import com.findtech.threePomelos.music.model.PresentIn;
import com.findtech.threePomelos.music.model.RencentModelImp;
import com.findtech.threePomelos.music.view.MusicViewIn;
import com.findtech.threePomelos.music.recent.Song;

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
public class RecentMusicPresent implements PresentIn<Song> {

    private CollectModel collectModel;
    private MusicViewIn musicViewIn;

    public RecentMusicPresent(MusicViewIn musicViewIn, Context context) {
        this.musicViewIn = musicViewIn;
        collectModel = new RencentModelImp(this,context);

    }

    public void getData(){
        collectModel.toGetDAta();
    }


    @Override
    public void setData(ArrayList<Song> arrayList) {
        musicViewIn.successful(arrayList);
    }

    @Override
    public void onError() {
        musicViewIn.onError();
    }

}
