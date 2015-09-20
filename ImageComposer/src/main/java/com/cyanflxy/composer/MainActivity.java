package com.cyanflxy.composer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Canvas canvas;
    private int left;
    private int top;

    private int width = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = null;
        try {
            bitmap = drawBorder();
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
    }

    }

    private Bitmap drawBorder() throws IOException {
        Bitmap allBitmap = BitmapFactory.decodeStream(getAssets().open("all.png"));
        Bitmap border = Bitmap.createBitmap(allBitmap, 0, 32 * 5 +4, 8, 32 );
        saveBitmap(border, "border.png");

        allBitmap.recycle();
        return border;

    }

    private void writeJson() {

        String fileName = getFile("enemy.file");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);

            writeEnemy(fos);
            writeNPC(fos);

            fos.flush();

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Enemy {
        public String name;
        public int id;
        public int hp;
        public int damage;
        public int defence;
        public int exp;
        public int money;
    }

    private static final int ENEMY_BASE = 16;

    private class MyEnemy {
        public MyEnemy(Enemy enemy) {
            name = enemy.name;

            id[0] = enemy.id + ENEMY_BASE;
            id[1] = enemy.id + ENEMY_BASE + 1;
            id[2] = enemy.id + ENEMY_BASE + 2;
            id[3] = enemy.id + ENEMY_BASE + 3;

            property.name = enemy.name;
            property.hp = enemy.hp;
            property.damage = enemy.damage;
            property.defence = enemy.defence;
            property.exp = enemy.exp;
            property.money = enemy.money;

        }

        public String name;
        public int[] id = new int[4];
        public String type = "enemy";
        public Property property = new Property();
    }

    private class Property {
        public String name;
        public int hp;
        public int damage;
        public int defence;
        public int exp;
        public int money;
        public String extra = "";
    }


    private void writeEnemy(OutputStream os) throws IOException, XmlPullParserException {
        List<Enemy> list = readXml();
        List<Object> myEnemyList = new ArrayList<>(list.size());

        for (Enemy enemy : list) {
            myEnemyList.add(new MyEnemy(enemy));
        }

        writeJson(os, myEnemyList);
    }

    private List<Enemy> readXml() throws IOException, XmlPullParserException {
        XmlPullParser parser = Xml.newPullParser();
        InputStream is = getAssets().open("enemy.tsx");
        parser.setInput(is, "UTF-8");

        List<Enemy> enemyList = new ArrayList<>();
        Enemy enemy = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();

                    if ("tile".equals(tagName)) {
                        enemy = new Enemy();
                        enemy.id = Integer.parseInt(parser.getAttributeValue(0));
                    } else if (enemy != null && "property".equals(tagName)) {
                        String name = parser.getAttributeValue("", "name");
                        String value = parser.getAttributeValue("", "value");

                        if ("att".equals(name)) {
                            enemy.damage = Integer.parseInt(value);
                        } else if ("def".equals(name)) {
                            enemy.defence = Integer.parseInt(value);
                        } else if ("exp".equals(name)) {
                            enemy.exp = Integer.parseInt(value);
                        } else if ("gold".equals(name)) {
                            enemy.money = Integer.parseInt(value);
                        } else if ("hp".equals(name)) {
                            enemy.hp = Integer.parseInt(value);
                        } else if ("name".equals(name)) {
                            enemy.name = value;
                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("tile".equals(parser.getName())) {
                        enemyList.add(enemy);
                        enemy = null;
                    }
                    break;
            }

            eventType = parser.next();
        }

        is.close();
        return enemyList;
    }

    private class NPC {

        public NPC(String name, int id, String type) {
            this.name = name;

            this.id[0] = id;
            this.id[1] = id + 1;
            this.id[2] = id + 2;
            this.id[3] = id + 3;

            this.type = type;
        }

        public String name;
        public int[] id = new int[4];
        public String type;
    }

    private void writeNPC(OutputStream os) throws IOException {
        List<NPC> list = new ArrayList<>(12);
        // npc
        list.add(new NPC("blue_old", 256, "npc"));
        list.add(new NPC("red_old", 260, "npc"));
        list.add(new NPC("thief", 264, "npc"));
        list.add(new NPC("angel", 268, "npc"));
        list.add(new NPC("ghost", 272, "npc"));
        list.add(new NPC("red_shop", 276, "npc"));
        list.add(new NPC("blue_shop", 280, "npc"));
        list.add(new NPC("princess", 284, "npc"));
        list.add(new NPC("blue_wizard", 288, "npc"));
        list.add(new NPC("red_wizard", 292, "npc"));
        list.add(new NPC("kine", 296, "npc"));
        list.add(new NPC("warrior", 300, "npc"));

        //door
        list.add(new NPC("yellow_door", 304, "door"));
        list.add(new NPC("blue_door", 308, "door"));
        list.add(new NPC("red_door", 312, "door"));
        list.add(new NPC("iron_door", 316, "door"));
        list.add(new NPC("blue_fake_wall", 320, "door"));
        list.add(new NPC("brown_fake_wall", 324, "door"));
        list.add(new NPC("gray_fake_wall", 328, "door"));
        list.add(new NPC("prison", 332, "door"));

        //wall
        list.add(new NPC("lava", 336, "wall"));
        list.add(new NPC("star", 340, "wall"));

        writeJson(os, list);
    }

    private void writeJson(OutputStream os, List<?> list) throws IOException {
        Gson gson = new Gson();

        for (Object o : list) {
            String str = gson.toJson(o);
            os.write(str.getBytes());
            os.write(',');
            os.write('\n');
        }

    }

    private void drawSword() {
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        try {

            Bitmap itemBitmap = BitmapFactory.decodeStream(getAssets().open("item_4.png"));

            Bitmap clip = Bitmap.createBitmap(itemBitmap, 0, 32, 32, 32);
            canvas.drawBitmap(clip, 0, 0, null);

            canvas.rotate(270);
            canvas.save();

            saveBitmap(bitmap, "sword.png");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }
    }

    private Bitmap drawPicture() {

        Bitmap bitmap = Bitmap.createBitmap(width, 832, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        left = 0;
        top = 0;

        try {
            //drawBg();
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

            saveBitmap(bitmap, "resource.png");
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void drawBg() throws IOException {
        Bitmap allBitmap = BitmapFactory.decodeStream(getAssets().open("all.png"));
        Bitmap floor = Bitmap.createBitmap(allBitmap, 3 * 32, 32, 32, 32);

        for (int w = 0; w < canvas.getWidth(); w += 32) {
            for (int h = 0; h < canvas.getHeight(); h += 32) {
                canvas.drawBitmap(floor, w, h, null);
            }
        }

        allBitmap.recycle();
        floor.recycle();

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

        bitmap.recycle();

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
        doorBitmap.recycle();
    }

    private void drawBackground() throws IOException {
        Bitmap lavaBitmap = BitmapFactory.decodeStream(getAssets().open("lava.png"));
        canvas.drawBitmap(lavaBitmap, this.left, this.top, null);
        addLeft(128);
        lavaBitmap.recycle();

        Bitmap starBitmap = BitmapFactory.decodeStream(getAssets().open("star.png"));
        canvas.drawBitmap(starBitmap, this.left, this.top, null);
        addLeft(128);
        starBitmap.recycle();

        Bitmap allBitmap = BitmapFactory.decodeStream(getAssets().open("all.png"));

        Bitmap wall = Bitmap.createBitmap(allBitmap, 5 * 32, 0, 32 * 2, 32);
        canvas.drawBitmap(wall, left, top, null);
        addLeft(32 * 2);


        Bitmap floor = Bitmap.createBitmap(allBitmap, 3 * 32, 32, 32, 32);
        canvas.drawBitmap(floor, left, top, null);
        addLeft(32);

        Bitmap stairs = Bitmap.createBitmap(allBitmap, 0, 31 * 32, 64, 32);
        canvas.drawBitmap(stairs, left, top, null);
        addLeft(64);


        Bitmap shopLeft = Bitmap.createBitmap(allBitmap, 3 * 32, 31 * 32, 32, 32);
        canvas.drawBitmap(shopLeft, left, top, null);
        addLeft(32);

        Bitmap shopRight = Bitmap.createBitmap(allBitmap, 5 * 32, 31 * 32, 32, 32);
        canvas.drawBitmap(shopRight, left, top, null);
        addLeft(32);
        allBitmap.recycle();
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
        itemBitmap.recycle();
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
        itemBitmap.recycle();
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
        itemBitmap.recycle();
    }

    private void saveBitmap(Bitmap bitmap, String fileName) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getFile(fileName));
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

    private String getFile(String fileName) {

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

        return new File(parent, fileName).getAbsolutePath();
    }
}
