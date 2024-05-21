package com.example.buggyalarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;

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

            String firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm.appspot.com/o/melodie.mp3?alt=media&token=fc458882-c9bc-4902-a264-4f70bc5e5a73";
            String accessToken = "fc458882-c9bc-4902-a264-4f70bc5e5a73";

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(firebaseStorageUrl);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (isRepeating) {
                            mediaPlayer.start();
                        }
                    }
                });
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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