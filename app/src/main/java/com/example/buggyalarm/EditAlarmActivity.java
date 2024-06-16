package com.example.buggyalarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAlarmActivity extends AppCompatActivity {

    Button btnSave, btnCancel, btnSelectRingtone;
    TimePicker timePicker;
    final String[] selectedMelody = new String[1];
    TextView txtSelectedRingtone;
    MaterialButtonToggleGroup toggleGroup;
    private TextView txtAlarmNotification;

    private String alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        // Find view IDs
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelectRingtone = findViewById(R.id.btnSelectRingtone);
        timePicker = findViewById(R.id.timePicker);
        txtSelectedRingtone = findViewById(R.id.txtRingtone);
        toggleGroup = findViewById(R.id.toggleGroup);
        txtAlarmNotification = findViewById(R.id.txtAlarmNotification);

        // Check for intent extras
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("alarmId")) {
            alarmId = intent.getStringExtra("alarmId");
            int hour = intent.getIntExtra("hour", 0);
            int minute = intent.getIntExtra("minute", 0);
            String melody = intent.getStringExtra("melody");
            boolean mon = intent.getBooleanExtra("mon", false);
            boolean tue = intent.getBooleanExtra("tue", false);
            boolean wed = intent.getBooleanExtra("wed", false);
            boolean thu = intent.getBooleanExtra("thu", false);
            boolean fri = intent.getBooleanExtra("fri", false);
            boolean sat = intent.getBooleanExtra("sat", false);
            boolean sun = intent.getBooleanExtra("sun", false);

            // Set time in TimePicker
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            } else {
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
            }

            // Set selected ringtone
            selectedMelody[0] = melody;
            txtSelectedRingtone.setText(selectedMelody[0]);

            // Set repeat days
            if (mon) toggleGroup.check(R.id.btnMon);
            if (tue) toggleGroup.check(R.id.btnTue);
            if (wed) toggleGroup.check(R.id.btnWed);
            if (thu) toggleGroup.check(R.id.btnThu);
            if (fri) toggleGroup.check(R.id.btnFri);
            if (sat) toggleGroup.check(R.id.btnSat);
            if (sun) toggleGroup.check(R.id.btnSun);
        }

        // Listener pentru butonul SAVE
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour, minute;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                updateAlarmInFirebase(alarmId, hour, minute, selectedMelody[0]);

                Toast.makeText(EditAlarmActivity.this, "Alarm updated", Toast.LENGTH_SHORT).show();

                // Redirecționare către MainActivity după salvare
                Intent intent = new Intent(EditAlarmActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Listener pentru butonul CANCEL
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecționare către MainActivity
                Intent intent = new Intent(EditAlarmActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Închide activitatea curentă pentru a preveni revenirea la ea cu butonul Back
            }
        });

        // Listener pentru butonul de selectare a tonului de apel
        btnSelectRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRingtoneMenu();
            }
        });

        startAlarmCheckService();
    }

    // Metodă pentru afișarea meniului de selectare a tonului de apel
    private void showRingtoneMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Ringtone");
        builder.setItems(R.array.ringtones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] ringtones = getResources().getStringArray(R.array.ringtones);
                selectedMelody[0] = ringtones[which];
                txtSelectedRingtone.setText(selectedMelody[0]);
            }
        });
        builder.show();
    }

    private void startAlarmCheckService() {
        Intent serviceIntent = new Intent(this, AlarmCheckService.class);
        startService(serviceIntent);
    }

    private void updateAlarmInFirebase(String alarmId, int hour, int minute, String melody) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userAlarmsRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_alarms").child(currentUser.getUid()).child(alarmId);
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

            userAlarmsRef.setValue(alarm);

            // Opțional: actualizarea listei locale de alarme pentru a reflecta modificarea
            // Acest lucru poate fi util pentru a evita întârzieri în actualizarea AlarmCheckService
            // Dacă folosești o listă locală de alarme în EditAlarmActivity, actualizează-o aici.
        }
    }

}
