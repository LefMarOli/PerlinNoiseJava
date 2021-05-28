package org.lefmaroli.perlin.generators.multidimensional.slice;

import org.lefmaroli.perlin.generators.ILayeredGenerator;
import org.lefmaroli.perlin.generators.multidimensional.IMultiDimensionalGenerator;

public interface LayeredSliceGenerator
    extends ISliceGeneratorDimension, ILayeredGenerator<double[][]>, IMultiDimensionalGenerator {}
