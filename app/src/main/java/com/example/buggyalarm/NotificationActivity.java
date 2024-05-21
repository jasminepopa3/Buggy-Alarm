package com.example.buggyalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        textView = findViewById(R.id.textViewData);
        String data = getIntent().getStringExtra("data");
        textView.setText(data);

        // Găsim butonul pentru oprirea melodiei în layout
        Button btnStopMusic = findViewById(R.id.btnStopMusic);

        // Adăugăm un ascultător pentru evenimentul de clic al butonului
        btnStopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
            }
        });
    }

    // Metoda pentru oprirea melodiei în MediaPlayerService
    private void stopMusic() {
        // Trimitem o comandă către MediaPlayerService pentru a opri redarea melodie
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }
}