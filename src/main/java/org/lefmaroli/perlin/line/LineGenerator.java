package org.lefmaroli.perlin.line;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineGenerator extends RootLineNoiseGenerator implements LineNoiseGenerator {

    private static final double MAX_2D_VECTOR_PRODUCT_VALUE = Math.sqrt(2.0) / 2.0;
    private static final Logger LOGGER = LogManager.getLogger(LineGenerator.class);
    private static final List<String> parameterNames = List.of("Line interpolation points", "Line length");

    private final double maxAmplitude;
    private final int lineInterpolationPoints;
    private final long randomSeed;
    private final RandomGenerator randomGenerator;
    private final int lineLength;
    private final int randomBoundsCount;
    private final boolean isCircular;
    private List<Vector2D> previousBounds;

    public LineGenerator(int noiseInterpolationPoints, int lineInterpolationPoints, int lineLength,
                         double maxAmplitude, long randomSeed, boolean isCircular) {
        super(noiseInterpolationPoints);
        assertValidValues(parameterNames, lineInterpolationPoints, lineLength);
        this.maxAmplitude = maxAmplitude;
        this.lineLength = lineLength;
        this.isCircular = isCircular;
        this.lineInterpolationPoints = correctInterpolationPointsForCircularity(lineInterpolationPoints, lineLength);
        if (this.lineInterpolationPoints != lineInterpolationPoints) {
            LOGGER.warn("Modified required line interpolation point count from " + lineInterpolationPoints + " to " +
                    this.lineInterpolationPoints + " to respect circularity.");
        }
        this.randomSeed = randomSeed;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.randomBoundsCount = 2 + lineLength / lineInterpolationPoints;
        this.previousBounds = generateNewRandomBounds();
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
                getNoiseInterpolationPoints() == that.getNoiseInterpolationPoints() &&
                randomSeed == that.randomSeed &&
                lineLength == that.lineLength &&
                isCircular == that.isCircular;
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(maxAmplitude, lineInterpolationPoints, getNoiseInterpolationPoints(), randomSeed, lineLength,
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
                ", noiseInterpolationPoints=" + getNoiseInterpolationPoints() +
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

    @Override
    protected List<LineNoiseData> generateNextSegment() {
        List<Vector2D> newBounds = generateNewRandomBounds();
        List<LineNoiseData> results = new ArrayList<>(getNoiseInterpolationPoints());
        for (int noiseIndex = 0; noiseIndex < getNoiseInterpolationPoints(); noiseIndex++) {
            results.add(processNoiseDomain(noiseIndex, newBounds));
        }
        previousBounds = newBounds;
        return results;
    }

    @Override
    protected LineNoiseDataContainer getInContainer(List<LineNoiseData> data) {
        return new LineNoiseDataContainer(data);
    }


    private LineNoiseData processNoiseDomain(int noiseIndex, List<Vector2D> newBounds) {
        double noiseDist = (double) (noiseIndex) / (getNoiseInterpolationPoints());
        double[] lineData = new double[lineLength];
        for (int lineIndex = 0; lineIndex < lineLength; lineIndex++) {
            lineData[lineIndex] = processLineDomain(noiseDist, lineIndex, newBounds);
        }
        return new LineNoiseData(lineData);
    }

    private double processLineDomain(double noiseDist, int lineIndex, List<Vector2D> newBounds) {
        int lowerBoundIndex = lineIndex / lineInterpolationPoints;
        Vector2D prevTop = previousBounds.get(lowerBoundIndex);
        Vector2D nextTop = newBounds.get(lowerBoundIndex);
        Vector2D prevBottom = previousBounds.get(lowerBoundIndex + 1);
        Vector2D nextBottom = newBounds.get(lowerBoundIndex + 1);
        int x = lineIndex % lineInterpolationPoints;
        double lineDist = (double) (x) / (lineInterpolationPoints);
        double interpolatedValue =
                interpolate(prevTop, nextTop, prevBottom, nextBottom, noiseDist, lineDist);
        return adjustValueRange(interpolatedValue) * maxAmplitude;
    }

    private double interpolate(Vector2D previousTopBound, Vector2D nextTopBound, Vector2D previousBottomBound,
                               Vector2D nextBottomBound, double noiseDist, double lineDist) {
        double previousTopBoundImpact = previousTopBound.getVectorProduct(noiseDist, lineDist);
        double nextTopBoundImpact = nextTopBound.getVectorProduct(noiseDist - 1.0, lineDist);
        double previousBottomBoundImpact = previousBottomBound.getVectorProduct(noiseDist, lineDist - 1.0);
        double nextBottomBoundImpact = nextBottomBound.getVectorProduct(noiseDist - 1.0, lineDist - 1.0);

        return Interpolation.linear2DWithFade(previousTopBoundImpact, previousBottomBoundImpact, nextTopBoundImpact,
                nextBottomBoundImpact, noiseDist, lineDist);
    }

    private List<Vector2D> generateNewRandomBounds() {
        List<Vector2D> newBounds = new ArrayList<>(randomBoundsCount);
        for (int i = 0; i < randomBoundsCount; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        if (isCircular) {
            newBounds.set(randomBoundsCount - 2, newBounds.get(0));
            newBounds.set(randomBoundsCount - 1, newBounds.get(1));
        }
        return newBounds;
    }
}
