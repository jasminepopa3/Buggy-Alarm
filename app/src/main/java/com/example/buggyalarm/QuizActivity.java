package com.example.buggyalarm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.ChildEventListener;
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
    private List<Question> incorrectQuestions;
    private TextView questionTextView;
    private TextView questionIndicatorTextView;
    private Button[] optionButtons;
    private Button nextButton;
    private int currentQuestionIndex = 0;
    private boolean answered = false;
    private LinearProgressIndicator questionProgressIndicator;
    private int totalCorrectAnswers = 0;
    private String bugs;
    private String language;
    private String level;

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

        // Preia valorile atributelor din intent
        bugs = getIntent().getStringExtra("bugs");
        language = getIntent().getStringExtra("language");
        level = getIntent().getStringExtra("level");

        incorrectQuestions = new ArrayList<>();

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
                Toast.makeText(this, "You must select an answer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestions() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDbRef = mDatabase.getReference("questions/" + language + "/Level "+ level);
        mDbRef.addValueEventListener(new ValueEventListener() {
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

                // Select only the first 3 questions if there are more than 3
                if (questionList.size() > Integer.parseInt(bugs)) {
                    questionList = questionList.subList(0, Integer.parseInt(bugs));
                }

                // Display the first question if the list is not empty
                if (!questionList.isEmpty()) {
                    displayQuestion();
                } else {
                    Toast.makeText(QuizActivity.this, "No questions available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(QuizActivity.this, "Error loading questions.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);
            questionTextView.setText(question.getQuestionText());
            questionIndicatorTextView.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
            List<String> options = question.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options.get(i));
            }
            answered = false;
            updateProgress(); // Update the progress bar
        } else {
            // Handle the case where there are no more questions
//            Toast.makeText(this, "You have completed all questions.", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(false);
            goToEndActivity(); // Redirect to EndActivity
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
                // Correct answer
                totalCorrectAnswers++;
            }else{
                //Add the incorrect questions to the new list
                incorrectQuestions.add(questionList.get(currentQuestionIndex));
            }

            answered = true;
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            // Handle the case where there are no more questions
            Toast.makeText(this, "You have completed all questions.", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(false);
            goToEndActivity(); // Redirect to EndActivity
        }
    }

    private void goToEndActivity() {
        if (totalCorrectAnswers == questionList.size()) {
            Intent intent = new Intent(QuizActivity.this, StopAlarmActivity.class);
            intent.putExtra("TOTAL_CORRECT_ANSWERS", totalCorrectAnswers);
            startActivity(intent);
            finish(); // Stop the current activity
        } else {

            // If there are incorrect questions, restart the quiz with those questions
            showRetryDialog();
        }
    }

    private void showRetryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_retry);
        dialog.setCancelable(false);

        // Set the dialog to be full-screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        new Handler().postDelayed(() -> {
            dialog.dismiss();

            // If there are incorrect questions, restart the quiz with those questions
            questionList = new ArrayList<>(incorrectQuestions);
            incorrectQuestions.clear();
            currentQuestionIndex = 0;
            totalCorrectAnswers = 0;

            // Handle the case where there are incorrect answers
            nextButton.setEnabled(true);
            displayQuestion();
        }, 3000); // Display the dialog for 3 seconds
    }
}
