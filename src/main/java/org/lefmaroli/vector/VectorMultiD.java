package org.lefmaroli.vector;

public class VectorMultiD {
  private final double[] coordinates;

  public VectorMultiD(double... coordinates) {
    this.coordinates = coordinates;
  }

  public VectorMultiD normalize() {
    double length = getLength();
    if (Double.compare(length, 1.0) != 0) {
      for (var i = 0; i < coordinates.length; i++) {
        coordinates[i] = coordinates[i] / length;
      }
    }
    return this;
  }

  public double getLength() {
    var result = 0.0;
    for (double coordinate : coordinates) {
      result += coordinate * coordinate;
    }
    return Math.sqrt(result);
  }

  public double getVectorProduct(double... other) {
    var result = 0.0;
    for (var i = 0; i < coordinates.length; i++) {
      result += coordinates[i] * other[i];
    }
    return result;
  }
}
