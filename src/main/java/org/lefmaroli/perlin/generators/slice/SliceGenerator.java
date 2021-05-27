package org.lefmaroli.perlin.generators.slice;

import org.lefmaroli.perlin.generators.IRootGenerator;
import org.lefmaroli.perlin.generators.dimensional.IMultiDimensionalGenerator;

public interface SliceGenerator
    extends IRootGenerator<double[][]>, IMultiDimensionalGenerator, ISliceGeneratorDimension {

  double getWidthStepSize();
  double getHeightStepSize();
}
