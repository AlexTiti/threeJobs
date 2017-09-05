package com.findtech.threePomelos.music.utils;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/07/06
 *   desc     :
 *   version  :   V 1.0.5
 */
public class DownMusicBean {

    private String musicName;
    private Number musicType;


    public DownMusicBean(String musicName, Number musicType) {
        this.musicName = musicName;
        this.musicType = musicType;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public Number getMusicType() {
        return musicType;
    }

    public void setMusicType(Number musicType) {
        this.musicType = musicType;
    }
}
