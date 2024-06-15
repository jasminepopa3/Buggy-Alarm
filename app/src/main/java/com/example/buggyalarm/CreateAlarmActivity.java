package com.example.buggyalarm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CreateAlarmActivity extends AppCompatActivity {

    Button btnSave, btnCancel;
    TimePicker timePicker;
    boolean notificationSet = false;
    final String[] melodie_selectata = new String[1];
    TextView txtSelectedRingtone;
    MaterialButtonToggleGroup toggleGroup;
    private TextView txtAlarmNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        // Find button IDs
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        timePicker = findViewById(R.id.timePicker);
        txtSelectedRingtone = findViewById(R.id.txtRingtone);
        toggleGroup = findViewById(R.id.toggleGroup);
        txtAlarmNotification = findViewById(R.id.txtAlarmNotification);

        // Request notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(CreateAlarmActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateAlarmActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Listener pentru schimbarea timpului în TimePicker
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calculateTimeUntilAlarm(hourOfDay, minute);
            }
        });

        // Listener pentru butonul SAVE
        btnSave.setOnClickListener(new View.OnClickListener() {
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

                Toast.makeText(CreateAlarmActivity.this, "Alarm set", Toast.LENGTH_SHORT).show();

                // Redirecționare către MainActivity după salvare
                Intent intent = new Intent(CreateAlarmActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Închide activitatea curentă pentru a preveni revenirea la ea cu butonul Back
            }
        });

        // Listener pentru butonul CANCEL
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecționare către MainActivity
                Intent intent = new Intent(CreateAlarmActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Închide activitatea curentă pentru a preveni revenirea la ea cu butonul Back
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

            // Set repeat days based on selected buttons
            alarm.setMon(toggleGroup.getCheckedButtonIds().contains(R.id.btnMon));
            alarm.setTue(toggleGroup.getCheckedButtonIds().contains(R.id.btnTue));
            alarm.setWed(toggleGroup.getCheckedButtonIds().contains(R.id.btnWed));
            alarm.setThu(toggleGroup.getCheckedButtonIds().contains(R.id.btnThu));
            alarm.setFri(toggleGroup.getCheckedButtonIds().contains(R.id.btnFri));
            alarm.setSat(toggleGroup.getCheckedButtonIds().contains(R.id.btnSat));
            alarm.setSun(toggleGroup.getCheckedButtonIds().contains(R.id.btnSun));
            alarm.setEnabled(true);

            userAlarmsRef.child(alarmId).setValue(alarm);
        }
    }

    private void startAlarmCheckService() {
        Intent serviceIntent = new Intent(this, AlarmCheckService.class);
        startService(serviceIntent);
    }

    // Metodă pentru afișarea meniului de selectare a tonului de apel
    public void showRingtoneMenu(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Ringtone");
        builder.setItems(R.array.ringtones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] ringtones = getResources().getStringArray(R.array.ringtones);
                melodie_selectata[0] = ringtones[which];
                txtSelectedRingtone.setText(melodie_selectata[0]);
            }
        });
        builder.show();
    }

    private void calculateTimeUntilAlarm(int hourOfDay, int minute) {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);

        // Calendarul curent
        Calendar now = Calendar.getInstance();

        // Verificăm dacă ora alarmei este în viitor
        if (alarmTime.before(now)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1); // Adăugăm o zi dacă ora este deja în trecut
        }

        // Diferența de timp în milisecunde între acum și ora alarmei
        long diffMillis = alarmTime.getTimeInMillis() - now.getTimeInMillis();

        // Conversie din milisecunde în ore și minute
        long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60;

        // Construim mesajul
        String message = getString(R.string.alarm_set_message, hours, minutes);

        // Actualizăm TextView cu mesajul
        txtAlarmNotification.setText(message);
        txtAlarmNotification.setVisibility(View.VISIBLE); // Facem TextView vizibil
    }
}
