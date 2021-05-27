package org.lefmaroli.perlin.generators.layers;

import java.util.function.Supplier;
import org.lefmaroli.perlin.generators.IGenerator;

public class LayerProcess<N, L extends IGenerator<N>> implements Supplier<N> {

  private final L layer;

  public LayerProcess(L layer) {
    this.layer = layer;
  }

  @Override
  public N get() {
    return layer.getNext();
  }
}
