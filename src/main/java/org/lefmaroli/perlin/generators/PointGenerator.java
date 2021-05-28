package org.lefmaroli.perlin.generators;

public interface PointGenerator extends IRootGenerator<Double> {

  @Override
  default int getDimensions() {
    return 1;
  }
}
