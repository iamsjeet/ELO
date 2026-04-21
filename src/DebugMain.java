import model.*;
import engine.*;
import service.*;
import persistence.PersistenceManager;
import comparison.ContentBasedService;
import comparison.CollaborativeFilteringService;

import java.util.*;

public class ConsoleMain {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // 🔥 USERS
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            users.add(new User(i, "User_" + i));
        }

        // 🔥 PRODUCTS
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            products.add(new Product(i, "Product_" + i));
        }

        // 🔥 LOAD DATA
        PersistenceManager.loadGlobal(products);
        PersistenceManager.loadUsers(users);

        // 🔥 LOGIN
        System.out.println("==== LOGIN ====");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName());
        }

        System.out.print("Select user (1-5): ");
        int userChoice = sc.nextInt();

        if (userChoice < 1 || userChoice > 5) {
            System.out.println("Invalid user.");
            return;
        }

        User user = users.get(userChoice - 1);
        System.out.println("Logged in as: " + user.getName());

        // 🔥 SERVICES
        EloEngine global = new EloEngine();
        PersonalizedEloEngine personal = new PersonalizedEloEngine();
        RecommendationService rec = new RecommendationService();

        ContentBasedService contentService = new ContentBasedService();
        CollaborativeFilteringService cfService = new CollaborativeFilteringService();

        while (true) {

            System.out.println("\n==== RECOMMENDATIONS ====");

            // 🔥 ELO
            List<Product> recommendations = rec.recommend(user, products);

            for (int i = 0; i < 3; i++) {
                Product p = recommendations.get(i);
                System.out.println((i + 1) + ". " + p.getName() +
                        " (ELO: " + String.format("%.2f", p.getRating()) + ")");
            }

            // 🔥 CONTENT-BASED
            List<Product> contentRec = contentService.recommend(user, products);

            System.out.println("\n[Content-Based]");
            for (int i = 0; i < 3; i++) {
                System.out.println((i + 1) + ". " + contentRec.get(i).getName());
            }

            // 🔥 COLLABORATIVE
            List<Product> cfRec = cfService.recommend(user, users, products);

            System.out.println("\n[Collaborative Filtering]");
            for (int i = 0; i < 3; i++) {
                System.out.println((i + 1) + ". " + cfRec.get(i).getName());
            }

            // 🔥 INPUT
            System.out.println("\nSelect a product (1/2/3) OR 0 to exit:");
            int choice = sc.nextInt();

            if (choice == 0) {
                System.out.println("Session ended.");
                break;
            }

            if (choice < 1 || choice > 3) {
                System.out.println("Invalid choice.");
                continue;
            }

            Product selected = recommendations.get(choice - 1);

            // 🔥 UPDATE ELO
            for (int i = 0; i < 3; i++) {
                if (i == choice - 1) continue;

                Product other = recommendations.get(i);

                global.update(selected, other, true);
                personal.update(user, selected.getId(), other.getId(), true);
            }

            System.out.println("\nYou selected: " + selected.getName());

            // 🔥 SAVE
            PersistenceManager.saveGlobal(products);
            PersistenceManager.saveUsers(users);

            // 🔥 SHOW UPDATED
            System.out.println("---- UPDATED GLOBAL RATINGS ----");
            for (Product p : products) {
                System.out.println(p.getName() + " → " +
                        String.format("%.2f", p.getRating()));
            }
        }

        sc.close();
    }
}