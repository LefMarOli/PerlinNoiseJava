package org.lefmaroli.perlin.slice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;
import org.lefmaroli.perlin.line.LineNoiseData;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SliceGenerator extends RootNoiseGenerator<SliceNoiseDataContainer, SliceNoiseData>
        implements SliceNoiseGenerator {
    private static final double MAX_3D_VECTOR_PRODUCT_VALUE = Math.sqrt(2.0);

    private static final Logger LOGGER = LogManager.getLogger(SliceGenerator.class);

    private static final List<String> parameterNames =
            List.of("Width interpolation points", "Slice width", "Height interpolation points", "Slice height");
    private final int widthInterpolationPoints;
    private final int heightInterpolationPoints;
    private final int sliceWidth;
    private final int sliceHeight;
    private final double maxAmplitude;
    private final long randomSeed;
    private final RandomGenerator randomGenerator;
    private final int randomBoundsXCount;
    private final int randomBoundsYCount;
    private final boolean isCircular;
    private Vector3D[][] previousBounds;

    SliceGenerator(int noiseInterpolationPoints, int widthInterpolationPoint, int heightInterpolationPoint,
                   int sliceWidth, int sliceHeight, double maxAmplitude, long randomSeed, boolean isCircular) {
        super(noiseInterpolationPoints);
        assertValidValues(parameterNames, widthInterpolationPoint, heightInterpolationPoint, sliceWidth, sliceHeight);
        this.widthInterpolationPoints = correctInterpolationPointsForCircularity(widthInterpolationPoint, sliceWidth);
        if (this.widthInterpolationPoints != widthInterpolationPoint) {
            LOGGER.warn("Modified required width interpolation point count from " + widthInterpolationPoint +
                    " to " + this.widthInterpolationPoints + " to respect circularity.");
        }
        this.heightInterpolationPoints =
                correctInterpolationPointsForCircularity(heightInterpolationPoint, sliceHeight);
        if (this.heightInterpolationPoints != heightInterpolationPoint) {
            LOGGER.warn("Modified required height interpolation point count from " + heightInterpolationPoint +
                    " to " + this.heightInterpolationPoints + " to respect circularity.");
        }
        this.sliceWidth = sliceWidth;
        this.sliceHeight = sliceHeight;
        this.maxAmplitude = maxAmplitude;
        this.randomSeed = randomSeed;
        this.randomGenerator = new RandomGenerator(randomSeed);
        this.isCircular = isCircular;
        this.randomBoundsXCount = 2 + this.sliceWidth / this.widthInterpolationPoints;
        this.randomBoundsYCount = 2 + this.sliceHeight / this.heightInterpolationPoints;
        this.previousBounds = getNewBounds();
    }

    public int getWidthInterpolationPoints() {
        return widthInterpolationPoints;
    }

    public int getHeightInterpolationPoints() {
        return heightInterpolationPoints;
    }

    private Vector3D[][] getNewBounds() {
        Vector3D[][] newBounds = new Vector3D[randomBoundsXCount][randomBoundsYCount];
        for (int i = 0; i < randomBoundsXCount; i++) {
            for (int j = 0; j < randomBoundsYCount; j++) {
                newBounds[i][j] = randomGenerator.getRandomUnitVector3D();
            }
        }
        if (isCircular) {
            for (int i = randomBoundsXCount - 2; i < randomBoundsXCount; i++) {
                for (int j = randomBoundsYCount - 2; j < randomBoundsYCount; j++) {
                    newBounds[i][j] = newBounds[i - (randomBoundsXCount - 2)][j - (randomBoundsYCount - 2)];
                }
            }
        }
        return newBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SliceGenerator that = (SliceGenerator) o;
        return widthInterpolationPoints == that.widthInterpolationPoints &&
                heightInterpolationPoints == that.heightInterpolationPoints &&
                sliceWidth == that.sliceWidth &&
                sliceHeight == that.sliceHeight &&
                Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                randomSeed == that.randomSeed &&
                isCircular == that.isCircular;
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(super.hashCode(), widthInterpolationPoints, heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed, isCircular);
    }

    @Override
    public String toString() {
        return "SliceGenerator{" +
                "noiseInterpolationPoints=" + getNoiseInterpolationPoints() +
                ", widthInterpolationPoints=" + widthInterpolationPoints +
                ", heightInterpolationPoints=" + heightInterpolationPoints +
                ", sliceWidth=" + sliceWidth +
                ", sliceHeight=" + sliceHeight +
                ", maxAmplitude=" + maxAmplitude +
                ", randomSeed=" + randomSeed +
                ", isCircular=" + isCircular +
                '}';
    }

    @Override
    public int getSliceWidth() {
        return sliceWidth;
    }

    @Override
    public int getSliceHeight() {
        return sliceHeight;
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    @Override
    protected List<SliceNoiseData> generateNextSegment() {
        Vector3D[][] newBounds = getNewBounds();
        List<SliceNoiseData> results = new ArrayList<>(getNoiseInterpolationPoints());
        for (int noiseIndex = 0; noiseIndex < getNoiseInterpolationPoints(); noiseIndex++) {
            results.add(processNoiseDomain(noiseIndex, newBounds));
        }
        previousBounds = newBounds;
        return results;
    }

    private SliceNoiseData processNoiseDomain(int noiseIndex, Vector3D[][] newBounds) {
        List<LineNoiseData> results = new ArrayList<>(sliceWidth);
        double noiseDist = (double) (noiseIndex) / (getNoiseInterpolationPoints());
        for (int xIndex = 0; xIndex < sliceWidth; xIndex++) {
            results.add(processSliceWidthDomain(noiseDist, xIndex, newBounds));
        }
        return new SliceNoiseData(results);
    }

    private LineNoiseData processSliceWidthDomain(double noiseDist, int xIndex, Vector3D[][] newBounds) {
        int x = xIndex % widthInterpolationPoints;
        double xDist = (double) (x) / (widthInterpolationPoints);
        int lowerBoundXIndex = xIndex / widthInterpolationPoints;
        double[] yData = new double[sliceHeight];
        for (int yIndex = 0; yIndex < sliceHeight; yIndex++) {
            yData[yIndex] = processSliceHeightDomain(noiseDist, xDist, yIndex, newBounds, lowerBoundXIndex);
        }
        return new LineNoiseData(yData);
    }

    private double processSliceHeightDomain(double noiseDist, double xDist, int yIndex, Vector3D[][] newBounds,
                                            int lowerBoundXIndex) {
        int y = yIndex % heightInterpolationPoints;
        double yDist = (double) (y) / (heightInterpolationPoints);
        int lowerBoundYIndex = yIndex / heightInterpolationPoints;
        double interpolatedValue = interpolate(noiseDist, xDist, yDist, newBounds, lowerBoundXIndex, lowerBoundYIndex);
        return adjustValueRange(interpolatedValue) * maxAmplitude;
    }

    private static double adjustValueRange(double interpolatedValue) {
        return ((interpolatedValue / MAX_3D_VECTOR_PRODUCT_VALUE) + 1.0) / 2.0;
    }

    private double interpolate(double noiseDist, double xDist, double yDist, Vector3D[][] newBounds,
                               int lowerBoundXIndex, int lowerBoundYIndex) {

        Vector3D previousTopLeftBound = previousBounds[lowerBoundXIndex][lowerBoundYIndex];
        Vector3D previousTopRightBound = previousBounds[lowerBoundXIndex][lowerBoundYIndex + 1];
        Vector3D previousBottomLeftBound = previousBounds[lowerBoundXIndex + 1][lowerBoundYIndex];
        Vector3D previousBottomRightBound = previousBounds[lowerBoundXIndex + 1][lowerBoundYIndex + 1];
        Vector3D nextTopLeftBound = newBounds[lowerBoundXIndex][lowerBoundYIndex];
        Vector3D nextTopRightBound = newBounds[lowerBoundXIndex][lowerBoundYIndex + 1];
        Vector3D nextBottomLeftBound = newBounds[lowerBoundXIndex + 1][lowerBoundYIndex];
        Vector3D nextBottomRightBound = newBounds[lowerBoundXIndex + 1][lowerBoundYIndex + 1];

        double previousTopLeftImpact = previousTopLeftBound.getVectorProduct(xDist, yDist, noiseDist);
        double previousTopRightImpact = previousTopRightBound.getVectorProduct(xDist, yDist - 1.0, noiseDist);
        double previousBottomLeftImpact = previousBottomLeftBound.getVectorProduct(xDist - 1.0, yDist, noiseDist);
        double previousBottomRightImpact =
                previousBottomRightBound.getVectorProduct(xDist - 1.0, yDist - 1.0, noiseDist);
        double nextTopLeftImpact = nextTopLeftBound.getVectorProduct(xDist, yDist, noiseDist - 1.0);
        double nextTopRightImpact = nextTopRightBound.getVectorProduct(xDist, yDist - 1.0, noiseDist - 1.0);
        double nextBottomLeftImpact = nextBottomLeftBound.getVectorProduct(xDist - 1.0, yDist, noiseDist - 1.0);
        double nextBottomRightImpact = nextBottomRightBound.getVectorProduct(xDist - 1.0, yDist - 1.0, noiseDist - 1.0);

        return Interpolation
                .linear3DWithFade(previousTopLeftImpact, nextTopLeftImpact, previousTopRightImpact, nextTopRightImpact,
                        previousBottomLeftImpact, nextBottomLeftImpact, previousBottomRightImpact,
                        nextBottomRightImpact, xDist, yDist, noiseDist);
    }

    @Override
    protected SliceNoiseDataContainer getInContainer(List<SliceNoiseData> data) {
        return new SliceNoiseDataContainer(data);
    }
}
