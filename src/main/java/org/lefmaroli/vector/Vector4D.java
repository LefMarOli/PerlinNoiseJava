package org.lefmaroli.vector;

public class Vector4D {

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

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public double getT() {
    return t;
  }

  public double getLength() {
    return Math.sqrt(x * x + y * y + z * z + t * t);
  }

  public double getVectorProduct(Vector4D other) {
    return getVectorProduct(other.x, other.y, other.z, other.t);
  }

  public double getCoordinatesProduct(double otherX){
    return this.x * otherX;
  }

  public double getCoordinatesProduct(double otherX, double otherY){
    return (this.x * otherX) + (this.y * otherY);
  }

  public double getCoordinatesProduct(double otherX, double otherY, double otherZ){
    return (this.x * otherX) + (this.y * otherY) + (this.z * otherZ);
  }

  public double getVectorProduct(double otherX, double otherY, double otherZ, double otherT) {
    return (this.x * otherX) + (this.y * otherY) + (this.z * otherZ) + (this.t * otherT);
  }

  public Vector4D normalize() {
    double length = getLength();
    if (length > 0.0) {
      return new Vector4D(x / length, y / length, z / length, t / length);
    } else {
      return this;
    }
  }

  @Override
  public String toString() {
    return "Vector4D{" + "x=" + x + ", y=" + y + ", z=" + z + ", t=" + t + '}';
  }
}
