package org.lefmaroli.vector;

public class Vector3D extends AbstractDimensionalVector {

  private final double x;
  private final double y;
  private final double z;

  public Vector3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public int getDimension() {
    return 3;
  }

  @Override
  public Vector3D normalize() {
    double length = getLength();
    if (Math.abs(length - 1.0) < 1E-10) {
      return this;
    }
    return new Vector3D(x / length, y / length, z / length).normalize();
  }

  @Override
  public double getLength() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  @Override
  protected double computeVectorProduct(double[] other) {
    return (x * other[0]) + (y * other[1]) + (z * other[2]);
  }
}
