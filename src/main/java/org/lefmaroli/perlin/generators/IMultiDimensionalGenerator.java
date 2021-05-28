package org.lefmaroli.perlin.generators;

import java.util.concurrent.ForkJoinPool;

public interface IMultiDimensionalGenerator {
  boolean isCircular();

  boolean hasParallelProcessingEnabled();

  ForkJoinPool getExecutionPool();
}
