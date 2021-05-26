package org.lefmaroli.perlin.point;

import java.util.Objects;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;
import org.lefmaroli.perlin.RootNoiseGenerator;

public class PointGenerator extends RootNoiseGenerator<Double> implements PointNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);
  private final PerlinNoiseDataContainer perlinData;
  private double currentPosition;

  public PointGenerator(double noiseStepSize, double maxAmplitude, long randomSeed) {
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
    return "PointGenerator{"
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
