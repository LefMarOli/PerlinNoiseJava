package org.lefmaroli.perlin.point;

import java.util.List;
import org.lefmaroli.execution.ExecutorServiceScheduler;
import org.lefmaroli.perlin.layers.LayeredNoiseGenerator;

public class LayeredPointGenerator extends LayeredNoiseGenerator<Double, PointNoiseGenerator>
    implements PointNoiseGenerator {

  LayeredPointGenerator(List<PointNoiseGenerator> layers) {
    super(layers, new ExecutorServiceScheduler(10));
  }

  @Override
  public String toString() {
    return "LayeredPointGenerator{"
        + "layers="
        + getLayers()
        + ", maxAmplitude="
        + getMaxAmplitude()
        + '}';
  }

  @Override
  protected Double[] initializeResults(int count) {
    var toInitialize = new Double[count];
    for (var i = 0; i < count; i++) {
      toInitialize[i] = 0.0;
    }
    return toInitialize;
  }

  @Override
  protected void addTogether(Double[] results, Double[] newLayer) {
    for (var i = 0; i < results.length; i++) {
      results[i] = results[i] + newLayer[i];
    }
  }

  @Override
  protected Double[] normalizeBy(Double[] data, double maxAmplitude) {
    for (var i = 0; i < data.length; i++) {
      data[i] = data[i] / maxAmplitude;
    }
    return data;
  }
}
