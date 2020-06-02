package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.INoiseGenerator;

public interface PointNoiseGenerator extends INoiseGenerator<PointNoiseDataContainer> {

  @Override
  default int getDimensions() {
    return 1;
  }
}
