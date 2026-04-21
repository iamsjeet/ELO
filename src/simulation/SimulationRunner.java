package simulation;

import model.*;
import service.*;

import java.util.*;

public class SimulationRunner {

    public void run(List<User> users, List<Product> products) {

        InteractionSimulator sim = new InteractionSimulator();
        RecommendationService rec = new RecommendationService();

        for (User user : users) {

            sim.simulate(user, products, 80);

            List<Product> recommendations = rec.recommend(user, products);

            System.out.println("\nUser Recommendations:");
            for (int i = 0; i < 3; i++) {
                System.out.println(recommendations.get(i).getName());
            }
        }
    }
}