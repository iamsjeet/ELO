package service;

import model.*;
import java.util.*;

public class RecommendationService {

    private ScoreCombiner combiner = new ScoreCombiner();

    public List<Product> recommend(User user, List<Product> products) {

        Map<Product, Double> scoreMap = new HashMap<>();

        for (Product p : products) {

            double personal = user.getRating(p.getId());
            double global = p.getRating();

            double score = combiner.combine(
                    personal, global, user.getInteractions()
            );

            scoreMap.put(p, score);
        }

        List<Product> sorted = new ArrayList<>(products);

        sorted.sort((a, b) ->
                Double.compare(scoreMap.get(b), scoreMap.get(a))
        );

        return sorted;
    }
}