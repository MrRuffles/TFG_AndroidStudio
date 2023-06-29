package com.example.tfg.db;

import org.bson.types.ObjectId;

public class Profile {
    private ObjectId _id;
    private String userId;
    private String name;
    private String email;
    private String telefono;
    private String localidad;

    public Profile(){}

    public Profile(ObjectId _id, String userId, String name, String email, String telefono, String localidad) {
        this._id = _id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.telefono = telefono;
        this.localidad = localidad;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
}
