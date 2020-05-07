package org.lefmaroli.perlin1d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class PerlinNoise1D {

    private final static Logger LOGGER = LogManager.getLogger(PerlinNoise1D.class);

    private final double amplitudeFactor;
    private final double segmentLength;
    private final Random randomGenerator;
    private final Queue<Double> generated = new LinkedBlockingQueue<>();
    private double previousBound;

    public PerlinNoise1D(int interpolationPoints, double amplitudeFactor, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater or equal to 4");
        }
        this.amplitudeFactor = amplitudeFactor;
        this.segmentLength = interpolationPoints + 2;
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
        LOGGER.debug("Created layer with amplitude factor of " + amplitudeFactor + " and " + interpolationPoints +
                " interpolation points.");
    }

    Double[] getNext(int count) {
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

    private void generateNextSegment() {
        double newBound = randomGenerator.nextDouble();
        double currentPos = 0.0;
        while (currentPos < segmentLength) {
            double relativePositionInSegment = currentPos / segmentLength;
            double interpolatedValue = Interpolation.linearWithFade(previousBound, newBound, relativePositionInSegment);
            generated.add(interpolatedValue * amplitudeFactor);
            currentPos++;
        }
        previousBound = newBound;
    }
}
