package com.findtech.threePomelos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/08
 *     desc    ：
 *     version ： 1.0
 */
public class BitmapUtil {

    public static Bitmap blurBitmap(Bitmap bitmap , Context context){

        Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allocationIn = Allocation.createFromBitmap(rs,bitmap);
        Allocation allocationOut = Allocation.createFromBitmap(rs,bitmap1);

        blur.setRadius(23.f);

        blur.setInput(allocationIn);
        blur.forEach(allocationOut);
        allocationOut.copyTo(bitmap1);
        bitmap.recycle();
        rs.destroy();
        return bitmap1;
    }



}
