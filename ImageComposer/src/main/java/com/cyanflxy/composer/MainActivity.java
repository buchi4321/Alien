package com.cyanflxy.composer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private Canvas canvas;
    private int left;
    private int top;

    private int width = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawPicture();

    }

    private void drawPicture() {

        Bitmap bitmap = Bitmap.createBitmap(width, 832, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        left = 0;
        top = 0;

        try {
            drawFlow("hero.png");

            drawEnemy();

            drawFlow("npc_1.png");
            drawFlow("npc_2.png");
            drawFlow("npc_3.png");

            drawDoor("door_1.png");
            drawDoor("door_2.png");

            drawBackground();
            nextLine();

            drawItem1();
            nextLine();

            drawFlow("item_2.png");
            nextLine();

            drawItem3();
            nextLine();

            drawItem4();
            nextLine();

            saveBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addLeft(int width) {
        left += width;
        if (left >= this.width) {
            left = 0;
            top += 32;
        }
    }

    private void nextLine() {
        if (left == 0) {
            return;
        }

        left = 0;
        top += 32;
    }


    private void drawEnemy() throws IOException {
        Bitmap enemyBitmap = BitmapFactory.decodeStream(getAssets().open("enemy.png"));
        int height = enemyBitmap.getHeight() - 32 * 4;

        for (int i = 0; i < height; i += 32) {
            Bitmap clip = Bitmap.createBitmap(enemyBitmap, 0, i, 128, 32);
            canvas.drawBitmap(clip, left, top, null);
            addLeft(128);
        }

    }

    private void drawFlow(String file) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(file));
        int height = bitmap.getHeight();

        for (int top = 0; top < height; top += 32) {
            Bitmap clip = Bitmap.createBitmap(bitmap, 0, top, 128, 32);
            canvas.drawBitmap(clip, this.left, this.top, null);
            addLeft(128);
        }

    }

    private void drawDoor(String doorFile) throws IOException {
        Bitmap doorBitmap = BitmapFactory.decodeStream(getAssets().open(doorFile));

        for (int left = 0; left < 128; left += 32) {
            for (int top = 0; top < 128; top += 32) {
                Bitmap clip = Bitmap.createBitmap(doorBitmap, left, top, 32, 32);
                canvas.drawBitmap(clip, this.left, this.top, null);
                addLeft(32);
            }
        }
    }


    private void drawBackground() throws IOException {
        Bitmap lavaBitmap = BitmapFactory.decodeStream(getAssets().open("lava.png"));
        canvas.drawBitmap(lavaBitmap, this.left, this.top, null);
        addLeft(128);

        Bitmap starBitmap = BitmapFactory.decodeStream(getAssets().open("star.png"));
        canvas.drawBitmap(starBitmap, this.left, this.top, null);
        addLeft(128);

        Bitmap allBitmap = BitmapFactory.decodeStream(getAssets().open("all.png"));

        Bitmap wall = Bitmap.createBitmap(allBitmap, 5 * 32, 0, 32 * 3, 32);
        canvas.drawBitmap(wall, left, top, null);
        addLeft(32 * 3);

        Bitmap stairs = Bitmap.createBitmap(allBitmap, 0, 31 * 32, 64, 32);
        canvas.drawBitmap(stairs, left, top, null);
        addLeft(64);


        Bitmap shopLeft = Bitmap.createBitmap(allBitmap, 3 * 32, 31 * 32, 32, 32);
        canvas.drawBitmap(shopLeft, left, top, null);
        addLeft(32);

        Bitmap shopRight = Bitmap.createBitmap(allBitmap, 5 * 32, 31 * 32, 32, 32);
        canvas.drawBitmap(shopRight, left, top, null);
        addLeft(32);
    }

    private void drawItem1() throws IOException {
        Bitmap itemBitmap = BitmapFactory.decodeStream(getAssets().open("item_1.png"));

        for (int top = 0; top < 128; top += 32) {
            for (int left = 0; left < 128; left += 32) {
                if (top / 32 == 3) {
                    if (left == 0 || left == 32) {
                        continue;
                    }
                }

                Bitmap clip = Bitmap.createBitmap(itemBitmap, left, top, 32, 32);
                canvas.drawBitmap(clip, this.left, this.top, null);
                addLeft(32);
            }
        }
    }

    private void drawItem3() throws IOException {
        Bitmap itemBitmap = BitmapFactory.decodeStream(getAssets().open("item_3.png"));

        for (int top = 0; top < 128; top += 32) {
            for (int left = 0; left < 128; left += 32) {
                if (top / 32 == 3) {
                    if (left == 3 * 32) {
                        continue;
                    }
                }

                Bitmap clip = Bitmap.createBitmap(itemBitmap, left, top, 32, 32);
                canvas.drawBitmap(clip, this.left, this.top, null);
                addLeft(32);
            }
        }
    }

    private void drawItem4() throws IOException {
        Bitmap itemBitmap = BitmapFactory.decodeStream(getAssets().open("item_4.png"));

        for (int top = 0; top < 128; top += 32) {
            for (int left = 0; left < 128; left += 32) {
                if (top == 32 || top == 32 * 3) {
                    if (left != 0) {
                        continue;
                    }
                }

                Bitmap clip = Bitmap.createBitmap(itemBitmap, left, top, 32, 32);
                canvas.drawBitmap(clip, this.left, this.top, null);
                addLeft(32);
            }
        }
    }

    private static void saveBitmap(Bitmap bitmap) throws IOException {

        File parent = new File(Environment.getExternalStorageDirectory(), "CyanFlxy");
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                throw new RuntimeException("Can not Create File " + parent.getAbsolutePath());
            }
        }

        parent = new File(parent, "Alien");
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                throw new RuntimeException("Can not Create File " + parent.getAbsolutePath());
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(parent, "alien_resource.png"));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
