package com.foodie.restaurantmanager;

import java.io.Serializable;

public class Meal implements Serializable {
    private String m_id;
    private String menuImg;
    private String menuName;
    private String menuDesc;
    private Double menuPrice;
    private Integer menuQty;

    public Meal() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }
    // Constructor that is used to create an instance of the meal object
    public Meal(String m_id, String menuImg, String menuName, String menuDesc, Double menuPrice, Integer menuQty) {
        this.m_id = m_id;
        this.menuImg = menuImg;
        this.menuName = menuName;
        this.menuDesc = menuDesc;
        this.menuPrice = menuPrice;
        this.menuQty = menuQty;
    }

    public String getid(){ return m_id;}
    public String getmenuImg(){ return menuImg;}
    public String getmenuName(){ return menuName;}
    public String getmenuDesc(){ return menuDesc;}
    public Double getmenuPrice(){ return menuPrice;}
    public Integer getmenuQty(){ return menuQty;}

    public void setid(String m_id) { this.m_id = m_id;}
    public void setmenuImg(String menuImg) { this.menuImg = menuImg;}
    public void setmenuName(String menuName) { this.menuName = menuName;}
    public void setmenuDesc(String menuDesc) { this.menuDesc = menuDesc;}
    public void setmenuPrice(Double menuPrice) { this.menuPrice = menuPrice;}
    public void setmenuQty(Integer menuQty) { this.menuQty = menuQty;}


}
