package com.foodie.restaurantmanager;

public class Rating {
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
