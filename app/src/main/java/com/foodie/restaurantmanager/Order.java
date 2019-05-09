package com.foodie.restaurantmanager;

import java.io.Serializable;

public class Order implements Serializable {
    private Integer orderID;
    private String customerName;
    private String status;
    private String notes;
    private String lunchTime;
//    private JSONArray meals;
    private String meals;

    // Constructor that is used to create an instance of the meal object
    public Order(Integer orderID, String customerName, String status, String notes, String lunchTime, String meals) {
        this.orderID = orderID;
        this.customerName = customerName;
        this.status = status;
        this.notes = notes;
        this.lunchTime = lunchTime;
        this.meals = meals;
    }

    public Integer getOrderID(){ return orderID;}
    public String getcustomerName(){ return customerName;}
    public String getstatus(){ return status;}
    public String getnotes(){ return notes;}
    public String getlunchTime(){ return lunchTime;}
    public String getmeals(){ return meals;}

    public void setOrderID(Integer orderID) { this.orderID = orderID;}
    public void setcustomerName(String customerName) { this.customerName = customerName;}
    public void setstatus(String status) { this.status = status;}
    public void setnotes(String notes) { this.notes = notes;}
    public void setlunchTime(String lunchTime) { this.lunchTime = lunchTime;}
    public void setmeals(String meals) { this.meals = meals;}


}
