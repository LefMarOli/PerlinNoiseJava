package org.lefmaroli.perlin.data;

public interface NoiseData<R, D extends NoiseData> {

  void add(D other);

  void normalizeBy(double maxValue);

  R getAsRawData();
}
