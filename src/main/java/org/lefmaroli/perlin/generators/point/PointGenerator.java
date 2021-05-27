package org.lefmaroli.perlin.generators.point;

import org.lefmaroli.perlin.generators.IRootGenerator;

public interface PointGenerator extends IRootGenerator<Double> {

  @Override
  default int getDimensions() {
    return 1;
  }
}
