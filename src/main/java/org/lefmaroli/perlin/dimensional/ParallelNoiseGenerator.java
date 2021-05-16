package org.lefmaroli.perlin.dimensional;

import java.util.concurrent.CompletableFuture;
import org.lefmaroli.execution.ExecutorPool;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;

public abstract class ParallelNoiseGenerator<C> extends MultiDimensionalRootNoiseGenerator<C> {

  private final ExecutorPool executorPool;

  protected ParallelNoiseGenerator(double noiseStepSize, double maxAmplitude,
      long randomSeed, boolean isCircular, ExecutorPool executorPool) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular);
    this.executorPool = executorPool;
  }

  protected CompletableFuture<Double> submitNewPerlinTask(PerlinNoise perlinNoise,
      PerlinNoiseDataContainer dataContainer){
    return CompletableFuture.supplyAsync(()->perlinNoise.getFor(dataContainer), executorPool);
  }


}
