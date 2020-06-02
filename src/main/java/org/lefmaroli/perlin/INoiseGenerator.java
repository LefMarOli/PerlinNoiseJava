package org.lefmaroli.perlin;

import org.lefmaroli.perlin.data.NoiseData;

public interface INoiseGenerator<ReturnType extends NoiseData> {
  ReturnType getNext(int count);

  double getMaxAmplitude();

  int getDimensions();
}
