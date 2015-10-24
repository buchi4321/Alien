package com.cyanflxy.game.sound;

import android.media.MediaPlayer;

import com.cyanflxy.game.data.GameSharedPref;
import com.github.cyanflxy.magictower.AppApplication;
import com.github.cyanflxy.magictower.R;

public class SoundUtil {

    public static void openDoor() {
        play(R.raw.sound_door);
    }

    public static void fight() {
        play(R.raw.sound_fight);
    }

    public static void getGood() {
        play(R.raw.sound_good);
    }

    public static void buySuccess() {
        play(R.raw.sound_buy_success);
    }

    public static void buyError() {
        play(R.raw.sound_buy_error);
    }

    public static void fail() {
        play(R.raw.sound_fail);
    }

    private static void play(int id) {
        if (!GameSharedPref.isPlayGameSound()) {
            return;
        }

        final MediaPlayer player = MediaPlayer.create(AppApplication.baseContext, id);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.reset();
                player.release();
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                player.reset();
                player.release();
                return false;
            }
        });

        player.start();
    }
}
