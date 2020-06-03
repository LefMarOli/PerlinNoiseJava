package org.lefmaroli.perlin.data;

public interface NoiseDataContainer<R, D extends NoiseData<?, D>, U extends NoiseData<?, U>>
    extends NoiseData<R, D> {
  U[] getAsArray();
}
