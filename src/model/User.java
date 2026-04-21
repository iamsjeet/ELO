package model;

import java.util.HashMap;
import java.util.Map;
import util.Constants;

public class User {

    private int id;
    private String name;

    private Map<Integer, Double> ratings = new HashMap<>();
    private int interactions = 0;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public double getRating(int productId) {
        return ratings.getOrDefault(productId, Constants.INITIAL_RATING);
    }

    public void setRating(int productId, double rating) {
        ratings.put(productId, rating);
    }

    public void increment() {
        interactions++;
    }

    public int getInteractions() {
        return interactions;
    }

    // 🔥 ADDED GETTER (fixes your error)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}