package com.findtech.threePomelos.home.musicbean;

import com.avos.avoscloud.AVFile;

/**
 * Created by Alex on 2017/5/7.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/07
 *     desc    ：
 *     version ： 1.0
 */
public class MusicNetBean  {
    private String title;
    private String objectId;
    private AVFile mFile;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AVFile getmFile() {
        return mFile;
    }

    public void setmFile(AVFile mFile) {
        this.mFile = mFile;
    }
}
