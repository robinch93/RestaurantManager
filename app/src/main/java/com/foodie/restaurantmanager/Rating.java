package com.foodie.restaurantmanager;

import java.io.Serializable;

public class Rating implements Serializable {
    public Float stars;
    public String comment;

    public Rating() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Rating(Float stars, String comment) {
        this.stars = stars;
        this.comment = comment;
    }
}
