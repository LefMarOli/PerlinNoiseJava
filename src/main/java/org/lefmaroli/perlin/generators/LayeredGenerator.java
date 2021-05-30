package org.lefmaroli.perlin.generators;

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
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.perlin.configuration.JitterStrategy;

abstract class LayeredGenerator<N> implements ILayeredGenerator<N> {

  private static final int SIZE_THRESHOLD = 2500;
  private final double maxAmplitude;
  private final List<IGenerator<N>> layers;
  private final Queue<N> generated = new LinkedList<>();
  private final Queue<N> containers = new LinkedList<>();
  private final List<CompletableFuture<N>> futures;
  private int containersCount = 0;
  private final int totalSize;
  private final long timeout;
  private final ExecutorService executorService;
  private boolean emittedExecutorShutdownWarning = false;

  protected LayeredGenerator(
      List<? extends IGenerator<N>> layers,
      ExecutorService executorService,
      JitterStrategy jitterStrategy) {
    if (layers.isEmpty()) {
      throw new IllegalArgumentException("Number of layers must at least be 1");
    }
    this.layers = new ArrayList<>(layers.size());
    this.layers.addAll(layers);
    this.futures = new ArrayList<>(layers.size());
    var sum = 0.0;
    for (IGenerator<N> layer : layers) {
      sum += layer.getMaxAmplitude();
    }
    this.maxAmplitude = sum;
    var size = 0;
    for (IGenerator<N> layer : layers) {
      size += layer.getTotalSize();
    }
    this.totalSize = size;
    this.timeout = (long) totalSize * jitterStrategy.getTimeout() / SIZE_THRESHOLD;
    this.executorService = executorService;
  }

  private boolean hasParallelProcessingEnabled() {
    if (executorService == null) {
      return false;
    }
    if (executorService.isShutdown()) {
      if (!emittedExecutorShutdownWarning) {
        LogManager.getLogger(this.getClass())
            .warn("Provided executorService is already shutdown, processing in serial mode");
        emittedExecutorShutdownWarning = true;
      }
      return false;
    }
    return true;
  }

  @Override
  public int getTotalSize() {
    return totalSize;
  }

  @Override
  public int getNumberOfLayers() {
    return layers.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LayeredGenerator<?> that = (LayeredGenerator<?>) o;
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
    if (Thread.currentThread().isInterrupted()) {
      LogManager.getLogger(this.getClass()).debug("Interrupted processing [getNext()]");
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
      try {
        processParallel(container);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LayerProcessException("Interrupted while doing parallel processing", e);
      }
    } else {
      processSerial(container);
    }
  }

  private void processParallel(N container) throws InterruptedException {
    for (IGenerator<N> layer : layers) {
      futures.add(CompletableFuture.supplyAsync(new LayerProcess<>(layer), executorService));
    }
    for (CompletableFuture<N> f : futures) {
      if (Thread.currentThread().isInterrupted()) {
        LogManager.getLogger(this.getClass()).debug("Interrupted processing [processParallel]");
        return;
      }
      try {
        container = addTogether(container, f.get(timeout, TimeUnit.MILLISECONDS));
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
    for (IGenerator<N> layer : layers) {
      if (Thread.currentThread().isInterrupted()) {
        LogManager.getLogger(this.getClass()).debug("Interrupted processing [processSerial]");
        return;
      }
      container = addTogether(container, layer.getNext());
    }
    generated.add(normalizeBy(container, maxAmplitude));
  }

  public double getMaxAmplitude() {
    return maxAmplitude;
  }

  protected List<IGenerator<N>> getLayers() {
    return layers;
  }

  protected abstract N getNewContainer();

  protected abstract N resetContainer(N container);

  protected abstract N addTogether(N results, N newLayer);

  protected abstract N normalizeBy(N data, double maxAmplitude);
}
