package com.example.buggyalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    TextView textView;
    private String bugs;
    private String language;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        textView = findViewById(R.id.textViewData);
        String data = getIntent().getStringExtra("data");
        bugs = getIntent().getStringExtra("bugs");
        language = getIntent().getStringExtra("language");
        level = getIntent().getStringExtra("level");
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
        // Trimitem bugs către QuizActivity
        intent.putExtra("bugs", bugs);
        intent.putExtra("language", language);
        intent.putExtra("level", level);
        // Adaugă flag pentru a începe o nouă activitate și a șterge activitățile vechi din stivă
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}