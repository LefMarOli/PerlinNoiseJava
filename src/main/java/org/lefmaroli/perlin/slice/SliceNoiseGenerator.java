package org.lefmaroli.perlin.slice;

import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseGenerator;

public interface SliceNoiseGenerator
    extends INoiseGenerator<double[][]>, MultiDimensionalNoiseGenerator {

  @Override
  default int getDimensions() {
    return 3;
  }

  int getSliceWidth();

  int getSliceHeight();
}
