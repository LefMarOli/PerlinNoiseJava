package org.lefmaroli.perlin2d;

import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.Vector2D;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PerlinNoise2D {

    private static final double MAX_VALUE = Math.sqrt(2.0) / 2.0;
    private final double amplitudeFactor;
    private final int segmentLength;
    private final RandomGenerator randomGenerator;
    private final List<Queue<Double>> generated;
    private int lineLength;
    private int randomBounds;
    private List<Vector2D> previousBounds = new ArrayList<>();

    PerlinNoise2D(int lineLength, int interpolationPoints, double amplitudeFactor, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater or equal to 4");
        }
        this.amplitudeFactor = amplitudeFactor;
        this.segmentLength = interpolationPoints + 2;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.generated = new ArrayList<>(lineLength);
        setLineLength(lineLength);
    }

    private void setLineLength(int lineLength){
        if(lineLength < 0){
            throw new IllegalArgumentException("Line length must be greater than 0");
        }
        this.lineLength = lineLength;
        this.randomBounds = 2 + lineLength / segmentLength;
        if(previousBounds.size() < randomBounds){
            previousBounds.addAll(generateNewRandomBounds(randomBounds - previousBounds.size()));
        }
        addGeneratedRows(lineLength);
    }

    Double[][] getNextLines(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        assertEnoughDataIsGenerated(count);
        return constructLinesFromGeneratedData(count);
    }

    private void addGeneratedRows(int width) {
        for (int i = 0; i < width; i++) {
            this.generated.add(new LinkedBlockingQueue<>());
        }
    }

    private Double[][] constructLinesFromGeneratedData(int count) {
        Double[][] newLines = new Double[count][lineLength];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < lineLength; j++) {
                Queue<Double> row = generated.get(j);
                newLines[i][j] = row.poll();
            }
        }
        return newLines;
    }

    private void assertEnoughDataIsGenerated(int count) {
        while (generated.get(0).size() < count) {
            generateNextSegment();
        }
    }

    private void generateNextSegment() {
        List<Vector2D> newBounds = generateNewRandomBounds(randomBounds);

        for (int yIndex = 0; yIndex < lineLength; yIndex++) {
            int lowerBoundIndex = yIndex / segmentLength;
            Vector2D topLeftBound = previousBounds.get(lowerBoundIndex);
            Vector2D topRightBound = newBounds.get(lowerBoundIndex);
            Vector2D bottomLeftBound = previousBounds.get(lowerBoundIndex + 1);
            Vector2D bottomRightBound = newBounds.get(lowerBoundIndex + 1);

            int segmentYIndex = yIndex % segmentLength;
            double yDist = (double) (segmentYIndex) / segmentLength;

            for (int segmentXIndex = 0; segmentXIndex < segmentLength; segmentXIndex++) {

                double xDist = (double) (segmentXIndex) / segmentLength;

                Vector2D topLeftDistance = new Vector2D(xDist, yDist);
                Vector2D topRightDistance = new Vector2D(xDist - 1.0, yDist);
                Vector2D bottomLeftDistance = new Vector2D(xDist, yDist - 1.0);
                Vector2D bottomRightDistance = new Vector2D(xDist - 1.0, yDist - 1.0);

                double topLeftBoundImpact = topLeftBound.getVectorProduct(topLeftDistance);
                double topRightBoundImpact = topRightBound.getVectorProduct(topRightDistance);
                double bottomLeftBoundImpact = bottomLeftBound.getVectorProduct(bottomLeftDistance);
                double bottomRightBoundImpact = bottomRightBound.getVectorProduct(bottomRightDistance);

                double interpolatedValue =
                        interpolate2D(xDist, yDist, topLeftBoundImpact, topRightBoundImpact, bottomLeftBoundImpact,
                                bottomRightBoundImpact);
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

    private List<Vector2D> generateNewRandomBounds(int count) {
        List<Vector2D> newBounds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        return newBounds;
    }
}
