package model;

public class Product {

    private int id;
    private String name;
    private double rating;
    private String category;

    // 🔥 Constructor
    public Product(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = 1000; // default ELO
    }

    // 🔥 Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getCategory() {
        return category;
    }

    // 🔥 Used by ELO engine
    public void updateRating(double rating) {
        this.rating = rating;
    }

    // 🔥 Used by PersistenceManager (IMPORTANT)
    public void setRating(Double rating) {
        this.rating = rating;
    }
}