package org.lefmaroli.interpolation;

public class CornerMatrixFactory {

  private CornerMatrixFactory(){}

  public static CornerMatrix getForDimension(int dimension) {
    if (dimension == 1) {
      return new CornerMatrix1D();
    } else {
      return new CornerMatrixMultiD(dimension);
    }
  }
}
