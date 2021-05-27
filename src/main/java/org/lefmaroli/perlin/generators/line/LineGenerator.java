package org.lefmaroli.perlin.generators.line;

import org.lefmaroli.perlin.generators.IRootGenerator;
import org.lefmaroli.perlin.generators.dimensional.IMultiDimensionalGenerator;

public interface LineGenerator
    extends IRootGenerator<double[]>, IMultiDimensionalGenerator, ILineGeneratorDimension {

  double getLineStepSize();
}
