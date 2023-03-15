package com.ascent.WeatherApp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ascent.WeatherApp.LocalDB.LocalDBHelper;

public class MainActivity extends AppCompatActivity {
 LocalDBHelper localDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        localDBHelper = new LocalDBHelper(this);
//        localDBHelper.StartWork();

    }
}