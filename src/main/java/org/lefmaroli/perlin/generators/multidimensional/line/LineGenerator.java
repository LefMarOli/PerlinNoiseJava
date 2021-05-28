package org.lefmaroli.perlin.generators.multidimensional.line;

import org.lefmaroli.perlin.generators.IRootGenerator;
import org.lefmaroli.perlin.generators.multidimensional.IMultiDimensionalGenerator;

public interface LineGenerator
    extends IRootGenerator<double[]>, IMultiDimensionalGenerator, ILineGeneratorDimension {

  double getLineStepSize();
}
