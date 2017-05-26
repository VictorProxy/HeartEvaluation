package com.vgtech.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.vgtech.common.FileCacheUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {


    public static String saveBitmap(Context context, Bitmap bm, String picName) {
        Log.e("", "保存图片");
        try {
            File f = new File(FileCacheUtils.getPublishImageDir(context), picName + ".ing");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
            return f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String saveBitmap(Context context, Bitmap bm, String picName,String type) {
        Log.e("", "保存图片");
        try {
            File f = new File(FileCacheUtils.getPublishImageDir(context), picName + "."+type);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
            return f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String saveXmppBitmap(Context context, Bitmap bm, String picName) {
        Log.e("", "保存图片");
        try {
            File f = new File(FileCacheUtils.getXmppImageDir(context), picName + ".ing");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
            return f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
