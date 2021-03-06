package org.lefmaroli.perlin.generators;

import java.util.function.Supplier;

class LayerProcess<N, L extends IGenerator<N>> implements Supplier<N> {

  private final L layer;

  LayerProcess(L layer) {
    this.layer = layer;
  }

  @Override
  public N get() {
    var next = layer.getNext();
    if (Thread.currentThread().isInterrupted()) {
      throw new LayerProcessException(
          "Incomplete process due to interruption", new InterruptedException());
    }
    return next;
  }
}
