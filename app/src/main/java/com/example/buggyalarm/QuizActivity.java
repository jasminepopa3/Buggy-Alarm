package com.example.buggyalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private List<Question> questionList;
    private TextView questionTextView;
    private TextView questionIndicatorTextView;
    private Button[] optionButtons;
    private Button nextButton;
    private int currentQuestionIndex = 0;
    private boolean answered = false;
    private LinearProgressIndicator questionProgressIndicator;
    private int totalCorrectAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        // Initialize UI elements
        questionTextView = findViewById(R.id.question_textview);
        questionIndicatorTextView = findViewById(R.id.question_indicator_textview);
        optionButtons = new Button[]{
                findViewById(R.id.btn0),
                findViewById(R.id.btn1),
                findViewById(R.id.btn2),
                findViewById(R.id.btn3)
        };
        nextButton = findViewById(R.id.next_btn);
        questionProgressIndicator = findViewById(R.id.question_progress_indicator);

        // Load questions from Firebase
        loadQuestions();

        // Set onClickListeners for option buttons
        for (Button button : optionButtons) {
            button.setOnClickListener(this::onOptionSelected);
        }

        // Set onClickListener for next button
        nextButton.setOnClickListener(view -> {
            if (answered) {
                nextQuestion();
            } else {
                Toast.makeText(this, "Trebuie să selectezi un răspuns.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    if (question != null && question.getOptions().size() == 4) {
                        questionList.add(question);
                    }
                }

                // Shuffle the question list
                Collections.shuffle(questionList);

                // Display the first question if the list is not empty
                if (!questionList.isEmpty()) {
                    displayQuestion();
                } else {
                    Toast.makeText(QuizActivity.this, "Nu există întrebări disponibile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(QuizActivity.this, "Eroare la încărcarea întrebărilor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);
            questionTextView.setText(question.getQuestionText());
            questionIndicatorTextView.setText("Întrebarea " + (currentQuestionIndex + 1) + "/" + questionList.size());
            List<String> options = question.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options.get(i));
            }
            answered = false;
            updateProgress(); // Adăugăm actualizarea progresului aici
        } else {
            // Handle the case where there are no more questions
            Toast.makeText(this, "Ai terminat toate întrebările.", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(false);
            goToEndActivity(); // Redirecționează la EndActivity
        }
    }

    private void updateProgress() {
        int progress = (int) ((currentQuestionIndex + 1) / (float) questionList.size() * 100);
        questionProgressIndicator.setProgressCompat(progress, true);
    }


    public void onOptionSelected(View view) {
        if (currentQuestionIndex < questionList.size()) {
            Button selectedButton = (Button) view;
            String selectedOption = selectedButton.getText().toString();
            String correctOption = questionList.get(currentQuestionIndex).getOptions()
                    .get(questionList.get(currentQuestionIndex).getCorrectOptionIndex());

            if (selectedOption.equals(correctOption)) {
                // Răspuns corect
                //Toast.makeText(this, "Răspuns corect!", Toast.LENGTH_SHORT).show();
                totalCorrectAnswers++;
            }
            /*
            else {
                // Răspuns greșit
                Toast.makeText(this, "Răspuns greșit!", Toast.LENGTH_SHORT).show();
            }
            */

            answered = true;
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            // Handle the case where there are no more questions
            Toast.makeText(this, "Ai terminat toate întrebările.", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(false);
            goToEndActivity(); // Redirecționează la EndActivity
        }
    }

    private void goToEndActivity() {
        Intent intent = new Intent(QuizActivity.this, StopAlarmActivity.class);
        intent.putExtra("TOTAL_CORRECT_ANSWERS", totalCorrectAnswers);
        startActivity(intent);
        finish(); // Oprește activitatea curentă
    }
}


/*
public class QuizActivity extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private List<Question> questionList;
    private TextView questionTextView;
    private Button[] optionButtons;
    private int currentQuestionIndex = 0;
    private LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        // Initialize UI elements
        questionTextView = findViewById(R.id.question_textview);
        optionButtons = new Button[]{
                findViewById(R.id.btn0),
                findViewById(R.id.btn1),
                findViewById(R.id.btn2),
                findViewById(R.id.btn3)
        };
        progressIndicator = findViewById(R.id.question_progress_indicator); // Inițializează progressIndicator

        // Load questions from Firebase
        loadQuestions();
    }

    private void loadQuestions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    questionList.add(question);
                }

                // Shuffle the question list
                Collections.shuffle(questionList);

                // Display the first question
                displayQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);
            questionTextView.setText(question.getQuestionText());
            List<String> options = question.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options.get(i));
            }
            updateProgress();
        }
    }

    private void updateProgress() {
        int progress = (int) ((currentQuestionIndex + 1) / (float) questionList.size() * 100);
        progressIndicator.setProgress(progress);
    }

    public void onOptionSelected(View view) {
        Button selectedButton = (Button) view;
        String selectedOption = selectedButton.getText().toString();
        String correctOption = questionList.get(currentQuestionIndex).getOptions()
                .get(questionList.get(currentQuestionIndex).getCorrectOptionIndex());

        if (selectedOption.equals(correctOption)) {
            // Răspuns corect
            Toast.makeText(this, "Răspuns corect!", Toast.LENGTH_SHORT).show();
        } else {
            // Răspuns greșit
            Toast.makeText(this, "Răspuns greșit!", Toast.LENGTH_SHORT).show();
        }

        // Treci la următoarea întrebare
        nextQuestion();
    }

    public void onNextButtonClicked(View view) {
        nextQuestion();
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            // Handle the case where there are no more questions
            Toast.makeText(this, "Ai terminat toate întrebările.", Toast.LENGTH_SHORT).show();
            goToEndActivity(); // Redirecționează la EndActivity
        }
    }

    private void goToEndActivity() {
        Intent intent = new Intent(QuizActivity.this, StopAlarmActivity.class);
        startActivity(intent);
        finish(); // Oprește activitatea curentă
    }
}
*/




/*
public class QuizActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Găsește butonul pentru oprirea melodiei în layout
        Button stopMusicButton = findViewById(R.id.stopMusicButton);

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
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction("STOP");
        startService(intent);
    }



}
*/