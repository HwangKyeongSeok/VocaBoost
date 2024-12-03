package com.example.vocaboost;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetupActivity extends AppCompatActivity {
    private CheckBox BeginnerCheck, IntermediateCheck, AdvancedCheck, CustomCheck;
    private EditText CustomWordInput;
    private Button SetupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 초기화
        BeginnerCheck = findViewById(R.id.BeginnerCheck);
        IntermediateCheck = findViewById(R.id.IntermediateCheck);
        AdvancedCheck = findViewById(R.id.AdvancedCheck);
        CustomCheck = findViewById(R.id.CustomCheck);
        CustomWordInput = findViewById(R.id.CustomWord);
        SetupButton = findViewById(R.id.SetupButton);

        CustomWordInput.setEnabled(false);
        setupExclusiveCheckBoxes();

        SetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedOption();
            }
        });
    }

    private void setupExclusiveCheckBoxes() {
        CheckBox[] checkBoxes = {BeginnerCheck, IntermediateCheck, AdvancedCheck, CustomCheck};

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 다른 체크박스의 선택 해제
                    for (CheckBox cb : checkBoxes) {
                        if (cb != buttonView) {
                            cb.setChecked(false);
                        }
                    }

                    // 사용자 설정 체크박스 선택 시 입력 필드 활성화
                    if (buttonView == CustomCheck) {
                        CustomWordInput.setEnabled(true); // 활성화
                        CustomWordInput.setFocusable(true);
                        CustomWordInput.setFocusableInTouchMode(true);
                    } else {
                        // 다른 옵션 선택 시 입력 필드 비활성화 및 초기화
                        CustomWordInput.setEnabled(false);
                        CustomWordInput.setText(""); // 입력값 초기화
                    }
                }
            });
        }
    }

    private void saveSelectedOption() {
        int wordCount = 0;

        if (BeginnerCheck.isChecked()) {
            wordCount = 10; // 초보자는 10개
        } else if (IntermediateCheck.isChecked()) {
            wordCount = 20; // 중급자는 20개
        } else if (AdvancedCheck.isChecked()) {
            wordCount = 30; // 고급자는 30개
        } else if (CustomCheck.isChecked()) {
            String input = CustomWordInput.getText().toString().trim();
            try {
                wordCount = Integer.parseInt(input);
                if (wordCount < 10 || wordCount > 30) {
                    Toast.makeText(this, "10~30사이의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "옵션을 골라주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 설정 완료 저장
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstRun", false); // 최초 설정 완료
        editor.putInt("WordCount", wordCount); // 단어 수 저장
        editor.apply();

        // MainActivity로 이동
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}