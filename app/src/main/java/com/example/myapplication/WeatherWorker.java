package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherWorker extends Worker {

    private static final String TAG = "WeatherWorker";

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String locationName = getInputData().getString("locationName");

        if (locationName == null || locationName.isEmpty()) {
            sendNotification("天氣提醒", "無法取得地名");
            return Result.failure();
        }

        String weatherInfo = fetchWeather(locationName);
        if (weatherInfo == null) {
            sendNotification("天氣提醒", locationName + " 天氣資料查詢失敗");
            return Result.failure();
        }

        sendNotification(locationName + " 天氣資訊", weatherInfo);
        return Result.success();
    }

    private String fetchWeather(String locationName) {
        String apiKey = "CWA-85A2365E-51D0-444E-910C-68D9FC19DA52";
        String urlString = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=" + apiKey;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray locations = json.getJSONObject("records").getJSONArray("location");

                for (int i = 0; i < locations.length(); i++) {
                    JSONObject loc = locations.getJSONObject(i);
                    if (locationName.equals(loc.getString("locationName"))) {
                        JSONArray elements = loc.getJSONArray("weatherElement");

                        String weather = "", minT = "", maxT = "";

                        for (int j = 0; j < elements.length(); j++) {
                            JSONObject el = elements.getJSONObject(j);
                            String name = el.getString("elementName");
                            JSONObject parameter = el.getJSONArray("time")
                                    .getJSONObject(0).getJSONObject("parameter");

                            switch (name) {
                                case "Wx":
                                    weather = parameter.getString("parameterName");
                                    break;
                                case "MinT":
                                    minT = parameter.getString("parameterName");
                                    break;
                                case "MaxT":
                                    maxT = parameter.getString("parameterName");
                                    break;
                            }
                        }

                        return String.format("%s，氣溫 %s°C ~ %s°C", weather, minT, maxT);
                    }
                }

                Log.w(TAG, "找不到地名: " + locationName);
                return null;
            } else {
                Log.e(TAG, "API 回應錯誤，代碼: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "讀取天氣資料錯誤", e);
            return null;
        }
    }

    private void sendNotification(String title, String message) {
        Context context = getApplicationContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "weather_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "天氣通知", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(1, builder.build());
    }
}
