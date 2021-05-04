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

  protected RootNoiseGenerator(int noiseInterpolationPoints, double maxAmplitude, long randomSeed) {
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

  public int getNoiseInterpolationPoints() {
    return noiseInterpolationPoints;
  }

  public C getNext() {
    if (generated.size() - 1 < noiseInterpolationPoints) {
      addNextNoiseSegmentToQueue();
    }
    return generated.poll();
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
    return randomSeed == that.randomSeed
        && Double.compare(that.stepSize, stepSize) == 0
        && Double.compare(that.maxAmplitude, maxAmplitude) == 0;
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

  protected static void assertValidValues(List<String> names, int... values) {
    for (var i = 0; i < values.length; i++) {
      if (values[i] < 0) {
        throw new IllegalArgumentException(
            String.format("%s must be greater than 0", names.get(i)));
      }
    }
  }

  protected abstract C[] generateNextSegment();

  private void addNextNoiseSegmentToQueue() {
    generated.addAll(Arrays.asList(generateNextSegment()));
  }
}
