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

    private boolean isRepeating = false;

    private void startMusic(String melodie) {
        if (mediaPlayer == null) {

            String firebaseStorageUrl = new String();

            if (melodie.equals("Pan Jabi")) {
                firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm.appspot.com/o/melodie.mp3?alt=media&token=fc458882-c9bc-4902-a264-4f70bc5e5a73";
            } else if (melodie.equals("Vivaldi")) {
                firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm.appspot.com/o/melodie2.mp3?alt=media&token=d0a624f3-032f-468b-9d51-48213009a150";
            } else if (melodie.equals("AC/DC")) {
                firebaseStorageUrl = "https://firebasestorage.googleapis.com/v0/b/buggy-alarm.appspot.com/o/melodie3.mp3?alt=media&token=e30ac9a9-2c0b-4058-9714-98f7953794f7";
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