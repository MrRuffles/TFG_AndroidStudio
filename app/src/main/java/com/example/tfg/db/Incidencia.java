package com.example.tfg.db;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

public class Incidencia {

    @BsonProperty("_id")
    private ObjectId id;
    @BsonProperty("userId")
    private String userId;
    @BsonProperty("titulo")
    private String title;
    @BsonProperty("descripcion")
    private String description;
    @BsonProperty("direccion")
    private String address;
    @BsonProperty("localidad")
    private String locality;
    @BsonProperty("latitud")
    private BigDecimal latitude;
    @BsonProperty("longitud")
    private BigDecimal longitude;
    @BsonProperty("imagen1")
    private String image1;
    @BsonProperty("imagen2")
    private String image2;
    @BsonProperty("imagen3")
    private String image3;

    // empty constructor required for MongoDB Data Access POJO codec compatibility
    public Incidencia() {}
    public Incidencia(ObjectId id, String userId,String title, String description, String address, String locality, BigDecimal latitude, BigDecimal longitude, String image1, String image2, String image3) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.address = address;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }

    public ObjectId get_Id() {
        return id;
    }

    public void set_Id(ObjectId id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }
}

