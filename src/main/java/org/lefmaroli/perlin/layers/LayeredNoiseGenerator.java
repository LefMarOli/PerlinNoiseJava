package org.lefmaroli.perlin.layers;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import org.lefmaroli.perlin.INoiseGenerator;

public abstract class LayeredNoiseGenerator<N, L extends INoiseGenerator<N>>
    implements INoiseGenerator<N> {

  private final double maxAmplitude;
  private final List<L> layers;
  private final Queue<N> generated = new LinkedList<>();
  private final Queue<N> containers = new LinkedList<>();
  private int containersCount = 0;

  protected LayeredNoiseGenerator(List<L> layers) {
    if (layers.isEmpty()) {
      throw new IllegalArgumentException("Number of layers must at least be 1");
    }
    this.layers = layers;
    var sum = 0.0;
    for (L layer : layers) {
      sum += layer.getMaxAmplitude();
    }
    this.maxAmplitude = sum;
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
    if(containersCount < 2){
      containersCount++;
      container = getNewContainer();
    }else{
      container = containers.poll();
    }
    addNextToQueue(container);
    var nextValue = generated.poll();
    containers.add(nextValue);
    return nextValue;
  }

  private void addNextToQueue(N container){
    container = resetContainer(container);
    for (L layer : layers) {
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
