package org.lefmaroli.perlin.generators.point;

import java.util.Objects;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;
import org.lefmaroli.perlin.generators.IGenerator;
import org.lefmaroli.perlin.generators.RootBuilder;
import org.lefmaroli.perlin.generators.RootGenerator;

public class PointGeneratorBuilder
    extends RootBuilder<Double, PointGenerator, PointGeneratorBuilder> {

  public PointGeneratorBuilder() {
    super(1);
  }

  @Override
  public PointGenerator build() {
    return (PointGenerator) super.build();
  }

  @Override
  protected PointGeneratorBuilder self() {
    return this;
  }

  @Override
  protected IGenerator<Double> buildNoiseGenerator(
      double[] stepSizes, double amplitude, long randomSeed) {
    return new PointGeneratorImpl(stepSizes[0], amplitude, randomSeed);
  }

  private static class PointGeneratorImpl extends RootGenerator<Double> implements PointGenerator {

    private static final Logger LOGGER = LogManager.getLogger(PointGeneratorImpl.class);
    private final PerlinNoiseDataContainer perlinData;
    private double currentPosition;

    PointGeneratorImpl(double noiseStepSize, double maxAmplitude, long randomSeed) {
      super(noiseStepSize, maxAmplitude, randomSeed);
      this.currentPosition = new Random(randomSeed).nextDouble();
      perlinData = new PerlinNoiseDataContainerBuilder(1, randomSeed).createNewContainer();
      LOGGER.debug("Created new {}", this);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      return super.equals(o);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
      return "PointGeneratorImpl{"
          + "noiseStepSize="
          + getNoiseStepSize()
          + ", maxAmplitude="
          + getMaxAmplitude()
          + ", randomSeed="
          + randomSeed
          + '}';
    }

    @Override
    protected Double getNewContainer() {
      return 0.0;
    }

    @Override
    protected Double generateNextSegment(Double container) {
      currentPosition += getNoiseStepSize();
      perlinData.setCoordinatesForDimension(0, currentPosition);
      return PerlinNoise.getFor(perlinData) * getMaxAmplitude();
    }

    @Override
    public int getTotalSize() {
      return 1;
    }
  }
}
