package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;

import java.util.*;

public class PointGenerator implements RootNoiseGenerator, PointNoiseGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);

    private final double maxAmplitude;
    private final int interpolationPoints;
    private final Random randomGenerator;
    private final long randomSeed;
    private final Queue<PointNoiseData> generated = new LinkedList<>();
    private double previousBound;

    public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
        if (interpolationPoints < 0) {
            throw new IllegalArgumentException("Interpolation points must be greater than 0");
        }
        this.maxAmplitude = maxAmplitude;
        this.interpolationPoints = interpolationPoints;
        this.randomSeed = randomSeed;
        this.randomGenerator = new Random(randomSeed);
        this.previousBound = randomGenerator.nextDouble();
        LOGGER.debug("Created with max amplitude of " + maxAmplitude + " and " + interpolationPoints +
                " interpolation points.");
    }

    @Override
    public PointNoiseDataContainer getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        return new PointNoiseDataContainer(computeAtLeast(count));
    }

    private List<PointNoiseData> computeAtLeast(int count){
        List<PointNoiseData> results = new ArrayList<>(count);
        if(count < generated.size()){
            for (int i = 0; i < count; i++) {
                results.add(generated.poll());
            }
        }else{
            for (int i = 0; i < generated.size(); i++) {
                results.add(generated.poll());
            }
            int newCount = count - results.size();
            while(newCount > interpolationPoints){
                results.addAll(generateNextSegment());
                newCount -= interpolationPoints;
            }
            List<PointNoiseData> lastSegment = generateNextSegment();
            results.addAll(lastSegment.subList(0, newCount));
            generated.addAll(lastSegment.subList(newCount, lastSegment.size()));
        }
        return results;
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    @Override
    public String toString() {
        return "PointGenerator{" +
                "maxAmplitude=" + maxAmplitude +
                ", interpolationPoints=" + interpolationPoints +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointGenerator that = (PointGenerator) o;
        return Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                Double.compare(that.interpolationPoints, interpolationPoints) == 0 &&
                randomSeed == that.randomSeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAmplitude, interpolationPoints, randomSeed);
    }

    @Override
    public int getNoiseInterpolationPointsCount() {
        return interpolationPoints;
    }

    private List<PointNoiseData> generateNextSegment() {
        double newBound = randomGenerator.nextDouble();
        double currentPos = 0.0;
        List<PointNoiseData> results = new ArrayList<>(interpolationPoints);
        while (currentPos < interpolationPoints) {
            double relativePositionInSegment = currentPos / interpolationPoints;
            double interpolatedValue = Interpolation.linearWithFade(previousBound, newBound, relativePositionInSegment);
            results.add(new PointNoiseData(interpolatedValue * maxAmplitude));
            currentPos++;
        }
        previousBound = newBound;
        return results;
    }
}
