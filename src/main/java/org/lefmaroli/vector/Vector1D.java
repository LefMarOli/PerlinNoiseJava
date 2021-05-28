package org.lefmaroli.vector;

class Vector1D extends AbstractDimensionalVector {

  private static final Vector1D NORMALIZED = new Vector1D(1.0);

  private final double x;

  public Vector1D(double x) {
    this.x = x;
  }

  @Override
  public Vector1D normalize() {
    return NORMALIZED;
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
