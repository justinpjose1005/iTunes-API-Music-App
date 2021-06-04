package com.example.lybrateassignment;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

public class TrackService extends Service {
    private String mUrl;
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.mUrl = intent.getStringExtra("url");
        try {
            mMediaPlayer.setDataSource(mUrl);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Audio started playing.", Toast.LENGTH_SHORT).show();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
                Toast.makeText(getApplicationContext(), "Audio preview has ended.", Toast.LENGTH_SHORT).show();
            }
        });
        return startId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.pause();
    }
}
