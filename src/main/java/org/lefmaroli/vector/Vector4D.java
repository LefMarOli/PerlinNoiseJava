package org.lefmaroli.vector;

public class Vector4D extends AbstractDimensionalVector {

  private final double x;
  private final double y;
  private final double z;
  private final double t;

  public Vector4D(double x, double y, double z, double t) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.t = t;
  }

  @Override
  public int getDimension() {
    return 4;
  }

  @Override
  public Vector4D normalize() {
    double length = getLength();
    if (Math.abs(length - 1.0) < 1E-10) {
      return this;
    }
    return new Vector4D(x / length, y / length, z / length, t / length).normalize();
  }

  @Override
  public double getLength() {
    return Math.sqrt(x * x + y * y + z * z + t * t);
  }

  @Override
  protected double computeVectorProduct(double[] other) {
    return (x * other[0]) + (y * other[1]) + (z * other[2]) + (t * other[3]);
  }
}
