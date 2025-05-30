package com.example.vocaboost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferences에서 최초 실행 여부 확인
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        // 5초 후 다음 화면으로 이동
        new Handler().postDelayed(() -> {
            Intent intent;
            if (isFirstRun) {
                // 최초 실행 시 SetupActivity로 이동
                intent = new Intent(SplashActivity.this, SetupActivity.class);

                // 최초 실행 완료 기록
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isFirstRun", false);
                editor.apply();
            } else {
                // 일반 실행 시 MainActivity로 이동
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // 스플래시 액티비티 종료
        }, 1500); // 1초 대기
    }
}
