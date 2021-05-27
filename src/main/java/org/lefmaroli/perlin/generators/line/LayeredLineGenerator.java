package org.lefmaroli.perlin.generators.line;

import org.lefmaroli.perlin.generators.ILayeredGenerator;
import org.lefmaroli.perlin.generators.dimensional.IMultiDimensionalGenerator;

public interface LayeredLineGenerator extends ILineGeneratorDimension, ILayeredGenerator<double[]>,
    IMultiDimensionalGenerator {
}
