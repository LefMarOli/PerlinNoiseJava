package org.lefmaroli.perlin;

public interface INoiseGenerator<R> {
  R[] getNext(int count);

  double getMaxAmplitude();

  int getDimensions();
}
