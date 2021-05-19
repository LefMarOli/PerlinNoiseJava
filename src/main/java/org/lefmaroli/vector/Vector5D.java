package org.lefmaroli.vector;

public class Vector5D extends AbstractDimensionalVector {

  private final double x;
  private final double y;
  private final double z;
  private final double t;
  private final double w;

  public Vector5D(double x, double y, double z, double t, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.t = t;
    this.w = w;
  }

  @Override
  public int getDimension() {
    return 5;
  }

  @Override
  public DimensionalVector normalize() {
    double length = getLength();
    if (Double.compare(length, 1.0) == 0) {
      return this;
    }
    return new Vector5D(x / length, y / length, z / length, t / length, w / length);
  }

  @Override
  public double getLength() {
    return Math.sqrt(x * x + y * y + z * z + t * t + w * w);
  }

  @Override
  protected double computeVectorProduct(double[] other) {
    return (x * other[0]) + (y * other[1]) + (z * other[2]) + (t * other[3]) + (w * other[4]);
  }
}
