package engine;

import model.Product;
import util.Constants;
import util.Logger;

public class EloEngine {

    private double expected(double a, double b) {
        return 1.0 / (1 + Math.pow(10, (b - a) / 400));
    }

    public void update(Product A, Product B, boolean A_wins) {

        if (A == null || B == null || A.getId() == B.getId()) return;

        double oldA = A.getRating();
        double oldB = B.getRating();

        double expA = expected(oldA, oldB);
        double expB = expected(oldB, oldA);

        double scoreA = A_wins ? 1 : 0;
        double scoreB = A_wins ? 0 : 1;

        double newA = oldA + Constants.K * (scoreA - expA);
        double newB = oldB + Constants.K * (scoreB - expB);

        newA = Math.max(500, Math.min(2500, newA));
        newB = Math.max(500, Math.min(2500, newB));

        A.updateRating(newA);
        B.updateRating(newB);

        Logger.log("GLOBAL | " + A.getName() + " vs " + B.getName() +
                " | Winner: " + (A_wins ? A.getName() : B.getName()) +
                " | " + oldA + "→" + newA + " , " + oldB + "→" + newB);
    }
}