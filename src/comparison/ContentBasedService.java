package comparison;

import model.Product;
import model.User;

import java.util.*;

public class ContentBasedService {

    public List<Product> recommend(User user, List<Product> products) {

        int bestProductId = -1;
        double bestRating = -1;

        for (Product p : products) {
            double r = user.getRating(p.getId());
            if (r > bestRating) {
                bestRating = r;
                bestProductId = p.getId();
            }
        }

        // 🔥 FIX: make final copy for lambda
        final int finalBestProductId = bestProductId;

        List<Product> result = new ArrayList<>(products);

        result.sort(Comparator.comparingInt(
                p -> Math.abs(p.getId() - finalBestProductId)
        ));

        return result;
    }
}