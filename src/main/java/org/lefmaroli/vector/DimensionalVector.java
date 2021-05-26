package org.lefmaroli.vector;

public interface DimensionalVector {

  DimensionalVector normalize();

  int getDimension();

  double getLength();

  double getVectorProduct(double... other);
}
