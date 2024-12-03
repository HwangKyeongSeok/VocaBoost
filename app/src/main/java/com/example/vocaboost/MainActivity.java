package com.example.vocaboost;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button DateButton, TestButton, AddButton, EditButton, WordButton, MeanButton;

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

        DateButton = findViewById(R.id.DateButton);
        TestButton = findViewById(R.id.TestButton);
        AddButton = findViewById(R.id.AddButton);
        EditButton = findViewById(R.id.EditButton);
        WordButton = findViewById(R.id.WordButton);
        MeanButton = findViewById(R.id.MeanButton);

        // 전달받은 단어 갯수 가져오기 (기본값: 10)
        int wordCount = getIntent().getIntExtra("WORD_COUNT", 10);

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
            wordCell.setText(i);
            wordCell.setPadding(16, 16, 16, 16);//얘는 보고 지우기
            wordCell.setTextSize(20);
            wordCell.setGravity(Gravity.CENTER);
            wordCell.setBackgroundResource(R.drawable.right_bottom_border);
            wordCell.setLayoutParams(Params);

            TextView meaningCell = new TextView(this);
            meaningCell.setText(i);
            meaningCell.setPadding(16, 16, 16, 16); //얘는 보고 지우기
            meaningCell.setTextSize(20);
            meaningCell.setGravity(Gravity.CENTER); // 올바른 셀에 적용
            meaningCell.setBackgroundResource(R.drawable.bottom_only_border);
            meaningCell.setLayoutParams(Params);

            // 행에 셀 추가
            row.addView(wordCell);
            row.addView(meaningCell);

            // 컨테이너에 행 추가
            container.addView(row);
        }
    }
}