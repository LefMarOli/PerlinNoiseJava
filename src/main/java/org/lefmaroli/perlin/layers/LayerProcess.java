package org.lefmaroli.perlin.layers;

import org.lefmaroli.execution.JitterTask;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.data.NoiseData;

public class LayerProcess<N, L extends INoiseGenerator<N>> extends JitterTask<N[]> {

  private final L layer;
  private final int count;

  public LayerProcess(L layer, int count) {
    this.layer = layer;
    this.count = count;
  }

  @Override
  protected N[] process() {
    return layer.getNext(count);
  }
}
