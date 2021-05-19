package org.lefmaroli.vector;

public class Vector1D extends AbstractDimensionalVector {

  private final double x;

  public Vector1D(double x) {
    this.x = x;
  }

  @Override
  public DimensionalVector normalize() {
    if (Double.compare(x, 1.0) == 0) {
      return this;
    } else {
      return new Vector1D(1.0);
    }
  }

  @Override
  public int getDimension() {
    return 1;
  }

  @Override
  public double getLength() {
    return x;
  }

  @Override
  protected double computeVectorProduct(double[] other) {
    return x * other[0];
  }
}
