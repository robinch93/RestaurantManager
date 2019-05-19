package com.foodie.restaurantmanager;

public class Rider {
    public String d_id;
    public String name;
    public String email;
    public String description;
    public String picturePath;
    public double lat;
    public double lon;
    public String token;


    public Rider() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Rider(String d_id, String name, String email, String description, String picturePath, double lat, double lon, String token) {
        this.d_id = d_id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.picturePath = picturePath;
        this.lat = lat;
        this.lon = lon;
        this.token = token;
    }
}
