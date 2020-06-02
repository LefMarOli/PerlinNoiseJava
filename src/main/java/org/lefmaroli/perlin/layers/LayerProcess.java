package org.lefmaroli.perlin.layers;

import org.lefmaroli.execution.JitterTask;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.data.NoiseData;

public class LayerProcess<
        ResultType extends NoiseData, LayerType extends INoiseGenerator<ResultType>>
    extends JitterTask<ResultType> {

  private final LayerType layer;
  private final int count;

  public LayerProcess(LayerType layer, int count) {
    this.layer = layer;
    this.count = count;
  }

  @Override
  protected ResultType process() {
    return layer.getNext(count);
  }
}
