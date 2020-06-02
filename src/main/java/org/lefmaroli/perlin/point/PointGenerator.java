package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;

import java.util.Random;

public class PointGenerator extends RootNoiseGenerator<PointNoiseDataContainer, PointNoiseData>
        implements PointNoiseGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);
    private static final int MAX_NUMBER_INTERPOLATION_POINTS = 500;
    private int currentPosInInterpolation = 0;
    private final int noiseSegmentLength;
    private final Random randomGenerator;
    private double previousBound;
    private double currentBound;
    private final PointNoiseData[] results;

    public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
        super(interpolationPoints, maxAmplitude, randomSeed);
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
        this.currentBound = randomGenerator.nextDouble();
        this.noiseSegmentLength = Math.min(interpolationPoints, MAX_NUMBER_INTERPOLATION_POINTS);
        results = new PointNoiseData[noiseSegmentLength];
        LOGGER.debug("Created new " + toString());
    }

    @Override
    public int getNoiseSegmentLength() {
        return noiseSegmentLength;
    }

    @Override
    public String toString() {
        return "PointGenerator{" +
                "noiseInterpolationPoints=" + getNoiseInterpolationPoints() +
                ", maxAmplitude=" + getMaxAmplitude() +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    protected PointNoiseData[] generateNextSegment() {
        for (int i = 0; i < noiseSegmentLength; i++) {
            currentPosInInterpolation++;
            if(currentPosInInterpolation == getNoiseInterpolationPoints()){
                previousBound = currentBound;
                currentBound = randomGenerator.nextDouble();
                currentPosInInterpolation = 0;
            }
            double relativePositionInSegment = currentPosInInterpolation / (double) getNoiseInterpolationPoints();
            double interpolatedValue =
                    Interpolation.linearWithFade(previousBound, currentBound, relativePositionInSegment);
            results[i] = new PointNoiseData(interpolatedValue * getMaxAmplitude());
        }
        return results;
    }

    @Override
    protected PointNoiseData[] getArrayOfSubType(int count) {
        return new PointNoiseData[count];
    }

    @Override
    protected PointNoiseDataContainer getInContainer(PointNoiseData[] data) {
        return new PointNoiseDataContainer(data);
    }
}
