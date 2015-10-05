package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cyanflxy.game.bean.EnemyProperty;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.MapElementBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.ImageResourceManager;
import com.cyanflxy.game.widget.HeadView;
import com.github.cyanflxy.magictower.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnemyPropertyFragment extends BaseFragment {

    public static final String TAG = "EnemyPropertyFragment";

    private GameContext gameContext;
    private ImageResourceManager imageManager;

    private List<EnemyProperty> enemyList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameContext = GameContext.getInstance();
        imageManager = gameContext.getImageResourceManager();

        collectEnemyInfo();
    }

    private void collectEnemyInfo() {
        enemyList = new ArrayList<>();
        Set<String> nameSet = new HashSet<>();

        MapBean map = gameContext.getCurrentMap();
        for (MapElementBean mapElement : map.mapData) {
            String name = mapElement.element;
            ImageInfoBean info = imageManager.getImage(name);

            if (info == null || info.type != ImageInfoBean.ImageType.enemy) {
                continue;
            }

            if (nameSet.contains(name)) {
                continue;
            }

            nameSet.add(name);
            enemyList.add(new EnemyProperty(info));
        }

        if (enemyList.size() > 1) {
            Collections.sort(enemyList);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enemy_property, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back).setOnClickListener(onCloseListener);

        ListView listView = (ListView) view.findViewById(R.id.enemy_list);
        listView.setAdapter(new EnemyAdapter());
        listView.setEmptyView(view.findViewById(R.id.empty_view));
    }

    private class EnemyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return enemyList.size();
        }

        @Override
        public Object getItem(int position) {
            return enemyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;

            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_enemy_property, parent, false);
                convertView.findViewById(R.id.title_name).setVisibility(View.GONE);
                convertView.findViewById(R.id.content_name).setVisibility(View.VISIBLE);
                vh = new ViewHolder();

                vh.headView = (HeadView) convertView.findViewById(R.id.enemy_head);
                vh.headView.setImageManager(imageManager);
                vh.nameView = (TextView) convertView.findViewById(R.id.enemy_name);
                vh.hpView = (TextView) convertView.findViewById(R.id.hp);
                vh.lostView = (TextView) convertView.findViewById(R.id.hp_lost);
                vh.damageView = (TextView) convertView.findViewById(R.id.damage);
                vh.defenseView = (TextView) convertView.findViewById(R.id.defense);
                vh.expView = (TextView) convertView.findViewById(R.id.exp);
                vh.moneyView = (TextView) convertView.findViewById(R.id.money);


                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            EnemyProperty enemy = enemyList.get(position);

            vh.headView.setImageInfo(imageManager.getImage(enemy.resourceName));
            vh.nameView.setText(enemy.name);

            vh.hpView.setText(String.valueOf(enemy.hp));
            vh.damageView.setText(String.valueOf(enemy.damage));
            vh.defenseView.setText(String.valueOf(enemy.defense));
            vh.expView.setText(String.valueOf(enemy.exp));
            vh.moneyView.setText(String.valueOf(enemy.money));

            if (gameContext.getHero().damage <= enemy.defense) {
                vh.lostView.setText(R.string.fight_fail);
            } else {
                int lostHp = gameContext.calculateHPDamage(enemy.hp, enemy.damage, enemy.defense, enemy.lifeDrain);
                vh.lostView.setText(String.valueOf(lostHp));
            }

            return convertView;
        }
    }

    private class ViewHolder {
        public HeadView headView;
        public TextView nameView;

        public TextView hpView;
        public TextView lostView;

        public TextView damageView;
        public TextView defenseView;

        public TextView moneyView;
        public TextView expView;
    }
}
