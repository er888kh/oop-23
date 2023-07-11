package com.example.snapfoodgui;

import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

@EdgeDBType
public class Rating {
    private static final Logger logger = LoggerFactory.getLogger(Rating.class);
    public Food food;
    public Double rating;

    public User user;

    public void setFood(Food food) {
        this.food = food;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
