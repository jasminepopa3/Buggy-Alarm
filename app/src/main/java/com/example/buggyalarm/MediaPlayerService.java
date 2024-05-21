package com.example.buggyalarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MediaPlayerService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "PLAY":
                    startMusic();
                    break;
                case "STOP":
                    stopMusic();
                    break;
                default:
                    // nothing
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isRepeating = false;

    private void startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.melodie);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (isRepeating) {
                        // Repornim melodia când s-a terminat doar dacă este în modul de repetare
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            // Setăm isRepeating pe false pentru a dezactiva modul de repetare
            isRepeating = false;
        }
    }

    // Metodă pentru a seta modul de repetare al melodiei
    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}