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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button button;
    TimePicker timePicker;
    boolean notificationSet = false;
    private MediaPlayer mediaPlayer;

    final String[] melodie_selectata = new String[1];

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in, redirect to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

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
        // Găsim butonul pentru oprirea melodiei în layout
        Button btnMelodie1 = findViewById(R.id.btnMelodie1);
        Button btnMelodie2 = findViewById(R.id.btnMelodie2);
        Button btnMelodie3 = findViewById(R.id.btnMelodie3);

        // Adăugăm ascultători pentru clicurile pe butoanele de melodie
        btnMelodie1.setOnClickListener(v -> melodie_selectata[0] ="Pan Jabi");

        btnMelodie2.setOnClickListener(v -> melodie_selectata[0] ="Vivaldi");

        btnMelodie3.setOnClickListener(v -> melodie_selectata[0] ="AC/DC");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationSet = true;
                int hour, minute;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                saveAlarmToFirebase(hour, minute, melodie_selectata[0]);

                Toast.makeText(MainActivity.this, "Alarm set", Toast.LENGTH_SHORT).show();
            }
        });

        startAlarmCheckService();

    }

    private void saveAlarmToFirebase(int hour, int minute, String melody) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userAlarmsRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_alarms").child(currentUser.getUid());
            String alarmId = userAlarmsRef.push().getKey(); // Generare ID unic pentru alarmă
            Alarm alarm = new Alarm(alarmId, hour, minute, melody);
            userAlarmsRef.child(alarmId).setValue(alarm);
        }
    }

    private void startAlarmCheckService() {
        Intent serviceIntent = new Intent(this, AlarmCheckService.class);
        startService(serviceIntent);
    }


}