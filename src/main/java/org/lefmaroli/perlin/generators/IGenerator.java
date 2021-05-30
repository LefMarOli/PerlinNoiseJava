package org.lefmaroli.perlin.generators;

interface IGenerator<R> {
  R getNext();

  double getMaxAmplitude();

  int getDimensions();

  int getTotalSize();
}
