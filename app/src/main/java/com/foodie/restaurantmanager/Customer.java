package com.foodie.restaurantmanager;

import java.io.Serializable;

public class Customer implements Serializable {
    public String c_id;
    public String name;
    public String email;
    public String description;
    public String address;
    public String image;
    public String phone;
    public String token;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Customer(String c_id, String name, String email, String description, String address, String image,String token, String phone) {
        this.c_id = c_id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.address = address;
        this.image = image;
        this.token = token;
        this.phone = phone;
    }
}
