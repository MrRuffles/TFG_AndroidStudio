package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView tel;
    private TextView loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        name = findViewById(R.id.name_label1);
        email = findViewById(R.id.email_label1);
        loc = findViewById(R.id.loc_label1);
        tel = findViewById(R.id.tel_label1);

        SharedPreferences sharedPref = getSharedPreferences("appTFG", Context.MODE_PRIVATE);
        name.setText(sharedPref.getString("NAME",""));
        email.setText(sharedPref.getString("EMAIL",""));
        loc.setText(sharedPref.getString("LOCALIDAD",""));
        tel.setText(sharedPref.getString("TELEFONO",""));

        Button logOutBtn = findViewById(R.id.profile_logout);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

    }

    private void logOut(){
        Intent intentLogOut = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intentLogOut);
    }
}
