package com.ascent.WeatherApp;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ascent.WeatherApp.LocalDB.LocalDBHelper;
import com.ascent.WeatherApp.Utils.CurrentDate;
import com.ascent.WeatherApp.model.UserModel;

public class LoginPage extends AppCompatActivity {


    private Button btn_login;
    private TextView txt_date;

    private Service service;
    private com.google.android.material.textfield.TextInputEditText username, password;
    private LocalDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        txt_date = (TextView) findViewById(R.id.txt_date);
        btn_login = findViewById(R.id.btn_login);
        username = (com.google.android.material.textfield.TextInputEditText) findViewById(R.id.txt_username);
        password = (com.google.android.material.textfield.TextInputEditText) findViewById(R.id.txt_password);

        String usernameInp = username.getText().toString();
        String passwordInp = password.getText().toString();

        db = new LocalDBHelper(this);

        CurrentDate c = new CurrentDate();
        txt_date.setText(c.currentDate());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.StartWork();
                if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "All fields are required !", Toast.LENGTH_LONG).show();

                } else {
//                        Get the saved username and password on local db
                    UserModel user = new UserModel();
                    user = db.getUser();
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>USER<<<<<<<<<<<<<<<<<<<<<<<");
                    System.out.println(db.getUser());
                    System.out.println("username to: " + user.getUsername().toString() );
                    System.out.println("password to: " + user.getPassword().toString() );
                    if (username.getText().toString().equals(user.getUsername().toString()) && password.getText().toString().equals(user.getPassword().toString())) {

                        Intent in = new Intent(LoginPage.this, weatherForecast.class);
                        startActivity(in);

                    } else {
                        System.out.println("Invalid credentials");
                    }
                }
            }
        });
    }


}