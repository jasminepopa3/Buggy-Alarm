package com.example.buggyalarm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList = new ArrayList<>();
    private FloatingActionButton fabCreateAlarm;
    private FirebaseAuth mAuth;
    private TextView noAlarmsTextView;

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

        ImageView imageView = findViewById(R.id.logoImageView);

        // Load bitmap with BitmapFactory.Options to resize it
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;  // Adjust sample size as needed
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cover_image, options);
        imageView.setImageBitmap(bitmap);
        noAlarmsTextView = findViewById(R.id.noAlarmsTextView);

        recyclerView = findViewById(R.id.alarm_recycler_view);
        fabCreateAlarm = findViewById(R.id.fab_create_alarm);

        // Initialize LinearLayoutManager for RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize AlarmAdapter with the alarm list
        alarmAdapter = new AlarmAdapter(this, alarmList);
        recyclerView.setAdapter(alarmAdapter);

        // Set click listener for the Floating Action Button
        fabCreateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateAlarmActivity when FloatingActionButton is clicked
                startActivity(new Intent(MainActivity.this, CreateAlarmActivity.class));
            }
        });

        // Fetch alarms from Firebase
        fetchAlarmsFromFirebase();
    }

    private void fetchAlarmsFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userAlarmsRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_alarms").child(currentUser.getUid());

            userAlarmsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    alarmList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Alarm alarm = snapshot.getValue(Alarm.class);
                        if (alarm != null) {
                            alarmList.add(alarm);
                        }
                    }

                    // Sort alarmList by time
                    Collections.sort(alarmList, new Comparator<Alarm>() {
                        @Override
                        public int compare(Alarm a1, Alarm a2) {
                            // Assuming time is stored as a long value representing milliseconds
                            return Long.compare(a1.getTime(), a2.getTime());
                        }
                    });

                    alarmAdapter.notifyDataSetChanged();

                    // Check if there are no alarms and update the visibility of the TextView
                    if (alarmList.isEmpty()) {
                        noAlarmsTextView.setVisibility(View.VISIBLE);
                    } else {
                        noAlarmsTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Failed to load alarms.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
