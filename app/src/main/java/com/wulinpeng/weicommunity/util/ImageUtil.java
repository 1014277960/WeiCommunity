package com.wulinpeng.weicommunity.util;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.wulinpeng.weicommunity.post.entity.ImageFolder;
import com.wulinpeng.weicommunity.post.entity.ImageInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public interface OnScanListener {
        public void onFinishOrUpdate(List<ImageFolder> folders);
    }

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

    public static void scanImages(final Context context, LoaderManager loaderManager, final OnScanListener listener) {
        loaderManager.initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] {"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                // 数据加载完毕或者数据更新
                if (data == null) {
                    return;
                }
                List<ImageFolder> list = new ArrayList<>();
                Map<String, List<ImageInfo>> folders = new HashMap<String, List<ImageInfo>>();

                // 整理出不同目录和对应的所有图片
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(path);
                    String parentName = file.getParentFile().getName();
                    if (!folders.containsKey(parentName)) {
                        folders.put(parentName, new ArrayList<ImageInfo>());
                    }
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setPath(path);
                    folders.get(parentName).add(imageInfo);
                }

                // 根据hashMap转换为List<ImageFolder>
                for (String folderName : folders.keySet()) {
                    List<ImageInfo> files = folders.get(folderName);
                    ImageFolder folder = new ImageFolder();
                    folder.setImages(files);
                    folder.setName(folderName);
                    list.add(folder);
                }

                // 添加总目录 "相机胶卷"
                ImageFolder mainFolder = createMainFolder(list);
                list.add(0, mainFolder);

                listener.onFinishOrUpdate(list);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }

    private static ImageFolder createMainFolder(List<ImageFolder> allFolder) {
        ImageFolder folder = new ImageFolder();
        folder.setName("相机胶卷");
        List<ImageInfo> imageInfos = new ArrayList<>();
        for (ImageFolder f : allFolder) {
            for (ImageInfo info : f.getImages()) {
                imageInfos.add(info);
            }
        }
        folder.setImages(imageInfos);
        return folder;
    }
}
