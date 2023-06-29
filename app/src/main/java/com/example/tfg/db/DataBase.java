package com.example.tfg.db;

import android.content.Context;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.AppException;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;


public class DataBase {
    private Profile profile = new Profile();
    private User user;
    public static String appId = "tfg-app-zsokw";
    private static DataBase instance=new DataBase();
    public static DataBase getInstance(){
        return instance;
    }


    public List<Incidencia> getIncidenciasbyLocation(Context context,String location){
        List<Incidencia> incidenciaList = new ArrayList<>();
        try{
            App app = new App(new AppConfiguration.Builder(appId).build());

            MongoClient mongodb = app.currentUser().getMongoClient("mongodb-atlas");

            MongoCollection collection = mongodb.getDatabase("TFG").getCollection("incidencia");
            Document filter = new Document("localidad", location);
            RealmResultTask<MongoCursor<Incidencia>> findTask = collection.find(filter).iterator();
            findTask.getAsync(task -> {
                if(task.isSuccess()){
                    MongoCursor<Incidencia> result = task.get();
                    while (result.hasNext()){
                        Incidencia in = result.next();
                        incidenciaList.add(in);
                    }
                }
            });

            return incidenciaList;



        }catch(AppException error){
            System.console().printf("No se ha podido encontrar incidencias, prueba de nuevo.");
            return null;
        }
    }

    public Profile getProfilebyEmail(Context context, String email){

        try{
            App app = new App(new AppConfiguration.Builder(appId).build());
            MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");

            MongoCollection collection = mongoClient.getDatabase("TFG").getCollection("profile");
            Document filter = new Document("email", email);
            RealmResultTask<Profile> findTask = collection.findOne(filter);
            findTask.getAsync(task -> {
                if (task.isSuccess()){
                    this.profile = task.get();
                }
            });

            return profile;
        }catch (AppException error){
            System.console().printf("No se ha podido encontrar el perfil.");
            return null;
        }
    }


}

