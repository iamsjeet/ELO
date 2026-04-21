package service;

import util.Constants;

public class ScoreCombiner {

    public double combine(double personal, double global, int interactions) {

        if (interactions == 0) return global;

        if (interactions < 5)
            return 0.3 * personal + 0.7 * global;

        if (interactions < Constants.COLD_START_THRESHOLD)
            return 0.3 * personal + 0.7 * global;

        return Constants.PERSONAL_WEIGHT * personal +
               Constants.GLOBAL_WEIGHT * global;
    }
}