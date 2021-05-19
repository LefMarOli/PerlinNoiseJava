package org.lefmaroli.vector;

public interface DimensionalVector {

  DimensionalVector normalize();

  int getDimension();

  double getLength();

  double getVectorProduct(double... other);
  //  {
  //    var result = 0.0;
  //    for (var i = 0; i < coordinates.length; i++) {
  //      result += coordinates[i] * other[i];
  //    }
  //    return result;
  //  }
}
