package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;

import java.util.*;

public class PointGenerator extends RootNoiseGenerator<PointNoiseDataContainer, PointNoiseData, Double>
        implements PointNoiseGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);

    private final double maxAmplitude;
    private final Random randomGenerator;
    private final long randomSeed;

    public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
        super(interpolationPoints);
        this.maxAmplitude = maxAmplitude;
        this.randomSeed = randomSeed;
        this.randomGenerator = new Random(randomSeed);
        LOGGER.debug("Created new " + toString());
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    @Override
    public String toString() {
        return "PointGenerator{" +
                "maxAmplitude=" + maxAmplitude +
                ", noiseInterpolationPointsCount=" + getNoiseInterpolationPointsCount() +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointGenerator that = (PointGenerator) o;
        return Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                Double.compare(that.getNoiseInterpolationPointsCount(), getNoiseInterpolationPointsCount()) == 0 &&
                randomSeed == that.randomSeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAmplitude, getNoiseInterpolationPointsCount(), randomSeed);
    }

    @Override
    protected List<PointNoiseData> generateNextSegment(Double previous, Double current) {
        double currentPos = 0.0;
        List<PointNoiseData> results = new ArrayList<>(getNoiseInterpolationPointsCount());
        while (currentPos < getNoiseInterpolationPointsCount()) {
            double relativePositionInSegment = currentPos / getNoiseInterpolationPointsCount();
            double interpolatedValue = Interpolation.linearWithFade(previous, current, relativePositionInSegment);
            results.add(new PointNoiseData(interpolatedValue * maxAmplitude));
            currentPos++;
        }
        return results;
    }

    @Override
    protected PointNoiseDataContainer getInContainer(List<PointNoiseData> data) {
        return new PointNoiseDataContainer(data);
    }

    @Override
    protected Double getNewBounds() {
        return randomGenerator.nextDouble();
    }
}
