package simulation;

import model.*;
import engine.EloEngine;
import engine.PersonalizedEloEngine;  

import java.util.*;

public class InteractionSimulator {

    private EloEngine global = new EloEngine();
    private PersonalizedEloEngine personal = new PersonalizedEloEngine();
    private Random rand = new Random();

    public void simulate(User user, List<Product> products, int steps) {

        for (int i = 0; i < steps; i++) {

            Product A = products.get(rand.nextInt(products.size()));
            Product B = products.get(rand.nextInt(products.size()));

            if (A == B) continue;

            boolean A_wins = rand.nextBoolean();

            global.update(A, B, A_wins);
            personal.update(user, A.getId(), B.getId(), A_wins);
        }
    }
}