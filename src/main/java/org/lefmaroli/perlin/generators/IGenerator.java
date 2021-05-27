package org.lefmaroli.perlin.generators;

public interface IGenerator<R> {
  R getNext();

  double getMaxAmplitude();

  int getDimensions();

  int getTotalSize();
}
