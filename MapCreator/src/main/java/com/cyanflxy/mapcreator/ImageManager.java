package com.cyanflxy.mapcreator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cyanflxy.mapcreator.bean.ImageInfoBean;
import com.cyanflxy.mapcreator.bean.ResourceBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class ImageManager {

    public static ImageManager getInstance() {
        return ImageManagerHolder.instance;
    }

    private static class ImageManagerHolder {
        private static ImageManager instance = new ImageManager();
    }

    private ResourceBean imageInfo;
    private Bitmap imageBitmap;

    private Map<Integer, Bitmap> bitmapCache;
    private List<ImageInfoBean> allImages;

    private ImageManager() {
        bitmapCache = new WeakHashMap<>();
        allImages = new ArrayList<>();
    }

    public void init(Context c) {
        try {
            InputStream is = c.getAssets().open("resources.file");
            int len = is.available();
            byte[] buffer = new byte[len];
            len = is.read(buffer);
            is.close();

            String result = new String(buffer, 0, len, "utf-8");
            imageInfo = ResourceBean.getInstance(result);

            imageBitmap = BitmapFactory.decodeStream(c.getAssets().open("resource.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        checkImageInfo();
    }

    public void checkImageInfo() {

        Set<String> nameSet = new HashSet<>(imageInfo.image.size());

        for (ImageInfoBean image : imageInfo.image) {
            String name = image.name;
            if ("mage1".equals(name)) {
                continue;
            }

            if (nameSet.contains(name)) {
                throw new RuntimeException("Resource Name duplicate:" + name);
            }

            nameSet.add(name);
            if(!"hero".equals(image.type)){
                allImages.add(image);
            }
        }
    }

    public List<ImageInfoBean> getAllImages() {
        return allImages;
    }

    public Bitmap getBitmap(int id) {
        Bitmap bitmap = bitmapCache.get(id);
        if (bitmap == null) {
            int w = imageInfo.pieceWidth;
            int h = imageInfo.pieceHeight;

            int bitmapInline = imageBitmap.getWidth() / w;

            int row = id / bitmapInline;
            int col = id % bitmapInline;

            bitmap = Bitmap.createBitmap(imageBitmap, col * w, row * h, w, h);
            bitmapCache.put(id, bitmap);
        }
        return bitmap;
    }

    public void destroy() {
        imageInfo = null;
        imageBitmap.recycle();

        for (Integer k : bitmapCache.keySet()) {
            bitmapCache.get(k).recycle();
        }
        bitmapCache.clear();
    }
}
