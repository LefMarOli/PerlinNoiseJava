package org.lefmaroli.perlin.generators;

import java.util.concurrent.ForkJoinPool;

interface IMultiDimensionalGenerator {
  boolean isCircular();

  boolean hasParallelProcessingEnabled();

  ForkJoinPool getExecutionPool();
}
