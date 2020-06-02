package org.lefmaroli.perlin.dimensional;

import org.apache.logging.log4j.LogManager;
import org.lefmaroli.perlin.RootNoiseGenerator;
import org.lefmaroli.perlin.data.NoiseData;
import org.lefmaroli.rounding.RoundUtils;

import java.util.Objects;

public abstract class MultiDimensionalRootNoiseGenerator<DataContainerType extends NoiseData, DataType>
        extends RootNoiseGenerator<DataContainerType, DataType> implements MultiDimensionalNoiseGenerator {

    protected static final int MB_10 = 10 * 1024 * 1024;
    protected static final int MB_10_IN_DOUBLES_SIZE = MB_10 / 8;

    private final boolean isCircular;

    public MultiDimensionalRootNoiseGenerator(int noiseInterpolationPoints, double maxAmplitude, long randomSeed,
                                              boolean isCircular) {
        super(noiseInterpolationPoints, maxAmplitude, randomSeed);
        this.isCircular = isCircular;
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MultiDimensionalRootNoiseGenerator<?, ?> that = (MultiDimensionalRootNoiseGenerator<?, ?>) o;
        return isCircular == that.isCircular;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isCircular);
    }

    protected int correctInterpolationPointsForCircularity(int interpolationPoints, int dimensionLength,
                                                           String dimensionName) {
        if (isCircular) {
            int newInterpolationPoints = RoundUtils.roundNToClosestFactorOfM(interpolationPoints, dimensionLength);
            LogManager.getLogger(this.getClass())
                    .warn("Modified required interpolation point count for " + dimensionName +
                            " from " + interpolationPoints + " to " + newInterpolationPoints +
                            " to respect circularity.");
            return newInterpolationPoints;
        } else {
            return interpolationPoints;
        }
    }
}
