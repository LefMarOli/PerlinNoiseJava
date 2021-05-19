package org.lefmaroli.vector;

public abstract class AbstractDimensionalVector implements DimensionalVector{

  @Override
  public double getVectorProduct(double... other) {
    if (other.length != getDimension()) {
      throw new IllegalArgumentException(
          "Vector product impossible, parameter length ("
              + other.length
              + ") different than vector length of " + getDimension());
    }
    return computeVectorProduct(other);
  }

  protected abstract double computeVectorProduct(double[] other);
}
