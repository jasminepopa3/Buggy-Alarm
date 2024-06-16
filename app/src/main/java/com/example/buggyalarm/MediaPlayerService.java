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
                case "PLAY MELODIE":
                    String melodie = intent.getStringExtra("melodie");
                    startMusic(melodie);
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

    private boolean isRepeating = true;

    private void startMusic(String melodie) {
        if (mediaPlayer == null) {

            String firebaseStorageUrl = new String();

            if (melodie.equals("Pan Jabi")) {
                firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm---db2.appspot.com/o/Panjabi.mp3?alt=media&token=4ba31dc2-3688-41f3-84d9-dc81d7d461d2";
            } else if (melodie.equals("Vivaldi")) {
                firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm---db2.appspot.com/o/Vivaldi.mp3?alt=media&token=d18a6582-2118-44fc-8212-5865312dc1b4";
            } else if (melodie.equals("AC/DC")) {
                firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm---db2.appspot.com/o/ACDC.mp3?alt=media&token=9812309d-4edd-4146-a032-074a479ed83e";
            }

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