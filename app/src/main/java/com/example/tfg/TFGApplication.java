package com.example.tfg;

import android.app.Application;

import com.example.tfg.db.DataBase;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TFGApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
