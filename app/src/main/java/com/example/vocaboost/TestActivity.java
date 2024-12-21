package com.example.vocaboost;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TextView koreanSentence, englishBefore, englishAfter, progressText;
    private EditText inputWord;
    private ProgressBar progressBar;
    private Button checkButton, nextButton;

    private List<String[]> wordData = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int incorrectCount = 0;

    private String correctAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // UI 초기화
        koreanSentence = findViewById(R.id.koreanSentence);
        englishBefore = findViewById(R.id.englishSentenceBefore);
        englishAfter = findViewById(R.id.englishSentenceAfter);
        inputWord = findViewById(R.id.inputWord);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        checkButton = findViewById(R.id.checkButton);
        nextButton = findViewById(R.id.nextButton);

        nextButton.setEnabled(false);

        // MainActivity에서 todayDate 가져오기
        Intent intent = getIntent();
        String todayDate = intent.getStringExtra("todayDate");

        // 데이터 로드
        loadWordData(todayDate);

        // 첫 번째 문제 표시
        if (!wordData.isEmpty()) {
            progressBar.setMax(wordData.size());
            showQuestion();
        }

        // 확인 버튼 클릭 이벤트
        checkButton.setOnClickListener(v -> {
            String userInput = inputWord.getText().toString().trim();
            if (userInput.isEmpty()) {
                Toast.makeText(this, "단어를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            checkAnswer(userInput);
        });

        // 다음 버튼 클릭 이벤트
        nextButton.setOnClickListener(v -> {
            if (currentQuestionIndex < wordData.size() - 1) {
                currentQuestionIndex++;
                showQuestion();
                checkButton.setEnabled(true);
                nextButton.setEnabled(false);
                inputWord.setText("");
            } else {
                showResultDialog();
            }
        });
    }

    private void loadWordData(String date) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getWordsByDate(date);

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
            String example = cursor.getString(cursor.getColumnIndex("example"));
            String translation = cursor.getString(cursor.getColumnIndex("translation"));
            wordData.add(new String[]{word, meaning, example, translation});
        }
        cursor.close();

        // 문제 순서 랜덤으로 섞기
        Collections.shuffle(wordData);
    }

    private void showQuestion() {
        String[] question = wordData.get(currentQuestionIndex);
        String word = question[0];
        String meaning = question[1];
        String example = question[2];
        String translation = question[3];

        koreanSentence.setText(translation);

        // 영어 문장에서 빈칸 설정
        String[] words = example.split(" ");
        int blankIndex = example.toLowerCase().indexOf(word.toLowerCase());
        correctAnswer = word;

        // 예문 분리
        String beforeBlank = example.substring(0, blankIndex).trim();
        String afterBlank = example.substring(blankIndex + word.length()).trim();

        englishBefore.setText(beforeBlank);
        englishAfter.setText(afterBlank);

        // 진행 상태 업데이트
        progressBar.setProgress(currentQuestionIndex + 1);
        progressText.setText((currentQuestionIndex + 1) + "/" + wordData.size());
    }

    private void checkAnswer(String userInput) {
        if (userInput.equalsIgnoreCase(correctAnswer)) {
            Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
            checkButton.setEnabled(false);
            nextButton.setEnabled(true);
        } else {
            Toast.makeText(this, "틀렸습니다!", Toast.LENGTH_SHORT).show();
            incorrectCount++;
            checkButton.setEnabled(false);
            nextButton.setEnabled(true);
        }
    }

    private void showResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("시험 결과");
        builder.setMessage("총 문제: " + wordData.size() + "\n맞춘 문제: " + (wordData.size() - incorrectCount) + "\n틀린 문제: " + incorrectCount);
        builder.setPositiveButton("확인", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }
}
