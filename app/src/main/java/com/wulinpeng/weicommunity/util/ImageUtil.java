package com.wulinpeng.weicommunity.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author wulinpeng
 * @datetime: 16/10/16 下午4:07
 * @description:
 */
public class ImageUtil {

    public static String IMG_TYPE_SMALL  = "thumbnail";
    public static String IMG_TYPE_MIDDLE = "bmiddle";
    public static String IMG_TYPE_LARGE  = "large";

    public static int IMG_TYPE_NORMAL = 0;
    public static int IMG_TYPE_GIF    = 1;
    public static int IMG_TYPE_LONG   = 2;

    /**
     * 根据status里的普通url替换其中的thumbnail获得不同尺寸的url(status提供不同尺寸url了,但是为什么只有一张?只能自己来了)
     * @param url
     * @param type
     * @return
     */
    public static String getScaleImageUrl(String url, String type) {
        return url.replace("thumbnail", type);
    }

    /**
     * 返回图片的类型,有normal、gif、long三种
     * @param context
     * @param url
     * @return
     */
    public static int getImageType(Context context, String url, Drawable drawable) {
        if (url.endsWith(".gif")) {
            return IMG_TYPE_GIF;
        }
        Rect outRect = drawable.getBounds();
        if ((outRect.right - outRect.left) * 3 < (outRect.bottom - outRect.top)) {
            return IMG_TYPE_LONG;
        }
        return IMG_TYPE_NORMAL;
    }

    /**
     * 保存图片到本地,加入系统图库,通知图库刷新
     * @param context
     * @param bitmap
     */
    public static void saveBitmap(Context context, Bitmap bitmap) {

        // 保存图片到本地
        File bitmapCacheDir = new File(SDCardUtil.getSDCardPath() + "WeiCommunity/wei_bitmap");
        if (!bitmapCacheDir.exists()) {
            bitmapCacheDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(bitmapCacheDir, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 加入系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 通知图库扫描指定文件夹刷新数据
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
}
