package org.lefmaroli.perlin.layers;

import java.util.function.Supplier;
import org.lefmaroli.perlin.INoiseGenerator;

public class LayerProcess<N, L extends INoiseGenerator<N>> implements Supplier<N> {

  private final L layer;

  public LayerProcess(L layer) {
    this.layer = layer;
  }

  @Override
  public N get() {
    return layer.getNext();
  }
}
