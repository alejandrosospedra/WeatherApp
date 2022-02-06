package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private Button getRequestButton;
    private TextView weather_info;
    private String weather_text_info="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getRequestButton = (Button) findViewById(R.id.get_weather);
        weather_info = (TextView) findViewById(R.id.textView);

        getRequestButton.setOnClickListener(view -> {
            //We have to do a thread for a network petition for work in background(mandatory)
            Thread thread = new Thread(() -> {
                try {
                    weather_text_info=getWeather("https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=6e36078b02f257df49f83be2314b0b32");
                } catch (IOException e) {
                    weather_text_info = e.toString();
                }
            });
            thread.start();
            try {
                thread.join();
                weather_info.setText(weather_text_info);
            } catch (InterruptedException e) {
                weather_info.setText(e.toString());
            }
        });
    }

    protected String getWeather (String urlPetition) throws IOException{
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlPetition);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        for (String line; (line = reader.readLine()) != null; ) {
            result.append(line);
        }
        return result.toString();
    }

}