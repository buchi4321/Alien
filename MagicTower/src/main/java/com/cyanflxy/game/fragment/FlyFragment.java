package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.widget.MapFloorSelectView;
import com.github.cyanflxy.magictower.R;

public class FlyFragment extends BaseFragment {

    public static final String TAG = "FlyFragment";

    public interface OnMapSelectListener {
        void onMapSelect(int mapFloor);
    }

    private OnMapSelectListener listener;

    public void setOnMapSelectListener(OnMapSelectListener l) {
        listener = l;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fly, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnFragmentCloseListener) getActivity()).closeFragment(FlyFragment.this);
            }
        });

        MapFloorSelectView floorSelector = (MapFloorSelectView) view.findViewById(R.id.map_floor_selector);
        floorSelector.setCurrentFloor(GameContext.getInstance().getHero().floor);
        floorSelector.setMap(GameContext.getInstance().getGameData().maps);
        floorSelector.setOnMapSelectListener(new MapFloorSelectView.OnMapSelectListener() {
            @Override
            public void onMapSelect(int mapFloor) {
                if (listener != null) {
                    listener.onMapSelect(mapFloor);
                }
            }
        });
    }


}
