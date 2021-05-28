package org.lefmaroli.perlin.generators;

public interface LineGenerator
    extends IRootGenerator<double[]>, IMultiDimensionalGenerator, ILineGeneratorDimension {

  double getLineStepSize();
}
