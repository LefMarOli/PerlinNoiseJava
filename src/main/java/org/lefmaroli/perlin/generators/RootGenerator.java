package org.lefmaroli.perlin.generators;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

abstract class RootGenerator<C> implements IRootGenerator<C> {

  protected final long randomSeed;
  private final Queue<C> generated = new LinkedList<>();
  private final Queue<C> containers = new LinkedList<>();
  private final double timeStepSize;
  private final double maxAmplitude;
  private int containersCount = 0;

  protected RootGenerator(double timeStepSize, double maxAmplitude, long randomSeed) {
    if (timeStepSize < 0.0) {
      throw new IllegalArgumentException("Noise interpolation points must be greater than 0");
    }
    this.timeStepSize = timeStepSize;
    this.maxAmplitude = maxAmplitude;
    this.randomSeed = randomSeed;
  }

  protected static void assertValidValues(List<String> names, double... values) {
    for (var i = 0; i < values.length; i++) {
      if (values[i] < 0.0) {
        throw new IllegalArgumentException(
            String.format("%s must be greater than 0", names.get(i)));
      }
    }
  }

  @Override
  public double getTimeStepSize() {
    return timeStepSize;
  }

  public C getNext() {
    if (generated.isEmpty()) {
      addNextNoiseSegmentToQueue();
    }
    var container = generated.poll();
    containers.add(container);
    return container;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RootGenerator<?> that = (RootGenerator<?>) o;
    return randomSeed == that.randomSeed
        && Double.compare(that.timeStepSize, timeStepSize) == 0
        && Double.compare(that.maxAmplitude, maxAmplitude) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(randomSeed, timeStepSize, maxAmplitude);
  }

  @Override
  public double getMaxAmplitude() {
    return maxAmplitude;
  }

  protected abstract C generateNextSegment(C container);

  protected abstract C getNewContainer();

  private void addNextNoiseSegmentToQueue() {
    C container;
    if (containersCount < 2) {
      containersCount++;
      container = getNewContainer();
    } else {
      container = containers.poll();
    }
    generated.add(generateNextSegment(container));
  }
}
