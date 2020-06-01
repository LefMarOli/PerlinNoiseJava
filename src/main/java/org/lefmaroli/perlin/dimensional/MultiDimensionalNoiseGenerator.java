package org.lefmaroli.perlin.dimensional;

import org.lefmaroli.rounding.RoundUtils;

public interface MultiDimensionalNoiseGenerator {
    boolean isCircular();

    default int correctInterpolationPointsForCircularity(int interpolationPoints, int dimensionLength) {
        if (!isCircular()) {
            return interpolationPoints;
        } else {
            return RoundUtils.roundNToClosestFactorOfM(interpolationPoints, dimensionLength);
        }
    }
}
