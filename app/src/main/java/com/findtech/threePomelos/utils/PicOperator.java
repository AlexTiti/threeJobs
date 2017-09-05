package com.findtech.threePomelos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.base.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gang-chen on 1/17/16.
 */
public class PicOperator {
    private Activity activity;
    public int screenWidth;// 手机屏幕的宽（像素）
    private int screenHeight;// 手机屏幕的高（像素）
    private static final String FileFolderName = "三爸育儿";
    public static String SOURCE_IMAGE_FILE = FileUtils.gainSDCardPath() + File.separator + FileFolderName + File.separator + "temp.jpg";
    public static String OUTPUT_IMAGE_FILE = FileUtils.gainSDCardPath() + File.separator + FileFolderName + File.separator + "icon" + File.separator + AVUser.getCurrentUser().getObjectId() + "_head_icon.png";
    public static Uri OUTPUT_IMAGE_URI = null;//裁剪后输出文件URI
    public static Uri SOURCE_IMAGE_URI = null;//图片文件源URI

    public PicOperator(Activity activity) {
        this.activity = activity;
        if (screenWidth == 0) {
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            screenWidth = metric.widthPixels; // 屏幕宽度（像素）
            screenHeight = metric.widthPixels; // 屏幕宽度（像素）
        }
    }

    public static void initFilePath() {
        File srcFile = new File(SOURCE_IMAGE_FILE);
        if (!srcFile.exists()) {
            srcFile.getParentFile().mkdirs();
        }
        File outFile = new File(OUTPUT_IMAGE_FILE);
        if (!outFile.exists()) {
            outFile.getParentFile().mkdirs();
        }
        SOURCE_IMAGE_URI = Uri.parse("file://" + SOURCE_IMAGE_FILE);
        OUTPUT_IMAGE_URI = Uri.parse("file://" + OUTPUT_IMAGE_FILE);
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {

        if (uri == null) return null;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void toCropImageActivity(Activity context, Uri fromFileURI, Uri saveFileURI,
                                           int outputX, int outputY, int requestCode) {
        toCropImageActivity(context, fromFileURI, saveFileURI, outputX, outputY, true, requestCode);
    }

    public static void toCropImageActivity(Activity context, Uri fromFileURI, Uri saveFileURI,
                                           int outputX, int outputY, boolean canScale, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(fromFileURI, "image/*");
        intent.putExtra("crop", "true");
        // X方向上的比例
        intent.putExtra("aspectX", 1);
        // Y方向上的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", canScale);
        // 图片剪裁不足黑边解决
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        // 需要将读取的文件路径和裁剪写入的路径区分，否则会造成文件0byte
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        // true-->返回数据类型可以设置为Bitmap，但是不能传输太大，截大图用URI，小图用Bitmap或者全部使用URI
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 取消人脸识别功能
        intent.putExtra("noFaceDetection", true);
        context.startActivityForResult(intent, requestCode);
    }

    public static Bitmap getIconFromData(Context context) {
        String localIconNormal = MyApplication.getInstance().getHeadIconPath();
        FileInputStream localStream = null;
        try {
            localStream = context.openFileInput(localIconNormal);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(localStream);
    }

    public static void saveToData(Context context, Bitmap bitmap) {
        FileOutputStream localFileOutputStream1 = null;
        try {
            localFileOutputStream1 = context.openFileOutput(MyApplication.getInstance().getHeadIconPath(), 0);
            Bitmap.CompressFormat localCompressFormat = Bitmap.CompressFormat.PNG;
            bitmap.compress(localCompressFormat, 10, localFileOutputStream1);
            try {
                localFileOutputStream1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getIconDataPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + MyApplication.getInstance().getHeadIconPath();
    }

    //Delete icon file in data/data/.../files
    public static void deleteIconInDataFiles(Context context) {
        File file = new File(getIconDataPath(context));
        if (file.exists()) {
            file.delete();
        }
    }

    public static Bitmap bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap decodeBitmapFromFilePath(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outHeight == -1 || options.outWidth == -1) {
            BitmapFactory.decodeFile(path, options);
        }
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqHeight) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d("cdg22", "inSampleSize : " + inSampleSize);
        return inSampleSize;
    }
}
