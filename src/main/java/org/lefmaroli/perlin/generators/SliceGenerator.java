package org.lefmaroli.perlin.generators;

public interface SliceGenerator
    extends IRootGenerator<double[][]>, IMultiDimensionalGenerator, ISliceGeneratorDimension {

  double getWidthStepSize();

  double getHeightStepSize();
}
