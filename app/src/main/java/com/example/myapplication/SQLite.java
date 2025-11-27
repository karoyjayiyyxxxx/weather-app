package com.example.myapplication;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class SQLite extends AppCompatActivity implements View.OnClickListener {

    private ImageView home;
    private Button btnDeleteAll;
    private TextView allDataTextView;
    private WeatherDBHelper dbHelper; // 使用獨立的 DB Helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        dbHelper = new WeatherDBHelper(this);

        home = findViewById(R.id.imageView4);
        btnDeleteAll = findViewById(R.id.btnViewAll); // 沿用舊按鈕 ID
        allDataTextView = findViewById(R.id.textViewAll);

        home.setOnClickListener(this);
        btnDeleteAll.setText("清除所有紀錄"); // 更改按鈕文字

        // 設定刪除按鈕的點擊事件
        btnDeleteAll.setOnClickListener(v -> showDeleteConfirmationDialog());

        // 頁面一啟動，就載入並顯示所有歷史紀錄
        loadAndShowAllHistory();
    }

    /**
     * 從資料庫讀取所有紀錄並顯示
     */
    private void loadAndShowAllHistory() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 查詢資料，並依照時間戳由新到舊排序
        Cursor cursor = db.query(WeatherDBHelper.TABLE_NAME,
                null, null, null, null, null,
                WeatherDBHelper.COLUMN_TIMESTAMP + " DESC");

        StringBuilder sb = new StringBuilder();

        if (cursor.getCount() == 0) {
            sb.append("目前沒有任何歷史紀錄。");
        } else {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String city = cursor.getString(cursor.getColumnIndex(WeatherDBHelper.COLUMN_CITY));
                @SuppressLint("Range") String weather = cursor.getString(cursor.getColumnIndex(WeatherDBHelper.COLUMN_WEATHER));
                @SuppressLint("Range") String temp = cursor.getString(cursor.getColumnIndex(WeatherDBHelper.COLUMN_TEMP));
                @SuppressLint("Range") String rain = cursor.getString(cursor.getColumnIndex(WeatherDBHelper.COLUMN_RAIN));
                @SuppressLint("Range") String feel = cursor.getString(cursor.getColumnIndex(WeatherDBHelper.COLUMN_FEEL));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(WeatherDBHelper.COLUMN_TIMESTAMP));

                sb.append("城市: ").append(city).append("\n")
                        .append("天氣: ").append(weather).append("\n")
                        .append("氣溫: ").append(temp).append("\n")
                        .append("降雨機率: ").append(rain).append("\n")
                        .append("體感: ").append(feel).append("\n")
                        .append("查詢時間: ").append(timestamp).append("\n")
                        .append("---------------------\n");
            }
        }
        cursor.close();
        db.close();

        allDataTextView.setText(sb.toString());
    }

    /**
     * 顯示刪除確認對話框
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("確認刪除")
                .setMessage("你確定要清除所有歷史紀錄嗎？這個操作無法復原。")
                .setPositiveButton("確定", (dialog, which) -> deleteAllHistory())
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 刪除所有歷史紀錄
     */
    private void deleteAllHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(WeatherDBHelper.TABLE_NAME, null, null);
        db.close();
        Toast.makeText(this, "所有紀錄已刪除", Toast.LENGTH_SHORT).show();
        loadAndShowAllHistory(); // 刷新顯示
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView4) {
            finish(); // 關閉此頁面，返回上一頁
        }
    }
}
