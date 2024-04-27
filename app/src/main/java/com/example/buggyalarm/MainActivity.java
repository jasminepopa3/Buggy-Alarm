package com.example.buggyalarm;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button;
    TimePicker timePicker;
    boolean notificationSet = false;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //  Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        // return insets;
        //});
        button = findViewById(R.id.btnNotifications);
        timePicker = findViewById(R.id.timePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationSet = true;
                checkNotificationTime();
                Toast.makeText(MainActivity.this, "Alarm set", Toast.LENGTH_SHORT).show();
            }
        });


        // Start checking the time periodically
        startCheckingTime();
    }

    public void checkNotificationTime() {
        int hour, minute;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        int currentHour, currentMinute;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            currentHour = java.time.LocalTime.now().getHour();
            currentMinute = java.time.LocalTime.now().getMinute();
        } else {
            currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
            currentMinute = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE);
        }

        if (hour == currentHour && minute == currentMinute) {
            makeNotification();
            notificationSet = false; // Reset notificationSet to false after notification is shown
        }
    }

    private void startCheckingTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000); // Check every second
                        if (notificationSet) {
                            checkNotificationTime();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void makeNotification(){
        String channelId = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("alarma programatori")
                .setContentText(" alarma canta")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "alarma canta");

        // Adăugăm un extra în Intent pentru a indica că trebuie să pornim serviciul MediaPlayerService
        intent.putExtra("playMusic", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, "Some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // Trimitem comanda către MediaPlayerService pentru a porni redarea melodiei
        Intent musicIntent = new Intent(this, MediaPlayerService.class);
        musicIntent.setAction("PLAY");
        startService(musicIntent);

        // Afișează notificarea
        notificationManager.notify(0, builder.build());


    }


}