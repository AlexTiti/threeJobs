package com.findtech.threePomelos.music.view;

import com.findtech.threePomelos.music.info.MusicInfo;

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
public interface MusicViewIn<T> {

    void  successful(ArrayList<T> musicInfos);
    void  onError();


}
