package com.example.vocaboost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Button;
import android.database.Cursor;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<WordItem> wordList;
    private Button DateButton, TestButton, AddButton, EditButton, WordButton, MeanButton;
    private DatabaseHelper dbHelper;
    private String todayDate;
    private ArrayList<TextView> wordCells;  // 단어 셀 리스트
    private ArrayList<TextView> meaningCells;  // 뜻 셀 리스트

    int wordCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // SharedPreferences를 통해 최초 실행 여부 확인
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        wordCount = prefs.getInt("WordCount", 10);
        // 리스트 초기화
        wordCells = new ArrayList<>();
        meaningCells = new ArrayList<>();

        DateButton = findViewById(R.id.DateButton);
        TestButton = findViewById(R.id.TestButton);
        AddButton = findViewById(R.id.AddButton);
        //EditButton = findViewById(R.id.EditButton);
        //WordButton = findViewById(R.id.WordButton);
        //MeanButton = findViewById(R.id.MeanButton);

        // 오늘 날짜 설정
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 단어 데이터 초기화
        wordList = new ArrayList<>(wordCount);

        dbHelper = new DatabaseHelper(this);

        // ScrollView 안의 LinearLayout 가져오기
        LinearLayout container = findViewById(R.id.Container);

        // weight 적용
        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(
                0, // 너비를 weight로 조정
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // weight 값
        );

        // 동적으로 단어 갯수만큼 항목 추가
        for (int i = 1; i <= wordCount; i++) {
            // 행 레이아웃 생성
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            // 왼쪽 셀 생성
            TextView wordCell = new TextView(this);
            wordCell.setText("");
            wordCell.setPadding(16, 16, 16, 16);//얘는 보고 지우기
            wordCell.setTextSize(20);
            wordCell.setGravity(Gravity.CENTER);
            wordCell.setBackgroundResource(R.drawable.right_bottom_border);
            wordCell.setLayoutParams(Params);

            TextView meaningCell = new TextView(this);
            wordCell.setText("");
            meaningCell.setPadding(16, 16, 16, 16); //얘는 보고 지우기
            meaningCell.setTextSize(20);
            meaningCell.setGravity(Gravity.CENTER); // 올바른 셀에 적용
            meaningCell.setBackgroundResource(R.drawable.bottom_only_border);
            meaningCell.setLayoutParams(Params);

            // 행에 셀 추가
            row.addView(wordCell);
            row.addView(meaningCell);

            // 빈칸 리스트에 추가
            wordCells.add(wordCell);
            meaningCells.add(meaningCell);

            // 컨테이너에 행 추가
            container.addView(row);
        }

        loadWordsFromDatabase();

        //추가 버튼
        AddButton.setOnClickListener(v -> showAddWordDialog());

        //날짜 버튼
        DateButton.setOnClickListener(v -> {
            DialogFragment dialogFragment = new DatePickerFragment();
            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        //시험 버튼
        TestButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            intent.putExtra("todayDate", todayDate);
            startActivity(intent);
        });
        addWordCellClickListeners();
    }

    // 데이터베이스에서 단어 로드
    private void loadWordsFromDatabase() {
        // 화면의 단어 초기화
        for (TextView wordCell : wordCells) {
            wordCell.setText(""); // 단어 초기화
        }
        for (TextView meaningCell : meaningCells) {
            meaningCell.setText(""); // 뜻 초기화
        }

        Cursor cursor = dbHelper.getWordsByDate(todayDate);

        if (cursor != null && cursor.moveToFirst()) {
            int wordIndex = cursor.getColumnIndex("word");
            int meaningIndex = cursor.getColumnIndex("meaning");

            if (wordIndex == -1 || meaningIndex == -1) {
                Toast.makeText(this, "데이터베이스 컬럼이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }

            int index = 0;
            do {
                String word = cursor.getString(wordIndex);
                String meaning = cursor.getString(meaningIndex);

                // 빈 셀에 단어와 뜻 추가
                if (index < wordCells.size()) {
                    wordCells.get(index).setText(word);
                    meaningCells.get(index).setText(meaning);
                }
                index++;
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "단어가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showAddWordDialog() {
        // 팝업창의 View를 inflate
        View dialogView = getLayoutInflater().inflate(R.layout.add_word_dialog, null);

        // 팝업 내부 View 참조
        EditText wordInput = dialogView.findViewById(R.id.AddWordEdit);
        EditText meaningInput = dialogView.findViewById(R.id.AddMeanEdit);
        EditText exampleInput = dialogView.findViewById(R.id.ExampleEdit); // 예문 입력 필드 추가
        EditText translationInput = dialogView.findViewById(R.id.TranslationEdit); // 예문 해석 입력 필드 추가

        // Dialog 생성
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("단어 추가")
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // 추가 버튼 클릭 시
        dialogView.findViewById(R.id.DialogWordAddButton).setOnClickListener(v -> {
            String word = wordInput.getText().toString().trim();
            String meaning = meaningInput.getText().toString().trim();
            String example = exampleInput.getText().toString().trim(); // 예문 입력값 가져오기
            String translation = translationInput.getText().toString().trim(); // 예문 해석 입력값 가져오기
            String date = todayDate; // 현재 날짜 사용

            if (!word.isEmpty() && !meaning.isEmpty()) {
                if (fillEmptyCell(word, meaning)) { // 빈칸에 추가 성공
                    dbHelper.insertWord(word, meaning, example, translation, date); // 다섯 개의 매개변수 전달
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "모든 빈칸이 채워졌습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "단어와 뜻을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 시
        dialogView.findViewById(R.id.DialogWordCancelButton).setOnClickListener(v -> dialog.dismiss());

        dialog.show(); // 팝업 띄우기
    }

    private void showExampleDialog(String word) {
        Cursor cursor = dbHelper.getWordByWord(word);

        if (cursor != null && cursor.moveToFirst()) {
            int exampleIndex = cursor.getColumnIndex("example");
            int translationIndex = cursor.getColumnIndex("translation");

            if (exampleIndex != -1 && translationIndex != -1) {
                String example = cursor.getString(exampleIndex);
                String translation = cursor.getString(translationIndex);

                String message = "예문: " + (example == null || example.isEmpty() ? "없음" : example) + "\n" +
                        "해석: " + (translation == null || translation.isEmpty() ? "없음" : translation);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("예문 및 해석")
                        .setMessage(message)
                        .setPositiveButton("확인", null)
                        .create();
                dialog.show();
            } else {
                Toast.makeText(this, "예문 또는 해석 컬럼이 데이터베이스에 없습니다.", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "해당 단어를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addWordCellClickListeners() {
        for (int i = 0; i < wordCells.size(); i++) {
            TextView wordCell = wordCells.get(i);
            wordCell.setOnClickListener(v -> {
                String word = wordCell.getText().toString();
                if (!word.isEmpty()) {
                    showExampleDialog(word);
                }
            });
        }
    }

    // 빈칸 채우기
    private boolean fillEmptyCell(String word, String meaning) {
        for (int i = 0; i < wordCount; i++) {
            if (wordCells.get(i).getText().toString().isEmpty()) {
                // 빈칸 발견 시 추가
                wordCells.get(i).setText(word);
                meaningCells.get(i).setText(meaning);
                return true; // 성공적으로 채웠음
            }
        }
        return false; // 모든 빈칸이 채워짐
    }

    public void onDateSelected(int year, int month, int day) {
        month++; // Calendar.MONTH는 0부터 시작
        todayDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);

        // 선택한 날짜에 맞는 단어 로드
        loadWordsFromDatabase();
    }


}