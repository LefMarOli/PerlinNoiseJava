package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;

import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class PointGenerator extends NoisePointGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);

    private final double maxAmplitude;
    private final int interpolationPoints;
    private final double segmentLength;
    private final Random randomGenerator;
    private final long randomSeed;
    private final Queue<Double> generated = new LinkedBlockingQueue<>();
    private double previousBound;

    public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater or equal to 4");
        }
        this.maxAmplitude = maxAmplitude;
        this.interpolationPoints = interpolationPoints;
        this.segmentLength = interpolationPoints + 2;
        this.randomSeed = randomSeed;
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
        LOGGER.debug("Created layer with max amplitude of " + maxAmplitude + " and " + interpolationPoints +
                " interpolation points.");
    }

    @Override
    public Double[] getNextPoints(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        while (generated.size() < count) {
            generateNextSegment();
        }

        Double[] results = new Double[count];
        for (int i = 0; i < count; i++) {
            results[i] = generated.poll();
        }
        return results;
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    @Override
    public String toString() {
        return "PointGenerator{" +
                "maxAmplitude=" + maxAmplitude +
                ", interpolationPoints=" + interpolationPoints +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointGenerator that = (PointGenerator) o;
        return Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                Double.compare(that.segmentLength, segmentLength) == 0 &&
                randomSeed == that.randomSeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAmplitude, segmentLength, randomSeed);
    }

    private void generateNextSegment() {
        double newBound = randomGenerator.nextDouble();
        double currentPos = 0.0;
        while (currentPos < segmentLength) {
            double relativePositionInSegment = currentPos / segmentLength;
            double interpolatedValue = Interpolation.linearWithFade(previousBound, newBound, relativePositionInSegment);
            generated.add(interpolatedValue * maxAmplitude);
            currentPos++;
        }
        previousBound = newBound;
    }
}
