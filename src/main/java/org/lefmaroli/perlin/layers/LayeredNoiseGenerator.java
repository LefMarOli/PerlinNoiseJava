package org.lefmaroli.perlin.layers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.lefmaroli.perlin.INoiseGenerator;

public abstract class LayeredNoiseGenerator<N, L extends INoiseGenerator<N>>
    implements INoiseGenerator<N> {

  private static final int SIZE_THRESHOLD = 2500;
  private static final int DEFAULT_TIMEOUT = 5;
  private final double maxAmplitude;
  private final List<L> layers;
  private final Queue<N> generated = new LinkedList<>();
  private final Queue<N> containers = new LinkedList<>();
  private final List<CompletableFuture<N>> futures;
  private int containersCount = 0;
  private final int totalSize;
  private final long timeout;
  private final ExecutorService executorService;

  protected LayeredNoiseGenerator(List<L> layers, ExecutorService executorService) {
    if (layers.isEmpty()) {
      throw new IllegalArgumentException("Number of layers must at least be 1");
    }
    this.layers = layers;
    this.futures = new ArrayList<>(layers.size());
    var sum = 0.0;
    for (L layer : layers) {
      sum += layer.getMaxAmplitude();
    }
    this.maxAmplitude = sum;
    var size = 0;
    for (L layer : layers) {
      size += layer.getTotalSize();
    }
    this.totalSize = size;
    this.timeout = (long) totalSize * DEFAULT_TIMEOUT / SIZE_THRESHOLD;
    this.executorService = executorService;
  }

  private boolean hasParallelProcessingEnabled() {
    return executorService != null;
  }

  @Override
  public int getTotalSize() {
    return totalSize;
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
  public N getNext() {
    N container;
    if (containersCount < 2) {
      containersCount++;
      container = getNewContainer();
    } else {
      container = containers.poll();
    }
    if (Thread.interrupted()) {
      return container;
    }
    addNextToQueue(container);
    var nextValue = generated.poll();
    containers.add(nextValue);
    return nextValue;
  }

  private void addNextToQueue(N container) {
    container = resetContainer(container);
    if (totalSize > SIZE_THRESHOLD && hasParallelProcessingEnabled()) {
      processParallel(container);
    } else {
      processSerial(container);
    }
  }

  private void processParallel(N container) {
    for (L layer : layers) {
      if (Thread.interrupted()) {
        return;
      }
      futures.add(CompletableFuture.supplyAsync(new LayerProcess<>(layer), executorService));
    }
    for (CompletableFuture<N> f : futures) {
      if (Thread.interrupted()) {
        return;
      }
      try {
        container = addTogether(container, f.get(timeout, TimeUnit.MILLISECONDS));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LayerProcessException("Interrupted while waiting for layer process", e);
      } catch (ExecutionException e) {
        throw new LayerProcessException("Execution exception in layer process", e);
      } catch (TimeoutException e) {
        throw new LayerProcessException(
            "Timeout of " + timeout + "ms reached before layer process completion", e);
      }
    }
    generated.add(normalizeBy(container, maxAmplitude));
    futures.clear();
  }

  private void processSerial(N container) {
    for (L layer : layers) {
      if (Thread.interrupted()) {
        return;
      }
      container = addTogether(container, layer.getNext());
    }
    generated.add(normalizeBy(container, maxAmplitude));
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

  protected abstract N getNewContainer();

  protected abstract N resetContainer(N container);

  protected abstract N addTogether(N results, N newLayer);

  protected abstract N normalizeBy(N data, double maxAmplitude);
}
