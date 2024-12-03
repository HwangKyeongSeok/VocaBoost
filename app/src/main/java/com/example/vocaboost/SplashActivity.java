package com.example.vocaboost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

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

        setTheme(R.style.Theme_Splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // SharedPreferences를 통해 최초 실행 여부 확인
                SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

                Intent intent;
                if (isFirstRun) {
                    // 최초 실행 시 설정 화면으로 이동
                    intent = new Intent(SplashActivity.this, SetupActivity.class);

                    // 최초 실행이 아님을 기록
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isFirstRun", false);
                    editor.apply();
                } else {
                    // 일반적인 메인 화면으로 이동
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }

                startActivity(intent);
                // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 화면 전환 효과 추가
                finish();
            }
        }, 2000); // 2초 지연
    }
}
