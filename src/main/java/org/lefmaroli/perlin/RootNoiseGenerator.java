package org.lefmaroli.perlin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public abstract class RootNoiseGenerator<C> implements INoiseGenerator<C> {

  protected final long randomSeed;
  private final Queue<C> generated = new LinkedList<>();
  private final double stepSize;
  private final int noiseInterpolationPoints;
  private final double maxAmplitude;

  public RootNoiseGenerator(int noiseInterpolationPoints, double maxAmplitude, long randomSeed) {
    if (noiseInterpolationPoints < 0) {
      throw new IllegalArgumentException("Noise interpolation points must be greater than 0");
    }
    this.noiseInterpolationPoints = noiseInterpolationPoints;
    this.stepSize = 1.0 / noiseInterpolationPoints;
    this.maxAmplitude = maxAmplitude;
    this.randomSeed = randomSeed;
  }

  public double getStepSize() {
    return stepSize;
  }

  public int getNoiseInterpolationPoints(){
    return noiseInterpolationPoints;
  }

  public C[] getNext(int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    return computeAtLeast(count);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RootNoiseGenerator<?> that = (RootNoiseGenerator<?>) o;
    return randomSeed == that.randomSeed &&
        Double.compare(that.stepSize, stepSize) == 0 &&
        Double.compare(that.maxAmplitude, maxAmplitude) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(randomSeed, stepSize, maxAmplitude);
  }

  @Override
  public double getMaxAmplitude() {
    return maxAmplitude;
  }

  public abstract int getNoiseSegmentLength();

  protected void assertValidValues(List<String> names, int... values) {
    for (int i = 0; i < values.length; i++) {
      if (values[i] < 0) {
        throw new IllegalArgumentException(
            String.format("%s must be greater than 0", names.get(i)));
      }
    }
  }

  protected abstract C[] generateNextSegment();

  protected abstract C[] getArrayOfSubType(int count);

  private C[] computeAtLeast(int count) {
    C[] results = getArrayOfSubType(count);
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
        C[] nextSegment = generateNextSegment();
        System.arraycopy(nextSegment, 0, results, currentIndex, noiseSegmentLength);
        currentIndex += noiseSegmentLength;
        remainingCount -= noiseSegmentLength;
      }
      C[] lastSegment = generateNextSegment();
      System.arraycopy(lastSegment, 0, results, currentIndex, remainingCount);
      C[] lastPortion = getArrayOfSubType(noiseSegmentLength - remainingCount);
      System.arraycopy(
          lastSegment, remainingCount, lastPortion, 0, noiseSegmentLength - remainingCount);
      generated.addAll(Arrays.asList(lastPortion));
    }
    return results;
  }
}
