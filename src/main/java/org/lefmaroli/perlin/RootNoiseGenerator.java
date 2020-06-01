package org.lefmaroli.perlin;

import org.lefmaroli.perlin.data.NoiseData;

import java.util.*;

public abstract class RootNoiseGenerator<ContainerDataType extends NoiseData, DataType>
        implements INoiseGenerator<ContainerDataType> {

    private final Queue<DataType> generated = new LinkedList<>();
    private final int noiseInterpolationPoints;
    private final double maxAmplitude;
    protected final long randomSeed;

    public RootNoiseGenerator(int noiseInterpolationPoints, double maxAmplitude, long randomSeed) {
        if (noiseInterpolationPoints < 0) {
            throw new IllegalArgumentException("Noise interpolation points must be greater than 0");
        }
        this.noiseInterpolationPoints = noiseInterpolationPoints;
        this.maxAmplitude = maxAmplitude;
        this.randomSeed = randomSeed;
    }

    protected void assertValidValues(List<String> names, int... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0) {
                throw new IllegalArgumentException(String.format("%s must be greater than 0", names.get(i)));
            }
        }
    }

    public int getNoiseInterpolationPoints() {
        return noiseInterpolationPoints;
    }

    protected abstract List<DataType> generateNextSegment();

    protected abstract ContainerDataType getInContainer(List<DataType> data);

    public ContainerDataType getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        return computeAtLeast(count);
    }

    private ContainerDataType computeAtLeast(int count) {
        List<DataType> results = new ArrayList<>(count);
        if (count < generated.size()) {
            for (int i = 0; i < count; i++) {
                results.add(generated.poll());
            }
        } else {
            for (int i = 0; i < generated.size(); i++) {
                results.add(generated.poll());
            }
            int newCount = count - results.size();
            int noiseInterpolationPointsCount = getNoiseInterpolationPoints();
            while (newCount > noiseInterpolationPointsCount) {
                results.addAll(generateNextSegment());
                newCount -= noiseInterpolationPointsCount;
            }
            List<DataType> lastSegment = generateNextSegment();
            results.addAll(lastSegment.subList(0, newCount));
            generated.addAll(lastSegment.subList(newCount, lastSegment.size()));
        }
        return getInContainer(results);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RootNoiseGenerator<?, ?> that = (RootNoiseGenerator<?, ?>) o;
        return noiseInterpolationPoints == that.noiseInterpolationPoints &&
                Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                randomSeed == that.randomSeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(noiseInterpolationPoints, maxAmplitude, randomSeed);
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }
}
