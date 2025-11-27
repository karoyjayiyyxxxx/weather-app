package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn,sqlite,staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        btn = findViewById(R.id.button);
        sqlite = findViewById(R.id.button2);
        staff = findViewById(R.id.button4);
        btn.setOnClickListener(this);
        sqlite.setOnClickListener(this);
        staff.setOnClickListener(this);

        // 直接傳台中市名稱給 Worker，啟動天氣查詢
        startWeatherWorkerWithLocationName("臺中市");
    }

    private void startWeatherWorkerWithLocationName(String locationName) {
        Data data = new Data.Builder()
                .putString("locationName", locationName)
                .build();

        OneTimeWorkRequest weatherWorkRequest =
                new OneTimeWorkRequest.Builder(WeatherWorker.class)
                        .setInputData(data)
                        .build();

        WorkManager.getInstance(this).enqueue(weatherWorkRequest);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.button2){
            Intent intent = new Intent(MainActivity.this, SQLite.class);
            startActivity(intent);
        }else if(v.getId() == R.id.button4){
            Intent intent = new Intent(MainActivity.this, Staff.class);
            startActivity(intent);
        }
    }
}
