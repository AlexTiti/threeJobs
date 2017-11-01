package com.findtech.threePomelos.music.utils;

import android.content.Context;
import android.os.Environment;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.ToastUtil;

import java.io.File;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/15
 *   desc     :
 *   version  :   V 1.0.5
 */
public class DownFileUtils {

    /**
     * 创建音乐下载文件
     * @param path
     * @return
     */


    public static File creatFileDir(Context context,String path){

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            File file = new File(Environment.getExternalStorageDirectory(),"threepomelos"+File.separator+path);
            return file;
        }
        else {
            ToastUtil.showToast(context.getApplicationContext(), context.getResources().getString(R.string.nosd));
        }
        return null;

    }

    private static File creatCacheDir(Context context,String path){
        String disFilePath ;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            disFilePath = context.getExternalCacheDir().getPath();
        } else {
            disFilePath = context.getCacheDir().getPath();
        }

        return new File(disFilePath+File.separator+path);
    }

    public static  File creatFile(Context context ,String path,String name){
        File fileDir = creatFileDir(context,path);
        if (fileDir == null){
            return null;
        }
        if (!fileDir.exists()){
            fileDir.mkdirs();
        }
        return new File(fileDir +File.separator+name);
    }


}
