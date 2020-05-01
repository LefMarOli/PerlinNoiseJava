package org.lefmaroli.randomgrid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;

import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class RandomLayer1D {

    private final double amplitudeFactor;
    private final double segmentLength;
    private final Random randomGenerator;
    private final Queue<Double> generated = new LinkedBlockingQueue<>();
    private double previousBound;

    RandomLayer1D(int interpolationPoints, double amplitudeFactor, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater or equal to 4");
        }
        this.amplitudeFactor = amplitudeFactor;
        this.segmentLength = interpolationPoints + 2;
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
    }

    Vector<Double> getNext(int count) {
        if(count < 1){
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        while (generated.size() < count) {
            generateNextSegment();
        }

        Vector<Double> results = new Vector<>(count);
        for (int i = 0; i < count; i++) {
            results.add(generated.poll());
        }
        return results;
    }

    private void generateNextSegment() {
        double newBound = randomGenerator.nextDouble();
        double currentPos = 0.0;
        while (currentPos < segmentLength) {
            generated.add(Interpolation.linearWithFade(previousBound, newBound, currentPos / segmentLength) *
                    amplitudeFactor);
            currentPos++;
        }
        previousBound = newBound;
    }
}
