package org.lefmaroli.perlin;

import org.lefmaroli.perlin.data.NoiseData;

public interface INoiseGenerator<R extends NoiseData> {
  R getNext(int count);

  double getMaxAmplitude();

  int getDimensions();
}
