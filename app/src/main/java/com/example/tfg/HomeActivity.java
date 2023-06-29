package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.db.DataBase;
import com.example.tfg.db.Incidencia;

import org.bson.Document;
import org.bson.internal.Base64;

import java.util.ArrayList;
import java.util.List;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.AppException;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    String appId = "tfg-app-zsokw";
    App app;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        linearLayout = findViewById(R.id.linear_container);

        app = new App(new AppConfiguration.Builder(appId).build());

        SharedPreferences sharedPref = getSharedPreferences("appTFG", Context.MODE_PRIVATE);

        getIncidencias(sharedPref.getString("LOCALIDAD", "Madrid"));


        Button incidencia = findViewById(R.id.button_incidencia);
        incidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goIncidencia();
            }
        });
        ImageButton profile = findViewById(R.id.floatingActionButton);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goProfile();
            }
        });


    }

    public void getIncidencias(String loc){

        try{

            MongoClient mongodb = app.currentUser().getMongoClient("mongodb-atlas");

            MongoCollection collection = mongodb.getDatabase("TFG").getCollection("incidencia");
            Document filter = new Document("localidad", loc);
            RealmResultTask<MongoCursor<Document>> findTask = collection.find(filter).iterator();
            findTask.getAsync(task -> {
                if(task.isSuccess()){
                    MongoCursor<Document> result = task.get();
                    while (result.hasNext()){
                        Incidencia in = new Incidencia();
                        Document d = result.next();
                        in.setTitle(d.getString("titulo"));
                        in.setAddress(d.getString("direccion"));
                        in.setDescription(d.getString("descripcion"));
                        addCard(in);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "No hay incidencias en tu localidad.", Toast.LENGTH_SHORT).show();
                }
            });




        }catch(AppException error){
            Toast.makeText(getApplicationContext(), "No se ha podido encontrar incidencias, prueba de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goIncidencia(){
        Intent intentIncidencia = new Intent(HomeActivity.this, IncidenciaActivity.class);
        startActivity(intentIncidencia);
    }

    private void goProfile(){
        Intent intentProfile = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intentProfile);
    }

    private void addCard(Incidencia incidencia){
        View view = getLayoutInflater().inflate(R.layout.item_layout,null);

        TextView title = view.findViewById(R.id.item_Title);
        TextView address = view.findViewById(R.id.item_Address);
        TextView desc = view.findViewById(R.id.item_Description);

        title.setText(incidencia.getTitle());
        address.setText(incidencia.getAddress());
        desc.setText(incidencia.getDescription());

        linearLayout.addView(view);
    }
}