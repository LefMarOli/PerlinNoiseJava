package org.lefmaroli.perlin;

public interface INoiseGenerator<R> {
  R getNext();

  double getMaxAmplitude();

  int getDimensions();
}
