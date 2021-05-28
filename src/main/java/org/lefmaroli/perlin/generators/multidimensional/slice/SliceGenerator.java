package org.lefmaroli.perlin.generators.multidimensional.slice;

import org.lefmaroli.perlin.generators.IRootGenerator;
import org.lefmaroli.perlin.generators.multidimensional.IMultiDimensionalGenerator;

public interface SliceGenerator
    extends IRootGenerator<double[][]>, IMultiDimensionalGenerator, ISliceGeneratorDimension {

  double getWidthStepSize();

  double getHeightStepSize();
}
