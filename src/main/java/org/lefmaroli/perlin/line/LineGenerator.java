package org.lefmaroli.perlin.line;

import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.Vector2D;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class LineGenerator extends NoiseLineGenerator implements BasicLineNoiseGenerator{

    private static final double MAX_2D_VECTOR_PRODUCT_VALUE = Math.sqrt(2.0) / 2.0;

    private final double maxAmplitude;
    private final int lineInterpolationPoints;
    private final int noiseInterpolationPoints;
    private final int lineSegmentLength;
    private final int noiseSegmentLength;
    private final long randomSeed;
    private final RandomGenerator randomGenerator;
    private final List<Queue<Double>> generated;
    private final int lineLength;
    private final int randomBounds;
    private List<Vector2D> previousBounds;

    public LineGenerator(int lineLength, int lineInterpolationPoints, int noiseInterpolationPoints,
                         double maxAmplitude, long randomSeed) {
        if (lineInterpolationPoints < 0) {
            throw new IllegalArgumentException("Line interpolation points must be greater than 0");
        }if (noiseInterpolationPoints < 0) {
            throw new IllegalArgumentException("Noise interpolation points must be greater than 0");
        }

        if (lineLength < 0) {
            throw new IllegalArgumentException("Line length must be greater than 0");
        }
        this.maxAmplitude = maxAmplitude;
        this.lineInterpolationPoints = lineInterpolationPoints;
        this.lineSegmentLength = lineInterpolationPoints + 2;
        this.noiseInterpolationPoints = noiseInterpolationPoints;
        this.noiseSegmentLength = noiseInterpolationPoints + 2;
        this.randomSeed = randomSeed;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.generated = new ArrayList<>(lineLength);
        this.lineLength = lineLength;
        this.randomBounds = 2 + lineLength / lineSegmentLength;
        this.previousBounds = new ArrayList<>(generateNewRandomBounds(randomBounds));

        addGeneratedRows(lineLength);
    }

    @Override
    public Double[][] getNextLines(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        assertEnoughDataIsGenerated(count);
        return constructLinesFromGeneratedData(count);
    }

    @Override
    public int getLineLength() {
        return lineLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineGenerator that = (LineGenerator) o;
        return Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                lineInterpolationPoints == that.lineInterpolationPoints &&
                noiseInterpolationPoints == that.noiseInterpolationPoints &&
                randomSeed == that.randomSeed &&
                lineLength == that.lineLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAmplitude, lineInterpolationPoints, noiseInterpolationPoints, randomSeed, lineLength);
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    @Override
    public String toString() {
        return "LineGenerator{" +
                "maxAmplitude=" + maxAmplitude +
                ", lineInterpolationPoints=" + lineInterpolationPoints +
                ", noiseInterpolationPoints=" + noiseInterpolationPoints +
                ", randomSeed=" + randomSeed +
                ", lineLength=" + lineLength +
                '}';
    }

    @Override
    public int getLineInterpolationPointsCount() {
        return lineInterpolationPoints;
    }

    @Override
    public int getNoiseInterpolationPointsCount() {
        return noiseInterpolationPoints;
    }

    private static double adjustValueRange(double interpolatedValue) {
        return ((interpolatedValue / MAX_2D_VECTOR_PRODUCT_VALUE) + 1.0) / 2.0;
    }

    private void addGeneratedRows(int width) {
        for (int i = 0; i < width; i++) {
            this.generated.add(new LinkedBlockingQueue<>());
        }
    }

    private void assertEnoughDataIsGenerated(int count) {
        while (generated.get(0).size() < count) {
            generateNextSegment();
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

    private void generateNextSegment() {
        List<Vector2D> newBounds = generateNewRandomBounds(randomBounds);

        for (int yIndex = 0; yIndex < lineLength; yIndex++) {
            int lowerBoundIndex = yIndex / lineSegmentLength;
            Vector2D topLeftBound = previousBounds.get(lowerBoundIndex);
            Vector2D topRightBound = newBounds.get(lowerBoundIndex);
            Vector2D bottomLeftBound = previousBounds.get(lowerBoundIndex + 1);
            Vector2D bottomRightBound = newBounds.get(lowerBoundIndex + 1);

            int segmentYIndex = yIndex % lineSegmentLength;
            double yDist = (double) (segmentYIndex + 1) / (lineSegmentLength + 1);

            for (int segmentXIndex = 0; segmentXIndex < noiseSegmentLength; segmentXIndex++) {

                double xDist = (double) (segmentXIndex + 1) / (noiseSegmentLength + 1);

                Vector2D topLeftDistance = new Vector2D(xDist, yDist);
                Vector2D topRightDistance = new Vector2D(xDist - 1.0, yDist);
                Vector2D bottomLeftDistance = new Vector2D(xDist, yDist - 1.0);
                Vector2D bottomRightDistance = new Vector2D(xDist - 1.0, yDist - 1.0);

                double topLeftBoundImpact = topLeftBound.getVectorProduct(topLeftDistance);
                double topRightBoundImpact = topRightBound.getVectorProduct(topRightDistance);
                double bottomLeftBoundImpact = bottomLeftBound.getVectorProduct(bottomLeftDistance);
                double bottomRightBoundImpact = bottomRightBound.getVectorProduct(bottomRightDistance);

                double interpolatedValue = Interpolation
                        .twoDimensionalWithFade(topLeftBoundImpact, topRightBoundImpact, bottomLeftBoundImpact,
                                bottomRightBoundImpact, xDist, yDist);
                double adjustedValue = adjustValueRange(interpolatedValue);
                double amplifiedValue = adjustedValue * maxAmplitude;
                generated.get(yIndex).add(amplifiedValue);
            }
        }
        previousBounds = newBounds;
    }

    private List<Vector2D> generateNewRandomBounds(int count) {
        List<Vector2D> newBounds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        return newBounds;
    }
}
