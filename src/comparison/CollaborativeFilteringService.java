package comparison;

import model.Product;
import model.User;

import java.util.*;

public class CollaborativeFilteringService {

    public List<Product> recommend(User currentUser, List<User> users, List<Product> products) {

        Map<Integer, Double> score = new HashMap<>();

        for (Product p : products) {
            score.put(p.getId(), 0.0);
        }

        // Simple logic: average ratings from other users
        for (User u : users) {
            if (u == currentUser) continue;

            for (Product p : products) {
                double r = u.getRating(p.getId());
                score.put(p.getId(), score.get(p.getId()) + r);
            }
        }

        List<Product> result = new ArrayList<>(products);

        result.sort((a, b) -> Double.compare(score.get(b.getId()), score.get(a.getId())));

        return result;
    }
}