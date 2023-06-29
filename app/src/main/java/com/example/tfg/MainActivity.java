package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.tfg.db.DataBase;
import com.example.tfg.db.Profile;

import org.bson.Document;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private User user;
    String appId = "tfg-app-zsokw";
    private Profile profile = new Profile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.log_email_input);
        password = findViewById(R.id.log_password_input);

        Button login = findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        Button register = findViewById(R.id.button_go_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegister();
            }
        });
    }

    public void login(){

        App app = new App(new AppConfiguration.Builder(appId).build());
        Credentials credentials = Credentials.emailPassword(email.getText().toString(),password.getText().toString());
        app.loginAsync(credentials, new App.Callback<User>(){

            @Override
            public void onResult(App.Result<User> result) {
                if(result.isSuccess()) {
                    user = result.get();

                    MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
                    MongoCollection collection = mongoClient.getDatabase("TFG").getCollection("profile");

                    Document filter = new Document("email", email.getText().toString());
                    RealmResultTask<Document> findTask = collection.findOne(filter);
                    findTask.getAsync(task -> {
                        if (task.isSuccess()){
                            SharedPreferences sharedPref = getSharedPreferences("appTFG", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("NAME", task.get().get("name").toString());
                            editor.putString("TELEFONO", task.get().get("telefono").toString());
                            editor.putString("LOCALIDAD", task.get().get("localidad").toString());
                            editor.putString("EMAIL", task.get().get("email").toString());
                            editor.apply();

                            Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intentHome);
                        }
                    });
                }
            }
        } );
    }

    public void goRegister(){
        Intent intentRegister = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intentRegister);
    }
}