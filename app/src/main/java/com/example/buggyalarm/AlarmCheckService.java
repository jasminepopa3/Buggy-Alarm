package com.example.buggyalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.example.buggyalarm.MediaPlayerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AlarmCheckService extends Service {
    private Handler handler;
    private Runnable runnable;
    private List<Alarm> alarmsList;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        alarmsList = new ArrayList<>();

        // Inițializați runnable-ul pentru verificări periodice
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchAlarmsFromFirebase();
                handler.postDelayed(this, 60000); // Verifică la fiecare minut
            }
        };

        // Porniți verificările periodice
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Opriți verificările periodice
        handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchAlarmsFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userAlarmsRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_alarms").child(currentUser.getUid());

            userAlarmsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    alarmsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Alarm alarm = snapshot.getValue(Alarm.class);
                        alarmsList.add(alarm);
                    }
                    checkAlarms();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Tratează eroarea
                }
            });
        }
    }

    private void checkAlarms() {
        int currentHour = java.time.LocalTime.now().getHour();
        int currentMinute = java.time.LocalTime.now().getMinute();

        for (Alarm alarm : alarmsList) {
            if (alarm.getHour() == currentHour && alarm.getMinute() == currentMinute) {
                makeNotification(alarm);
            }
        }
    }

    private void makeNotification(Alarm alarm) {
        String channelId = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Alarma programatori")
                .setContentText("Alarma canta: " + alarm.getMelody())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Alarma canta");

        // Adăugăm un extra în Intent pentru a indica că trebuie să pornim serviciul MediaPlayerService
        intent.putExtra("playMusic", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, "Some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // Trimitem comanda către MediaPlayerService pentru a porni redarea melodiei
        Intent musicIntent = new Intent(this, MediaPlayerService.class);
        musicIntent.setAction("PLAY MELODIE");
        musicIntent.putExtra("melodie", alarm.getMelody());
        startService(musicIntent);

        // Afișează notificarea
        notificationManager.notify(0, builder.build());
    }
}
