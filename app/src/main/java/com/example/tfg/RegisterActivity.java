package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.db.DataBase;
import com.example.tfg.db.Profile;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText reppass;
    private EditText name;
    private EditText telefono;
    private EditText localidad;
    private TextView error;
    String appId = "tfg-app-zsokw";
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.reg_email_input);
        password = findViewById(R.id.reg_password_input);
        reppass = findViewById(R.id.reg_reppass_input);
        name = findViewById(R.id.reg_name_input);
        telefono = findViewById(R.id.reg_telefono_input);
        localidad = findViewById(R.id.reg_localidad_input);
        app = new App(new AppConfiguration.Builder(appId).build());

        Button registro = findViewById(R.id.button_register);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    public void register(){
        if(password.getText().toString().equals(reppass.getText().toString())){
            app.getEmailPassword().registerUserAsync(email.getText().toString(),password.getText().toString(), res ->{
                if(res.isSuccess()){
                    Credentials credentials = Credentials.emailPassword(email.getText().toString(),password.getText().toString());
                    this.app.loginAsync(credentials, new App.Callback<User>(){

                        @Override
                        public void onResult(App.Result<User> result) {
                            if(result.isSuccess()) {
                                MongoClient mongodb = app.currentUser().getMongoClient("mongodb-atlas");
                                MongoCollection collection = mongodb.getDatabase("TFG").getCollection("profile");

                                collection.insertOne(new Document().append("userID", app.currentUser().getId()).append("name", name.getText().toString()).append("telefono", telefono.getText().toString()).append("localidad", localidad.getText().toString()).append("email", email.getText().toString())).getAsync(
                                        task -> {
                                            if(task.isSuccess()){
                                                Intent intentHome = new Intent(RegisterActivity.this, HomeActivity.class);
                                                startActivity(intentHome);
                                            }
                                        }
                                );
                            }
                        }
                    });
                }
                else{
                    error.setText("Ha habido un error al registrarte.");
                }
            });
        }else{
            error.setText("Las contraseñas no son iguales");
        }

    }
}
