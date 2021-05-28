package org.lefmaroli.perlin.generators.multidimensional;

import java.util.concurrent.ForkJoinPool;

public interface IMultiDimensionalGenerator {
  boolean isCircular();

  boolean hasParallelProcessingEnabled();

  ForkJoinPool getExecutionPool();
}
