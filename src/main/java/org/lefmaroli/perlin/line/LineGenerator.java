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

    private final int lineInterpolationPoints;
    private final int lineLength;
    private final RandomGenerator randomGenerator;
    private final int randomBoundsCount;
    private final LineNoiseData[] results;
    private final int noiseSegmentLength;
    private List<Vector2D> previousBounds;
    private List<Vector2D> currentBounds;
    private int currentPosInNoiseInterpolation = 0;

    public LineGenerator(int noiseInterpolationPoints, int lineInterpolationPoints, int lineLength,
                         double maxAmplitude, long randomSeed, boolean isCircular) {
        super(noiseInterpolationPoints, maxAmplitude, randomSeed, isCircular);
        assertValidValues(parameterNames, lineInterpolationPoints, lineLength);
        this.lineLength = lineLength;
        this.lineInterpolationPoints =
                correctInterpolationPointsForCircularity(lineInterpolationPoints, lineLength, "line length");
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.randomBoundsCount = 2 + lineLength / lineInterpolationPoints;
        this.previousBounds = generateNewRandomBounds();
        this.currentBounds = generateNewRandomBounds();
        this.noiseSegmentLength = computeNoiseSegmentLength(lineLength);
        this.results = new LineNoiseData[noiseSegmentLength];
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
        if (!super.equals(o)) return false;
        LineGenerator that = (LineGenerator) o;
        return lineInterpolationPoints == that.lineInterpolationPoints &&
                lineLength == that.lineLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lineInterpolationPoints, lineLength);
    }

    @Override
    public String toString() {
        return "LineGenerator{" +
                "noiseInterpolationPoints=" + getNoiseInterpolationPoints() +
                ", lineInterpolationPoints=" + lineInterpolationPoints +
                ", lineLength=" + lineLength +
                ", maxAmplitude=" + getMaxAmplitude() +
                ", randomSeed=" + randomSeed +
                ", isCircular=" + isCircular() +
                '}';
    }

    @Override
    public int getLineInterpolationPointsCount() {
        return lineInterpolationPoints;
    }

    @Override
    public int getNoiseSegmentLength() {
        return noiseSegmentLength;
    }

    @Override
    protected LineNoiseData[] generateNextSegment() {
        for (int i = 0; i < noiseSegmentLength; i++) {
            currentPosInNoiseInterpolation++;
            if (currentPosInNoiseInterpolation == getNoiseInterpolationPoints()) {
                previousBounds = currentBounds;
                currentBounds = generateNewRandomBounds();
                currentPosInNoiseInterpolation = 0;
            }
            results[i] = processNoiseDomain(currentPosInNoiseInterpolation);
        }
        return results;
    }

    @Override
    protected LineNoiseDataContainer getInContainer(LineNoiseData[] data) {
        return new LineNoiseDataContainer(data);
    }

    @Override
    protected LineNoiseData[] getArrayOfSubType(int count) {
        return new LineNoiseData[count];
    }

    private static double adjustValueRange(double interpolatedValue) {
        return ((interpolatedValue / MAX_2D_VECTOR_PRODUCT_VALUE) + 1.0) / 2.0;
    }

    private static double interpolate(Vector2D previousTopBound, Vector2D nextTopBound, Vector2D previousBottomBound,
                                      Vector2D nextBottomBound, double noiseDist, double lineDist) {
        double previousTopBoundImpact = previousTopBound.getVectorProduct(noiseDist, lineDist);
        double nextTopBoundImpact = nextTopBound.getVectorProduct(noiseDist - 1.0, lineDist);
        double previousBottomBoundImpact = previousBottomBound.getVectorProduct(noiseDist, lineDist - 1.0);
        double nextBottomBoundImpact = nextBottomBound.getVectorProduct(noiseDist - 1.0, lineDist - 1.0);

        return Interpolation.linear2DWithFade(previousTopBoundImpact, previousBottomBoundImpact, nextTopBoundImpact,
                nextBottomBoundImpact, noiseDist, lineDist);
    }

    private int computeNoiseSegmentLength(int lineLength) {
        int noiseSegmentLength = Math.min(MB_10_IN_DOUBLES_SIZE / lineLength, getNoiseInterpolationPoints());
        if (noiseSegmentLength < 1) {
            noiseSegmentLength = 1;
            LOGGER.warn("Creating line generator of more than 10MB in size");
        }
        return noiseSegmentLength;
    }

    private LineNoiseData processNoiseDomain(int noiseIndex) {
        double noiseDist = (double) (noiseIndex) / (getNoiseInterpolationPoints());
        double[] lineData = new double[lineLength];
        for (int lineIndex = 0; lineIndex < lineLength; lineIndex++) {
            lineData[lineIndex] = processLineDomain(noiseDist, lineIndex);
        }
        return new LineNoiseData(lineData);
    }

    private double processLineDomain(double noiseDist, int lineIndex) {
        int lowerBoundIndex = lineIndex / lineInterpolationPoints;
        Vector2D prevTop = previousBounds.get(lowerBoundIndex);
        Vector2D nextTop = currentBounds.get(lowerBoundIndex);
        Vector2D prevBottom = previousBounds.get(lowerBoundIndex + 1);
        Vector2D nextBottom = currentBounds.get(lowerBoundIndex + 1);
        int x = lineIndex % lineInterpolationPoints;
        double lineDist = (double) (x) / (lineInterpolationPoints);
        double interpolatedValue =
                interpolate(prevTop, nextTop, prevBottom, nextBottom, noiseDist, lineDist);
        return adjustValueRange(interpolatedValue) * getMaxAmplitude();
    }

    private List<Vector2D> generateNewRandomBounds() {
        List<Vector2D> newBounds = new ArrayList<>(randomBoundsCount);
        for (int i = 0; i < randomBoundsCount; i++) {
            newBounds.add(randomGenerator.getRandomUnitVector2D());
        }
        if (isCircular()) {
            newBounds.set(randomBoundsCount - 2, newBounds.get(0));
            newBounds.set(randomBoundsCount - 1, newBounds.get(1));
        }
        return newBounds;
    }
}
