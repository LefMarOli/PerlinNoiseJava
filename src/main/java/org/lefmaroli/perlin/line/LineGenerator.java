package org.lefmaroli.perlin.line;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.rounding.RoundUtils;
import org.lefmaroli.vector.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineGenerator extends RootLineNoiseGenerator implements LineNoiseGenerator {

    private static final double MAX_2D_VECTOR_PRODUCT_VALUE = Math.sqrt(2.0) / 2.0;
    private static final Logger LOGGER = LogManager.getLogger(LineGenerator.class);

    private final double maxAmplitude;
    private final int lineInterpolationPoints;
    private final long randomSeed;
    private final RandomGenerator randomGenerator;
    private final int lineLength;
    private final int randomBoundsCount;
    private final boolean isCircular;

    public LineGenerator(int lineLength, int lineInterpolationPoints, int noiseInterpolationPoints,
                         double maxAmplitude, long randomSeed, boolean isCircular) {
        super(noiseInterpolationPoints);
        if (lineInterpolationPoints < 0) {
            throw new IllegalArgumentException("Line interpolation points must be greater than 0");
        }
        if (lineLength < 0) {
            throw new IllegalArgumentException("Line length must be greater than 0");
        }
        this.maxAmplitude = maxAmplitude;
        this.lineLength = lineLength;
        this.isCircular = isCircular;
        this.lineInterpolationPoints =
                isCircular ? correctLineInterpolationPointsForCircularity(lineInterpolationPoints) :
                        lineInterpolationPoints;
        this.randomSeed = randomSeed;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.randomBoundsCount = 2 + lineLength / lineInterpolationPoints;
        LOGGER.debug("Created new " + toString());
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
                getNoiseInterpolationPointsCount() == that.getNoiseInterpolationPointsCount() &&
                randomSeed == that.randomSeed &&
                lineLength == that.lineLength &&
                isCircular == that.isCircular;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAmplitude, lineInterpolationPoints, getNoiseInterpolationPointsCount(), randomSeed, lineLength,
                isCircular);
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
                ", noiseInterpolationPointsCount=" + getNoiseInterpolationPointsCount() +
                ", randomSeed=" + randomSeed +
                ", lineLength=" + lineLength +
                ", isCircular=" + isCircular +
                '}';
    }

    @Override
    public int getLineInterpolationPointsCount() {
        return lineInterpolationPoints;
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    private static double adjustValueRange(double interpolatedValue) {
        return ((interpolatedValue / MAX_2D_VECTOR_PRODUCT_VALUE) + 1.0) / 2.0;
    }

    private int correctLineInterpolationPointsForCircularity(int lineInterpolationPoints) {
        int newInterpolationPointCount =
                RoundUtils.roundNToClosestFactorOfM(lineInterpolationPoints, lineLength);
        if (newInterpolationPointCount != lineInterpolationPoints) {
            LOGGER.warn("Modified required line interpolation point count from " + lineInterpolationPoints + " to " +
                    newInterpolationPointCount + " to respect circularity.");
        }
        return newInterpolationPointCount;
    }

    @Override
    protected List<LineNoiseData> generateNextSegment(List<Vector2D> previousBounds, List<Vector2D> currentBounds) {
        List<LineNoiseData> results = new ArrayList<>(getNoiseInterpolationPointsCount());
        for (int xIndex = 0; xIndex < getNoiseInterpolationPointsCount(); xIndex++) {
            results.add(processX(xIndex, previousBounds, currentBounds));
        }
        return results;
    }

    @Override
    protected LineNoiseDataContainer getInContainer(List<LineNoiseData> data) {
        return new LineNoiseDataContainer(data);
    }

    @Override
    protected List<Vector2D> getNewBounds() {
        List<Vector2D> newBounds = new ArrayList<>(randomBoundsCount);
        for (int i = 0; i < randomBoundsCount - 2; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        if (isCircular) {
            newBounds.add(newBounds.get(0));
            newBounds.add(newBounds.get(1));
        } else {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        return newBounds;
    }


    private LineNoiseData processX(int xIndex, List<Vector2D> previousBounds, List<Vector2D> newBounds) {
        double xDist = (double) (xIndex) / (getNoiseInterpolationPointsCount());
        List<Double> lineData = new ArrayList<>(lineLength);
        for (int yIndex = 0; yIndex < lineLength; yIndex++) {
            lineData.add(processY(xDist, yIndex, previousBounds, newBounds));
        }
        return new LineNoiseData(lineData);
    }

    private double processY(double xDist, int yIndex, List<Vector2D> previousBounds, List<Vector2D> newBounds) {
        int lowerBoundIndex = yIndex / lineInterpolationPoints;
        Vector2D topLeftBound = previousBounds.get(lowerBoundIndex);
        Vector2D topRightBound = newBounds.get(lowerBoundIndex);
        Vector2D bottomLeftBound = previousBounds.get(lowerBoundIndex + 1);
        Vector2D bottomRightBound = newBounds.get(lowerBoundIndex + 1);
        int y = yIndex % lineInterpolationPoints;
        double yDist = (double) (y) / (lineInterpolationPoints);
        return interpolate(topLeftBound, topRightBound, bottomLeftBound, bottomRightBound, yDist, xDist);
    }

    private double interpolate(Vector2D topLeftBound, Vector2D topRightBound, Vector2D bottomLeftBound,
                               Vector2D bottomRightBound, double yDist, double xDist) {
        Vector2D topLeftDist = new Vector2D(xDist, yDist);
        Vector2D topRightDist = new Vector2D(xDist - 1.0, yDist);
        Vector2D bottomLeftDist = new Vector2D(xDist, yDist - 1.0);
        Vector2D bottomRightDist = new Vector2D(xDist - 1.0, yDist - 1.0);

        double topLeftBoundImpact = topLeftBound.getVectorProduct(topLeftDist);
        double topRightBoundImpact = topRightBound.getVectorProduct(topRightDist);
        double bottomLeftBoundImpact = bottomLeftBound.getVectorProduct(bottomLeftDist);
        double bottomRightBoundImpact = bottomRightBound.getVectorProduct(bottomRightDist);

        double interpolatedValue = Interpolation
                .linear2DWithFade(topLeftBoundImpact, topRightBoundImpact, bottomLeftBoundImpact,
                        bottomRightBoundImpact, xDist, yDist);
        return adjustValueRange(interpolatedValue) * maxAmplitude;
    }
}
