package org.lefmaroli.perlin2d;

import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.Vector2D;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PerlinLayer2D {

    private final double amplitudeFactor;
    private final int segmentLength;
    private final RandomGenerator randomGenerator;
    private final List<Queue<Double>> generated;
    private final int width;
    private final int randomBounds;
    private List<Vector2D> previousBounds;
    public static final double MAX_VALUE = Math.sqrt(2.0) / 2.0;

    PerlinLayer2D(int width, int interpolationPoints, double amplitudeFactor, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater or equal to 4");
        }
        this.amplitudeFactor = amplitudeFactor;
        this.segmentLength = interpolationPoints + 2;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.generated = new ArrayList<>(width);
        initializeGeneratedDataContainers(width);
        this.width = width;
        this.randomBounds = 2 + width / segmentLength;
        this.previousBounds = generateNewRandomBounds();
    }

    private void initializeGeneratedDataContainers(int width) {
        for (int i = 0; i < width; i++) {
            this.generated.add(new LinkedBlockingQueue<>());
        }
    }

    Vector<Vector<Double>> getNextXSlices(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        assertEnoughDataIsGenerated(count);
        return constructXSlicesFromGeneratedData(count);
    }

    private Vector<Vector<Double>> constructXSlicesFromGeneratedData(int count) {
        Vector<Vector<Double>> xSlices = new Vector<>(count);
        for (int i = 0; i < count; i++) {
            Vector<Double> ySlices = new Vector<>(width);
            for (int j = 0; j < width; j++) {
                ySlices.add(generated.get(j).poll());
            }
            xSlices.add(ySlices);
        }
        return xSlices;
    }

    private void assertEnoughDataIsGenerated(int count) {
        while (generated.get(0).size() < count) {
            generateNextSegment();
        }
    }

    private void generateNextSegment() {
        List<Vector2D> newBounds = generateNewRandomBounds();

        for (int yIndex = 0; yIndex < width; yIndex++) {
            int lowerBoundIndex = yIndex / width;
            Vector2D topLeftBound = previousBounds.get(lowerBoundIndex);
            Vector2D topRightBound = newBounds.get(lowerBoundIndex);
            Vector2D bottomLeftBound = previousBounds.get(lowerBoundIndex + 1);
            Vector2D bottomRightBound = newBounds.get(lowerBoundIndex + 1);

            int segmentYIndex = yIndex % segmentLength;
            double yDist = (double) (segmentYIndex) / segmentLength;

            //iteration through length
            for (int segmentXIndex = 0; segmentXIndex < segmentLength; segmentXIndex++) {

                double xDist = (double) (segmentXIndex) / segmentLength;

                Vector2D topLeftDistance = new Vector2D(xDist, yDist);
                Vector2D topRightDistance = new Vector2D(xDist - 1.0, yDist);
                Vector2D bottomLeftDistance = new Vector2D(xDist, yDist - 1.0);
                Vector2D bottomRightDistance = new Vector2D(xDist - 1.0, yDist - 1.0);

                double topLeftProduct = topLeftBound.getVectorProduct(topLeftDistance);
                double topRightProduct = topRightBound.getVectorProduct(topRightDistance);
                double bottomLeftProduct = bottomLeftBound.getVectorProduct(bottomLeftDistance);
                double bottomRightProduct = bottomRightBound.getVectorProduct(bottomRightDistance);

                double interpolatedValue =
                        interpolate2D(xDist, yDist, topLeftProduct, topRightProduct, bottomLeftProduct,
                                bottomRightProduct);
                double adjustedValue = adjustValueRange(interpolatedValue);
                double amplifiedValue = adjustedValue * amplitudeFactor;
                generated.get(yIndex).add(amplifiedValue);
            }
        }
        previousBounds = newBounds;
    }

    private double adjustValueRange(double interpolatedValue) {
        return ((interpolatedValue / MAX_VALUE) + 1.0) / 2.0;
    }

    private double interpolate2D(double xDist, double yDist, double topLeftProduct, double topRightProduct,
                                 double bottomLeftProduct, double bottomRightProduct) {
        double topInterpolation = Interpolation.linearWithFade(topLeftProduct, topRightProduct, xDist);
        double bottomInterpolation =
                Interpolation.linearWithFade(bottomLeftProduct, bottomRightProduct, xDist);
        return Interpolation.linearWithFade(topInterpolation, bottomInterpolation, yDist);
    }

    private List<Vector2D> generateNewRandomBounds() {
        List<Vector2D> newBounds = new ArrayList<>(randomBounds);
        for (int i = 0; i < randomBounds; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        return newBounds;
    }
}
