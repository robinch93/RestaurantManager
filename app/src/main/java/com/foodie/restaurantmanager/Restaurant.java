package com.foodie.restaurantmanager;

public class Restaurant {
    public String r_id;
    public String name;
    public String email;
    public String phone;
    public String description;
    public String address;
    public String openhours;
    public String image;

    public Restaurant() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Restaurant(String r_id, String name, String email, String phone, String description, String address, String openhours, String image) {
        this.r_id = r_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.address = address;
        this.openhours = openhours;
        this.image = image;
    }
}
