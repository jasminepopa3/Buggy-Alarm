package com.example.buggyalarm;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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

        // Initialize the runnable for periodic checks
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchAlarmsFromFirebase();
                handler.postDelayed(this, 1000); // Check every second
            }
        };

        // Start periodic checks
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop periodic checks
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
                        if (alarm != null && alarm.isEnabled() && (isAlarmForToday(alarm) || shouldRepeatEveryday(alarm))) {
                            alarmsList.add(alarm);
                        }
                    }
                    checkAlarms();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private boolean isAlarmForToday(Alarm alarm) {
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        switch (currentDay) {
            case MONDAY:
                return alarm.isMon();
            case TUESDAY:
                return alarm.isTue();
            case WEDNESDAY:
                return alarm.isWed();
            case THURSDAY:
                return alarm.isThu();
            case FRIDAY:
                return alarm.isFri();
            case SATURDAY:
                return alarm.isSat();
            case SUNDAY:
                return alarm.isSun();
            default:
                return false;
        }
    }

    private boolean shouldRepeatEveryday(Alarm alarm) {
        // Verificăm dacă toate zilele săptămânii sunt setate pe false și alarma este activată
        return !alarm.isMon() && !alarm.isTue() && !alarm.isWed() && !alarm.isThu()
                && !alarm.isFri() && !alarm.isSat() && !alarm.isSun() && alarm.isEnabled();
    }


    private void checkAlarms() {
        int currentHour = LocalTime.now().getHour();
        int currentMinute = LocalTime.now().getMinute();
        int currentSecond = LocalTime.now().getSecond();

        for (Alarm alarm : alarmsList) {
            if (alarm.getHour() == currentHour && alarm.getMinute() == currentMinute && currentSecond == 0) {
                makeNotification(alarm);
            }
        }
    }

    private void makeNotification(Alarm alarm) {
        String channelId = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("BUGGY ALARM: TIME TO FIX SOME ERRORS!")
                .setContentText("Solve the quiz to stop the alarm!\n Are you ready?" + alarm.getMelody())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "It's bug o'clock!");

        // Adăugăm un extra în Intent pentru a indica că trebuie să pornim serviciul MediaPlayerService
        intent.putExtra("playMusic", true);

        // Transmiterea atributelor
        intent.putExtra("bugs", alarm.getBugs());
        intent.putExtra("language", alarm.getLanguage());
        intent.putExtra("level", alarm.getLevel());

        int requestCode = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, "A new coding challenge is waiting for you!", importance);
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
