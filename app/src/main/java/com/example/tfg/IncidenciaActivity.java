package com.example.tfg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tfg.db.DataBase;
import com.example.tfg.db.LocationTrack;

import org.bson.Document;
import org.bson.internal.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;

public class IncidenciaActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    private ImageButton buttonSpeak;
    private List<String> images = new ArrayList<>();
    private ImageButton buttonImage;
    private Button submit;
    private Button cancel;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private EditText title;
    private TextView address;
    private TextView latitude;
    private TextView longitude;
    private EditText description;
    private TextView error;
    String locality;
    private Integer cont;

    String appId = "tfg-app-zsokw";
    App app;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia);
        cont = 1;

        image1 = findViewById(R.id.img_1);
        image2 = findViewById(R.id.img_2);
        image3 = findViewById(R.id.img_3);

        buttonSpeak = findViewById(R.id.imageButtonDesc);
        buttonImage = findViewById(R.id.imageButtonGrid);
        submit = findViewById(R.id.button_submit);
        cancel = findViewById(R.id.button_cancel);
        title = findViewById(R.id.title_label1);
        address = findViewById(R.id.address_label1);
        latitude = findViewById(R.id.lat_label1);
        longitude = findViewById(R.id.long_label1);
        description = findViewById(R.id.editTextTextMultiLine);
        locality = "";

        app = new App(new AppConfiguration.Builder(appId).build());


        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        if(ContextCompat.checkSelfPermission(IncidenciaActivity.this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(IncidenciaActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        locationTrack = new LocationTrack(IncidenciaActivity.this);

        if (locationTrack.canGetLocation()) {


            double longi = locationTrack.getLongitude();
            double latit = locationTrack.getLatitude();

            latitude.setText(Double.toString(latit));
            longitude.setText(Double.toString(longi));

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocation(latit,longi,1);
                address.setText(addresses.get(0).getAddressLine(0));
                locality = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cont <= 3){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
            }
        });

        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    description.setText("");
                }catch (ActivityNotFoundException error){
                    Toast.makeText(getApplicationContext(), "Tu dispositivo no soporta la conversion Voz a Texto", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIncidencia();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelIncidencia();
            }
        });


    }

    public void createIncidencia(){
        String im1 = "";
        String im2 = "";
        String im3 = "";
        for(int i = 0; i < images.size(); i++){
            String ss;

            switch (i+1){
                case 1:
                    ss = convertBitmaptoBase64(((BitmapDrawable)image1.getDrawable()).getBitmap());
                    im1 = ss;
                    break;
                case 2:
                    ss = convertBitmaptoBase64(((BitmapDrawable)image2.getDrawable()).getBitmap());
                    im2 = ss;
                    break;
                case 3:
                    ss = convertBitmaptoBase64(((BitmapDrawable)image3.getDrawable()).getBitmap());
                    im3 = ss;
                    break;
            }
        }

        MongoClient mongodb = app.currentUser().getMongoClient("mongodb-atlas");
        MongoCollection collection = mongodb.getDatabase("TFG").getCollection("incidencia");

        collection.insertOne(new Document().append("userID", app.currentUser().getId()).append("titulo", title.getText().toString()).append("descripcion", description.getText().toString()).append("direccion", address.getText().toString()).append("localidad", locality).append("latitud", latitude.getText().toString()).append("longitud", longitude.getText().toString()).append("imagen1", im1).append("imagen2", im2).append("imagen3", im3)).getAsync(
                task -> {
                    if(task.isSuccess()){
                        Toast.makeText(getApplicationContext(),"Incidencia subida", Toast.LENGTH_SHORT).show();
                        Intent backIntent = new Intent(IncidenciaActivity.this, HomeActivity.class);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            startActivity(backIntent);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Ha habido un error al mandar la incidencia", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    public void cancelIncidencia(){
        Intent backIntent = new Intent(IncidenciaActivity.this, HomeActivity.class);
        startActivity(backIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:
                images.add(String.valueOf(data.getData()));
                printImage(data);
                break;
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    description.setText(text.get(0));
                }
                break;
        }
    }

    public String convertBitmaptoBase64(Bitmap b){
        ByteArrayOutputStream ba=new ByteArrayOutputStream(  );
        b.compress( Bitmap.CompressFormat.PNG,90,ba );
        byte[] by=ba.toByteArray();

        return Base64.encode(by);
    }

    private void printImage(Intent data){
        Bitmap image = (Bitmap) data.getExtras().get("data");
        switch (cont){
            case 1:
                image1.setImageBitmap(image);
                cont++;
                break;
            case 2:
                image2.setImageBitmap(image);
                cont++;
                break;
            case 3:
                image3.setImageBitmap(image);
                cont++;
                break;
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String)perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(IncidenciaActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
}


