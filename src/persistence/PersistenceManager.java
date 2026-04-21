package persistence;

import model.Product;
import model.User;

import java.io.*;
import java.util.*;

public class PersistenceManager {

    private static final String GLOBAL_FILE = "global_ratings.txt";
    private static final String USER_FILE = "user_ratings.txt";

    // 🔥 SAVE GLOBAL PRODUCT RATINGS
    public static void saveGlobal(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GLOBAL_FILE))) {
            for (Product p : products) {
                bw.write(p.getId() + "," + p.getRating());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving global ratings");
        }
    }

    // 🔥 LOAD GLOBAL PRODUCT RATINGS
    public static void loadGlobal(List<Product> products) {
        File file = new File(GLOBAL_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            Map<Integer, Double> map = new HashMap<>();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                map.put(Integer.parseInt(parts[0]), Double.parseDouble(parts[1]));
            }

            for (Product p : products) {
                if (map.containsKey(p.getId())) {
                    p.setRating(map.get(p.getId()));
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading global ratings");
        }
    }

    // 🔥 SAVE USER PERSONAL RATINGS
    public static void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {

            for (User u : users) {
                for (int i = 1; i <= 8; i++) {
                    bw.write(u.getName() + "," + i + "," + u.getRating(i));
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving user ratings");
        }
    }

    // 🔥 LOAD USER PERSONAL RATINGS
    public static void loadUsers(List<User> users) {
        File file = new File(USER_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                String name = parts[0];
                int productId = Integer.parseInt(parts[1]);
                double rating = Double.parseDouble(parts[2]);

                for (User u : users) {
                    if (u.getName().equals(name)) {
                        u.setRating(productId, rating);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading user ratings");
        }
    }
}