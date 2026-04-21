package engine;

import model.User;
import util.Constants;
import util.Logger;

public class PersonalizedEloEngine {

    private double expected(double a, double b) {
        return 1.0 / (1 + Math.pow(10, (b - a) / 400));
    }

    public void update(User user, int A, int B, boolean A_wins) {

        if (user == null || A == B) return;

        double oldA = user.getRating(A);
        double oldB = user.getRating(B);

        double expA = expected(oldA, oldB);
        double expB = expected(oldB, oldA);

        double scoreA = A_wins ? 1 : 0;
        double scoreB = A_wins ? 0 : 1;

        double newA = oldA + Constants.K * (scoreA - expA);
        double newB = oldB + Constants.K * (scoreB - expB);

        user.setRating(A, newA);
        user.setRating(B, newB);

        user.increment();

        Logger.log("USER " + user +
                " | " + A + " vs " + B +
                " | Winner: " + (A_wins ? A : B) +
                " | " + oldA + "→" + newA + " , " + oldB + "→" + newB);
    }
}