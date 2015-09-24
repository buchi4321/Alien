package com.cyanflxy.game.driver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cyanflxy.common.FileUtils;
import com.cyanflxy.game.bean.HeroPositionBean.Direction;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.bean.ImageResourceBean;
import com.cyanflxy.game.record.GameReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class ImageResourceManager {

    private ImageResourceBean imageInfo;
    private Bitmap imageBitmap;

    private Map<String, ImageInfoBean> imageInfoMap;
    private Map<Integer, Bitmap> bitmapCache;

    private int heroMoveStep;

    public ImageResourceManager(String resourceFile) {
        bitmapCache = new WeakHashMap<>();
        imageInfoMap = new HashMap<>();

        init(resourceFile);
    }

    private void init(String resourceFile) {
        InputStream resIS = null;
        InputStream bitmapIS = null;
        try {
            resIS = baseContext.getAssets().open(GameReader.getAssetsFileName(resourceFile));
            String result = FileUtils.getInputStreamString(resIS);
            resIS.close();

            imageInfo = ImageResourceBean.getInstance(result);

            String bitmapFile = GameReader.getAssetsFileName(imageInfo.source);
            bitmapIS = baseContext.getAssets().open(bitmapFile);
            imageBitmap = BitmapFactory.decodeStream(bitmapIS);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resIS != null) {
                try {
                    resIS.close();
                } catch (IOException e) {
                    // ignore
                }
            }

            if (bitmapIS != null) {
                try {
                    bitmapIS.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        initImageInfo();
        initHeroMoveStep();
    }

    private void initImageInfo() {

        for (ImageInfoBean image : imageInfo.image) {
            String name = image.name;
            if ("mage1".equals(name)) {
                continue;
            }

            imageInfoMap.put(image.name, image);
        }
    }

    private void initHeroMoveStep() {
        heroMoveStep = Integer.MAX_VALUE;

        for (Direction d : Direction.values()) {
            ImageInfoBean info = imageInfoMap.get(d.name());
            int len = info.getIdLength();
            if (heroMoveStep > len) {
                heroMoveStep = len;
            }
        }
    }

    public ImageInfoBean getImage(String name) {
        return imageInfoMap.get(name);
    }

    public Bitmap getBitmap(String name) {
        ImageInfoBean info = getImage(name);
        return getBitmap(info.getId());
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

    public int getHeroMoveStep() {
        return heroMoveStep;
    }

    public void destroy() {
        for (Integer k : bitmapCache.keySet()) {
            bitmapCache.get(k).recycle();
        }

        bitmapCache.clear();

        imageInfo = null;
        imageBitmap.recycle();
    }
}
