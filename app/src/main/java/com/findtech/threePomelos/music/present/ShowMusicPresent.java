package com.findtech.threePomelos.music.present;

import android.content.Context;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.CollecModelImp;
import com.findtech.threePomelos.music.model.CollectModel;
import com.findtech.threePomelos.music.model.PresentIn;
import com.findtech.threePomelos.music.view.MusicViewIn;

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
public class ShowMusicPresent implements PresentIn<MusicInfo>{

    private CollectModel collectModel;
    private MusicViewIn musicViewIn;
    public ShowMusicPresent(MusicViewIn musicViewIn,Context context) {
        this.musicViewIn = musicViewIn;
        collectModel = new CollecModelImp(this,context);

    }

    public void getMusic(){
        collectModel.toGetDAta();
    }




    @Override
    public void setData(ArrayList<MusicInfo> arrayList) {
        musicViewIn.successful(arrayList);
    }

    @Override
    public void onError() {
        musicViewIn.onError();
    }
}
