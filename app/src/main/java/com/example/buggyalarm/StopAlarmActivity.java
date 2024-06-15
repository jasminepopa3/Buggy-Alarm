package com.example.buggyalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StopAlarmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopalarm);

        TextView totalCorrectAnswersTextView = findViewById(R.id.total_correct_answers_textview);

        // Găsește butonul pentru oprirea melodiei în layout
        Button stopMusicButton = findViewById(R.id.stop_alarm_button);
        int totalCorrectAnswers = getIntent().getIntExtra("TOTAL_CORRECT_ANSWERS", 0);

        totalCorrectAnswersTextView.setText("Number of correct answers: " + totalCorrectAnswers);

        // Adaugă un ascultător pentru evenimentul de clic al butonului
        stopMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
            }
        });
    }

    private void stopMusic() {
        // Trimitem o comandă către MediaPlayerService pentru a opri redarea melodie
        Intent stopIntent = new Intent(this, MediaPlayerService.class);
        stopIntent.setAction("STOP");
        startService(stopIntent);

        // Redirecționăm utilizatorul către MainActivity
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish(); // Închide activitatea curentă
    }
}
