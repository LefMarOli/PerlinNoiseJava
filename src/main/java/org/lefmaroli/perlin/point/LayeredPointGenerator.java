package org.lefmaroli.perlin.point;

import java.util.List;
import org.lefmaroli.perlin.layers.LayeredNoiseGenerator;

public class LayeredPointGenerator extends LayeredNoiseGenerator<Double, PointNoiseGenerator>
    implements PointNoiseGenerator {

  LayeredPointGenerator(List<PointNoiseGenerator> layers) {
    super(layers);
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
  protected Double getNewContainer() {
    return 0.0;
  }

  @Override
  protected Double resetContainer(Double container){
    return 0.0;
  }

  @Override
  protected Double addTogether(Double results, Double newLayer) {
    return results + newLayer;
  }

  @Override
  protected Double normalizeBy(Double data, double maxAmplitude) {
    return data / maxAmplitude;
  }
}
