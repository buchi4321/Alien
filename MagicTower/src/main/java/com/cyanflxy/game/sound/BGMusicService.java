package com.cyanflxy.game.sound;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cyanflxy.game.data.GameSharedPref;

import java.io.IOException;

public class BGMusicService extends Service {

    public static final String MUSIC_CMD = "music_cmd";
    public static final String MUSIC_FILE = "music_file";
    public static final String MUSIC_VOLUME = "music_volume";

    public static final int CMD_SET_SOURCE = 1;
    public static final int CMD_CHANGE_VOLUME = 2;
    public static final int CMD_CHECK_STATE = 3;
    public static final int CMD_PAUSE = 4;

    private String musicFile;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        float v = GameSharedPref.getGameVolume();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setVolume(v, v);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int cmd = intent.getIntExtra(MUSIC_CMD, 0);
        switch (cmd) {
            case CMD_SET_SOURCE:
                String file = intent.getStringExtra(MUSIC_FILE);
                if (!TextUtils.equals(musicFile, file)) {
                    musicFile = file;

                    if (GameSharedPref.isPlayBackgroundMusic()) {
                        stopMusic();
                        startMusic();
                    }
                }
                break;
            case CMD_PAUSE:
                stopMusic();
                break;
            case CMD_CHECK_STATE:
                if (GameSharedPref.isPlayBackgroundMusic()) {
                    startMusic();
                } else {
                    stopMusic();
                }
                break;
            case CMD_CHANGE_VOLUME:
                float volume = intent.getFloatExtra(MUSIC_VOLUME, 0);
                mediaPlayer.setVolume(volume, volume);
                break;
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startMusic() {
        if (TextUtils.isEmpty(musicFile)) {
            return;
        }

        try {
            AssetFileDescriptor fd = getAssets().openFd(musicFile);
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            fd.close();

            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        mediaPlayer.reset();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
}
