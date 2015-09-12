package com.cyanflxy.mapcreator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private MapCreateView mapCreateView;
    private MapElementView mapElementView;

    private EditText floorView;
    private EditText widthView;
    private EditText heightView;

    private TextView nameView;
    private TextView typeView;

    private TextView enemyNameView;
    private TextView enemyDamageView;
    private TextView enemyDefenceView;
    private TextView enemyHpView;
    private TextView enemyExpView;
    private TextView enemyMoneyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mapCreateView = (MapCreateView) findViewById(R.id.map_create_view);
        mapElementView = (MapElementView) findViewById(R.id.map_element_view);

        findViewById(R.id.save).setOnClickListener(this);

        floorView = (EditText) findViewById(R.id.floor);
        widthView = (EditText) findViewById(R.id.width);
        heightView = (EditText) findViewById(R.id.height);

        nameView = (TextView) findViewById(R.id.element_name);
        typeView = (TextView) findViewById(R.id.element_type);

        enemyNameView = (TextView) findViewById(R.id.enemy_name);
        enemyDamageView = (TextView) findViewById(R.id.enemy_damage);
        enemyDefenceView = (TextView) findViewById(R.id.enemy_defence);
        enemyHpView = (TextView) findViewById(R.id.enemy_hp);
        enemyExpView = (TextView) findViewById(R.id.enemy_exp);
        enemyMoneyView = (TextView) findViewById(R.id.enemy_money);

        ImageManager imageManager = ImageManager.getInstance();
        imageManager.init(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                break;
        }
    }
}
