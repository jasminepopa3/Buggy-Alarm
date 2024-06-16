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
        TextView averageTimeTextView = findViewById(R.id.average_time_textview);
        TextView questionsTimeTextView = findViewById(R.id.questions_time_textview);

        // Găsește butonul pentru oprirea melodiei în layout
        Button stopMusicButton = findViewById(R.id.stop_alarm_button);
        double averageTime = getIntent().getDoubleExtra("AVERAGE_TIME", 0);
        String questions_time = getIntent().getStringExtra("QUESTION_TIMES_TEXT");


        totalCorrectAnswersTextView.setText("All answers are correct!");
        averageTimeTextView.setText("Average Time per Question: " + String.format("%.2f", averageTime) + " seconds");
        questionsTimeTextView.setText("Time spent on each question: " + questions_time + " seconds");

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
