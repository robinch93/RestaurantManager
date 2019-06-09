package com.foodie.restaurantmanager;

import java.io.Serializable;

import java.util.ArrayList;

public class Order implements Serializable {
    public String o_id;
    public String c_id;
    public String r_id;
    public String d_id;
    public String notes;
    public Integer status;  // 0 = Created,  1= Prepared, 2= Delivering, 3= Completed
    // String[] statusString = { "Created", "Prepared", "Delivering", "Completed" };
    public ArrayList<Meal> items = new ArrayList<Meal>();
    public String customerName;
    public String orderTime;
    public String creationTime;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Order(String o_id, String c_id, String r_id,String d_id, String notes, Integer status, ArrayList<Meal> items, String customerName, String orderTime, String creationTime) {
        this.o_id = o_id;
        this.c_id = c_id;
        this.r_id = r_id;
        this.d_id = d_id;
        this.notes = notes;
        this.status = status;
        this.items = items;
        this.customerName = customerName;
        this.orderTime = orderTime;
        this.creationTime = creationTime;
    }
}