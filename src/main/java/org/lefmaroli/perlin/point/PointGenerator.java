package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;

import java.util.*;

public class PointGenerator extends RootNoiseGenerator<PointNoiseDataContainer, PointNoiseData>
        implements PointNoiseGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);
    private final Random randomGenerator;
    private double previousBound;

    public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
        super(interpolationPoints, maxAmplitude, randomSeed);
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
        LOGGER.debug("Created new " + toString());
    }

    @Override
    public String toString() {
        return "PointGenerator{" +
                "noiseInterpolationPoints=" + getNoiseInterpolationPoints() +
                ", maxAmplitude=" + getMaxAmplitude() +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    protected List<PointNoiseData> generateNextSegment() {
        double newBound = randomGenerator.nextDouble();
        double currentPos = 0.0;
        List<PointNoiseData> results = new ArrayList<>(getNoiseInterpolationPoints());
        while (currentPos < getNoiseInterpolationPoints()) {
            double relativePositionInSegment = currentPos / getNoiseInterpolationPoints();
            double interpolatedValue = Interpolation.linearWithFade(previousBound, newBound, relativePositionInSegment);
            results.add(new PointNoiseData(interpolatedValue * getMaxAmplitude()));
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
