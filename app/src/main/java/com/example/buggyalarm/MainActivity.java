package com.example.buggyalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// MainActivity.java

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList = new ArrayList<>();
    private Button createAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.alarm_recycler_view);
        createAlarmButton = findViewById(R.id.create_alarm_button);

        // Initialize LinearLayoutManager for RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize AlarmAdapter with a list of dummy alarms
        // Replace with actual alarms retrieved from Firebase in your actual implementation
        alarmList.add(new Alarm("1", 8, 0, "", true, true, true, false, false, false, false));
        alarmList.add(new Alarm("2", 19, 30, "", false, false, false, true, true, false, false));
        alarmList.add(new Alarm("3", 7, 45, "", true, false, true, false, true, false, true));

        // Initialize AlarmAdapter with the dummy list
        alarmAdapter = new AlarmAdapter(this, alarmList);
        recyclerView.setAdapter(alarmAdapter);

        // Set click listener for the Create Alarm button
        createAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder action for Create Alarm button click
                Toast.makeText(MainActivity.this, "Create Alarm clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


