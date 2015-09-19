package com.cyanflxy.mapcreator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cyanflxy.mapcreator.bean.EnemyPropertyBean;
import com.cyanflxy.mapcreator.bean.ImageInfoBean;
import com.cyanflxy.mapcreator.bean.MapBean;
import com.cyanflxy.mapcreator.bean.SharePref;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener, MapElementView.OnImageSelectListener {

    private MapCreateView mapCreateView;
    private MapElementView mapElementView;

    private TextView floorView;

    private TextView nameView;
    private TextView typeView;

    private TextView enemyNameView;
    private TextView enemyDamageView;
    private TextView enemyDefenceView;
    private TextView enemyHpView;
    private TextView enemyExpView;
    private TextView enemyMoneyView;

    private int currentFloor;

    private ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        imageManager = new ImageManager(this);

        floorView = (TextView) findViewById(R.id.stair_level);
        findViewById(R.id.up_floor).setOnClickListener(this);
        findViewById(R.id.down_floor).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        mapCreateView = (MapCreateView) findViewById(R.id.map_create_view);
        mapCreateView.setImageManager(imageManager);

        mapElementView = (MapElementView) findViewById(R.id.map_element_view);
        mapElementView.setOnImageSelectListener(this);
        mapElementView.setImageManager(imageManager);

        nameView = (TextView) findViewById(R.id.element_name);
        typeView = (TextView) findViewById(R.id.element_type);

        enemyNameView = (TextView) findViewById(R.id.enemy_name);
        enemyDamageView = (TextView) findViewById(R.id.enemy_damage);
        enemyDefenceView = (TextView) findViewById(R.id.enemy_defence);
        enemyHpView = (TextView) findViewById(R.id.enemy_hp);
        enemyExpView = (TextView) findViewById(R.id.enemy_exp);
        enemyMoneyView = (TextView) findViewById(R.id.enemy_money);

        currentFloor = SharePref.getCurrentFloor();
        setFloorView();
        clearInfo();

        loadMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onDestroy() {
        imageManager.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                save();
                break;
            case R.id.down_floor:
                save();
                currentFloor--;
                setFloorView();
                loadMap();
                mapElementView.clearFocus();
                break;
            case R.id.up_floor:
                save();
                currentFloor++;
                setFloorView();
                loadMap();
                mapElementView.clearFocus();
                break;
        }
    }

    private void setFloorView() {
        SharePref.setCurrentFloor(currentFloor);
        String str = getString(R.string.floor_level, currentFloor);
        floorView.setText(str);
    }

    @Override
    public void onImageSelect(ImageInfoBean imageInfo) {
        mapCreateView.setCurrentImage(imageInfo);

        if (imageInfo == null) {
            clearInfo();
        } else {

            if ("enemy".equals(imageInfo.type)) {
                setEnemyInfo(imageInfo.property);
            } else {
                clearInfo();
            }

            nameView.setText(imageInfo.name);
            typeView.setText(imageInfo.type);
        }
    }

    private void clearInfo() {

        nameView.setText("");
        typeView.setText("");

        enemyNameView.setText("");
        enemyDamageView.setText("");
        enemyDefenceView.setText("");
        enemyHpView.setText("");
        enemyExpView.setText("");
        enemyMoneyView.setText("");
    }

    private void setEnemyInfo(EnemyPropertyBean enemyInfo) {

        enemyNameView.setText(enemyInfo.name);
        enemyDamageView.setText("" + enemyInfo.damage);
        enemyDefenceView.setText("" + enemyInfo.defence);
        enemyHpView.setText("" + enemyInfo.hp);
        enemyExpView.setText("" + enemyInfo.exp);
        enemyMoneyView.setText("" + enemyInfo.money);
    }

    private void save() {
        File floorFile = getFile(currentFloor);
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(floorFile);

            String map = getMapJson();
            fos.write(map.getBytes());
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private File getFile(int floor) {

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

        parent = new File(parent, "map");
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                throw new RuntimeException("Can not Create File " + parent.getAbsolutePath());
            }
        }

        String floorFileName = floor + ".file";
        return new File(parent, floorFileName);
    }

    private String getMapJson() {
        MapBean mapBean = new MapBean(currentFloor);
        mapBean.setMapData(mapCreateView.getMapData());
        return mapBean.toJson();
    }

    private void loadMap() {
        File mapFile = getFile(currentFloor);
        if (!mapFile.exists()) {
            mapCreateView.clearMap();
            return;
        }

        FileInputStream fis = null;
        String result = null;
        try {
            fis = new FileInputStream(mapFile);
            byte[] buffer = new byte[fis.available()];
            int len = fis.read(buffer);
            result = new String(buffer, 0, len, "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        if (TextUtils.isEmpty(result)) {
            return;
        }

        MapBean mapBean = MapBean.getInstance(result);
        mapCreateView.loadMapData(mapBean);
    }
}
