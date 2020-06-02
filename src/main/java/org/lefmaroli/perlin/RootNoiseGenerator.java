package org.lefmaroli.perlin;

import org.lefmaroli.perlin.data.NoiseData;

import java.util.*;

public abstract class RootNoiseGenerator<ContainerDataType extends NoiseData, DataType>
        implements INoiseGenerator<ContainerDataType> {

    protected final long randomSeed;
    private final Queue<DataType> generated = new LinkedList<>();
    private final int noiseInterpolationPoints;
    private final double maxAmplitude;

    public RootNoiseGenerator(int noiseInterpolationPoints, double maxAmplitude, long randomSeed) {
        if (noiseInterpolationPoints < 0) {
            throw new IllegalArgumentException("Noise interpolation points must be greater than 0");
        }
        this.noiseInterpolationPoints = noiseInterpolationPoints;
        this.maxAmplitude = maxAmplitude;
        this.randomSeed = randomSeed;
    }

    public int getNoiseInterpolationPoints() {
        return noiseInterpolationPoints;
    }

    public ContainerDataType getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        return computeAtLeast(count);
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

    public abstract int getNoiseSegmentLength();

    protected void assertValidValues(List<String> names, int... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0) {
                throw new IllegalArgumentException(String.format("%s must be greater than 0", names.get(i)));
            }
        }
    }

    protected abstract DataType[] generateNextSegment();

    protected abstract ContainerDataType getInContainer(DataType[] data);

    protected abstract DataType[] getArrayOfSubType(int count);

    private ContainerDataType computeAtLeast(int count) {
        DataType[] results = getArrayOfSubType(count);
        if (count < generated.size()) {
            for (int i = 0; i < count; i++) {
                results[i] = generated.poll();
            }
        } else {
            int currentIndex = generated.size();
            for (int i = 0; i < currentIndex; i++) {
                results[i] = generated.poll();
            }
            int remainingCount = count - currentIndex;
            int noiseSegmentLength = getNoiseSegmentLength();
            while (remainingCount > noiseSegmentLength) {
                DataType[] nextSegment = generateNextSegment();
                System.arraycopy(nextSegment, 0, results, currentIndex, noiseSegmentLength);
                currentIndex += noiseSegmentLength;
                remainingCount -= noiseSegmentLength;
            }
            DataType[] lastSegment = generateNextSegment();
            System.arraycopy(lastSegment, 0, results, currentIndex, remainingCount);
            generated.addAll(Arrays.asList(lastSegment).subList(remainingCount, noiseSegmentLength));
        }
        return getInContainer(results);
    }
}
