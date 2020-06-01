package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;

import java.util.*;

public class PointGenerator extends RootNoiseGenerator<PointNoiseDataContainer, PointNoiseData>
        implements PointNoiseGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);

    private final double maxAmplitude;
    private final Random randomGenerator;
    private final long randomSeed;
    private double previousBound;

    public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
        super(interpolationPoints);
        this.maxAmplitude = maxAmplitude;
        this.randomSeed = randomSeed;
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
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
                ", noiseInterpolationPoints=" + getNoiseInterpolationPoints() +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointGenerator that = (PointGenerator) o;
        return Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                Double.compare(that.getNoiseInterpolationPoints(), getNoiseInterpolationPoints()) == 0 &&
                randomSeed == that.randomSeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAmplitude, getNoiseInterpolationPoints(), randomSeed);
    }

    @Override
    protected List<PointNoiseData> generateNextSegment() {
        double newBound = randomGenerator.nextDouble();
        double currentPos = 0.0;
        List<PointNoiseData> results = new ArrayList<>(getNoiseInterpolationPoints());
        while (currentPos < getNoiseInterpolationPoints()) {
            double relativePositionInSegment = currentPos / getNoiseInterpolationPoints();
            double interpolatedValue = Interpolation.linearWithFade(previousBound, newBound, relativePositionInSegment);
            results.add(new PointNoiseData(interpolatedValue * maxAmplitude));
            currentPos++;
        }
        previousBound = newBound;
        return results;
    }

    @Override
    protected PointNoiseDataContainer getInContainer(List<PointNoiseData> data) {
        return new PointNoiseDataContainer(data);
    }
}
