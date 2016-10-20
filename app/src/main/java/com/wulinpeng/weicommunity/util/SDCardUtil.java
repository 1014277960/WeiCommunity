package com.wulinpeng.weicommunity.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.wulinpeng.weicommunity.Constans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午10:36
 * @description:
 */
public class SDCardUtil {

    /**
     * 保证WeiCommunity目录在二级目录创建前创建,以免出现not such file错误
     * @return
     */

    static {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/WeiCommunity";
            File cacheDir = new File(cachePath);
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
        }
    }

    public static boolean isSDCardEnabled() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    public static void putString(Context context, String fileDir, String fileName, String content) {
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
        try {
            file.createNewFile();
            if (isSDCardEnabled()) {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(content.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(Context context, String fileDir, String fileName) {
        File file = new File(fileDir, fileName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void putObject(Context context, String fileDir, String fileName, Object object) {
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
        try {
            if (file.exists()) {
                file.createNewFile();
            }
            if (isSDCardEnabled()) {
                FileOutputStream stream = new FileOutputStream(file);
                ObjectOutputStream oStream = new ObjectOutputStream(stream);
                oStream.writeObject(object);
                stream.close();
                oStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getObject(Context context, String fileDir, String fileName) {
        File file = new File(fileDir, fileName);
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            return stream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
