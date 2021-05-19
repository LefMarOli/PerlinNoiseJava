package org.lefmaroli.perlin.dimensional;

import java.util.concurrent.ForkJoinPool;

public interface MultiDimensionalNoiseGenerator {
  boolean isCircular();
  boolean hasParallelProcessingEnabled();
  ForkJoinPool getExecutionPool();
}
