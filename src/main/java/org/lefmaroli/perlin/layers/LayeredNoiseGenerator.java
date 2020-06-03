package org.lefmaroli.perlin.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.lefmaroli.execution.TaskScheduler;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.data.NoiseData;

public abstract class LayeredNoiseGenerator<N extends NoiseData<?, N>, L extends INoiseGenerator<N>>
    implements INoiseGenerator<N> {

  private final double maxAmplitude;
  private final TaskScheduler scheduler;
  private final List<L> layers;

  protected LayeredNoiseGenerator(List<L> layers, TaskScheduler scheduler) {
    if (layers.isEmpty()) {
      throw new IllegalArgumentException("Number of layers must at least be 1");
    }
    this.layers = layers;
    double sum = 0.0;
    for (L layer : layers) {
      sum += layer.getMaxAmplitude();
    }
    this.maxAmplitude = sum;
    this.scheduler = scheduler;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LayeredNoiseGenerator<?, ?> that = (LayeredNoiseGenerator<?, ?>) o;
    return Double.compare(that.maxAmplitude, maxAmplitude) == 0
        && Objects.equals(layers, that.layers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxAmplitude, layers);
  }

  @Override
  public N getNext(int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Parameter count must be greater than 0");
    }
    return generateResults(count);
  }

  public double getMaxAmplitude() {
    return maxAmplitude;
  }

  public int getNumberOfLayers() {
    return layers.size();
  }

  protected List<L> getLayers() {
    return layers;
  }

  protected abstract N initializeResults(int count);

  private N generateResults(int count) {
    N results = initializeResults(count);
    List<CompletableFuture<N>> futures = new ArrayList<>(layers.size());
    for (L layer : layers) {
      LayerProcess<N, L> layerProcess = new LayerProcess<>(layer, count);
      futures.add(scheduler.schedule(layerProcess));
    }
    for (CompletableFuture<N> future : futures) {
      try {
        results.add(future.get());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        throw new LayerProcessException(e);
      }
    }
    results.normalizeBy(maxAmplitude);
    return results;
  }
}
