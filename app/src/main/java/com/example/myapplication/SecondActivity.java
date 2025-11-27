package com.example.myapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spr;
    private TextView text, text2, text3, text4, text5, text6,text8;
    private ImageView weatherIcon, home,weather ,tb;
    private WeatherDBHelper dbHelper; // 使用獨立的 DB Helper

    private Button b3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 初始化資料庫輔助類別
        dbHelper = new WeatherDBHelper(this);

        // 綁定所有 View
        text = findViewById(R.id.textView);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);
        text4 = findViewById(R.id.textView4);
        text5 = findViewById(R.id.textView5);
        text6 = findViewById(R.id.textView6);
        text8 = findViewById(R.id.textView8);
        home = findViewById(R.id.imageView2);
        weatherIcon = findViewById(R.id.imageView);
        weather = findViewById(R.id.imageView3);
        tb=findViewById(R.id.imageView8);
        spr = findViewById(R.id.spinner);
        b3=findViewById(R.id.button3);

        home.setOnClickListener(this);

        spr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 當 Spinner 的選項被選擇時，執行天氣查詢
                if (position > 0) { // 避免選擇提示項 ("請選擇縣市") 時觸發
                    String selectedCity = parent.getItemAtPosition(position).toString();
                    fetchWeather(selectedCity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 從中央氣象署 API 取得天氣資料
     * @param selectedCity 選擇的城市名稱
     */
    private void fetchWeather(String selectedCity) {
        String apiKey = "CWA-85A2365E-51D0-444E-910C-68D9FC19DA52";
        String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=" + apiKey + "&locationName=" + selectedCity;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // --- 解析 JSON 資料 ---
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray locations = jsonObject.getJSONObject("records").getJSONArray("location");
                        JSONObject location = locations.getJSONObject(0);
                        JSONArray weatherElements = location.getJSONArray("weatherElement");

                        String weatherDescription = "", maxTemp = "", minTemp = "", PoP = "", CI = "";

                        for (int i = 0; i < weatherElements.length(); i++) {
                            JSONObject element = weatherElements.getJSONObject(i);
                            String elementName = element.getString("elementName");
                            JSONObject parameter = element.getJSONArray("time").getJSONObject(0).getJSONObject("parameter");

                            switch (elementName) {
                                case "Wx":
                                    weatherDescription = parameter.getString("parameterName");
                                    break;
                                case "MaxT":
                                    maxTemp = parameter.getString("parameterName");
                                    break;
                                case "MinT":
                                    minTemp = parameter.getString("parameterName");
                                    break;
                                case "PoP":
                                    PoP = parameter.getString("parameterName");
                                    break;
                                case "CI":
                                    CI = parameter.getString("parameterName");
                                    break;
                            }
                        }

                        // --- 更新 UI 介面 ---
                        text.setText(selectedCity + " 現在天氣：");
                        text2.setText(weatherDescription);
                        text3.setText(minTemp + "°C ~ " + maxTemp + "°C");
                        text5.setText("降雨機率：" + PoP + "%");
                        text6.setText("體感：" + CI);
                        updateWeatherIcon(weatherDescription, PoP);

                        // --- 儲存資料到資料庫 ---
                        saveWeatherData(selectedCity, weatherDescription, minTemp + "°C ~ " + maxTemp + "°C", PoP + "%", CI);

                    } catch (JSONException e) {
                        Toast.makeText(this, "資料解析錯誤: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "網路錯誤: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    /**
     * 根據天氣描述和降雨機率更新天氣圖示
     */
    private void updateWeatherIcon(String description, String popString) {
        int popValue = 0;
        try {
            popValue = Integer.parseInt(popString);
        } catch (NumberFormatException e) {
            // 忽略轉換錯誤
        }

        if (description.contains("雷")) {
            weatherIcon.setImageResource(R.drawable.thunderstorm);
        } else if (description.contains("雨")) {
            weatherIcon.setImageResource(R.drawable.rain);
        } else if (popValue > 40) {
            weatherIcon.setImageResource(R.drawable.rain);
        } else if (description.contains("雲") || description.contains("陰")) {
            weatherIcon.setImageResource(R.drawable.cloud);
        } else {
            weatherIcon.setImageResource(R.drawable.sun_cloudy);
        }

        if (description.contains("雷")) {
            weather.setImageResource(R.drawable.thunder);
            text8.setText("出門請小心！建議待在室內");
            text8.setTextColor(0xFFFF0000);
            text8.setBackgroundColor(0xFFFFFFFF);
            tb.setVisibility(View.VISIBLE);
            tb.setImageResource(R.drawable.ly);
            b3.setVisibility(View.VISIBLE);
            text8.setVisibility(View.VISIBLE);

        } else if (description.contains("雨")) {
            weather.setImageResource(R.drawable.rainy);
            text8.setText("請記得帶雨具喔！");
            text8.setTextColor(0xFFFF0000);
            text8.setBackgroundColor(0xFFFFFFFF);
            tb.setVisibility(View.VISIBLE);
            tb.setImageResource(R.drawable.dy);
            b3.setVisibility(View.VISIBLE);
            text8.setVisibility(View.VISIBLE);
        } else if (popValue > 40) {
            weather.setImageResource(R.drawable.rainy);
            text8.setText("請記得帶雨具喔！");
            text8.setTextColor(0xFFFF0000);
            text8.setBackgroundColor(0xFFFFFFFF);
           //tb.setVisibility(View.VISIBLE);
            //tb.setImageResource(R.drawable.dy);
            b3.setVisibility(View.VISIBLE);
            text8.setVisibility(View.VISIBLE);

        } else if (description.contains("雲") || description.contains("陰")) {
            weather.setImageResource(R.drawable.cloudy);
            text8.setText("有可能會下雨喔");
            text8.setTextColor(0xFFFF0000);
            text8.setBackgroundColor(0xFFFFFFFF);
            //tb.setVisibility(View.VISIBLE);
            //tb.setImageResource(R.drawable.cf);
            b3.setVisibility(View.VISIBLE);
            text8.setVisibility(View.VISIBLE);
        } else {
            weather.setImageResource(R.drawable.sun);
            text8.setText("請注意防曬～");
            text8.setTextColor(0xFFFF0000);
            text8.setBackgroundColor(0xFFFFFFFF);
            tb.setVisibility(View.VISIBLE);
            tb.setImageResource(R.drawable.gw);
            b3.setVisibility(View.VISIBLE);
            text8.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 將天氣資料儲存到 SQLite 資料庫
     */
    private void saveWeatherData(String city, String weather, String temp, String rain, String feel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeatherDBHelper.COLUMN_CITY, city);
        values.put(WeatherDBHelper.COLUMN_WEATHER, weather);
        values.put(WeatherDBHelper.COLUMN_TEMP, temp);
        values.put(WeatherDBHelper.COLUMN_RAIN, rain);
        values.put(WeatherDBHelper.COLUMN_FEEL, feel);

        long newRowId = db.insert(WeatherDBHelper.TABLE_NAME, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, "查詢紀錄已儲存", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "儲存失敗", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        // 將「建議圖片」設為隱藏
        tb.setVisibility(View.GONE);
        // 【連動】同時，也將「關閉按鈕」自己藏起來
        b3.setVisibility(View.GONE);
        text8.setVisibility(View.GONE);

        if (v.getId() == R.id.imageView2) {
            // 返回主畫面
            finish();
        }
    }
}
