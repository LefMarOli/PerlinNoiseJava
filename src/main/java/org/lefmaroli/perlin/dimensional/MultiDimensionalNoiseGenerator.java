package org.lefmaroli.perlin.dimensional;

import org.lefmaroli.rounding.RoundUtils;

public interface MultiDimensionalNoiseGenerator {
    boolean isCircular();

    default int correctInterpolationPointsForCircularity(int interpolationPoints, int dimensionLength) {
        return RoundUtils.roundNToClosestFactorOfM(interpolationPoints, dimensionLength);
    }
}
