package org.lefmaroli.perlin.generators.slice;

import org.lefmaroli.perlin.generators.ILayeredGenerator;
import org.lefmaroli.perlin.generators.dimensional.IMultiDimensionalGenerator;

public interface LayeredSliceGenerator
    extends ISliceGeneratorDimension, ILayeredGenerator<double[][]>, IMultiDimensionalGenerator {}
