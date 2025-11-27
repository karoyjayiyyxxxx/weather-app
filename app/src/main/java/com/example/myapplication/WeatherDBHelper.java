package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 獨立的資料庫輔助類別，用於管理天氣資料。
 * 這麼做可以讓程式碼結構更清晰，避免在 Activity 中混雜資料庫邏輯。
 */
public class WeatherDBHelper extends SQLiteOpenHelper {
    // --- 資料庫設定 ---
    private static final String DB_NAME = "weather_history.db"; // 資料庫名稱
    private static final int DB_VERSION = 1; // 資料庫版本

    // --- 資料表和欄位常數 ---
    public static final String TABLE_NAME = "WeatherData";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_WEATHER = "weather";
    public static final String COLUMN_TEMP = "temperature";
    public static final String COLUMN_RAIN = "rain";
    public static final String COLUMN_FEEL = "feel";
    // 將時間戳欄位類型改為 INTEGER，用於儲存毫秒數
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // 建立資料表的 SQL 語法
    // 移除 DEFAULT CURRENT_TIMESTAMP，改由應用程式端插入 System.currentTimeMillis()
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CITY + " TEXT NOT NULL, " +
                    COLUMN_WEATHER + " TEXT, " +
                    COLUMN_TEMP + " TEXT, " +
                    COLUMN_RAIN + " TEXT, " +
                    COLUMN_FEEL + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER" + // 欄位類型改為 INTEGER
                    ");";

    public WeatherDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 執行 SQL 語法以建立資料表
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果資料庫版本更新，則刪除舊資料表並重建
        // **注意：這會清除所有舊資料！**
        // 如果你需要保留資料，則需要實作更複雜的資料遷移邏輯。
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}