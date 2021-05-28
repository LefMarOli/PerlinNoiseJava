package org.lefmaroli.vector;

public class Vector2D extends AbstractDimensionalVector {

  private final double x;
  private final double y;

  public Vector2D(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int getDimension() {
    return 2;
  }

  @Override
  public Vector2D normalize() {
    double length = getLength();
    if (Math.abs(length - 1.0) < 1E-10) {
      return this;
    }
    return new Vector2D(x / length, y / length).normalize();
  }

  @Override
  public double getLength() {
    return Math.sqrt(x * x + y * y);
  }

  @Override
  protected double computeVectorProduct(double[] other) {
    return (x * other[0]) + (y * other[1]);
  }
}
