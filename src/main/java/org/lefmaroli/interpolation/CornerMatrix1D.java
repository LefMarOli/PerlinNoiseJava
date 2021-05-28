package org.lefmaroli.interpolation;

class CornerMatrix1D extends CornerMatrix {
  private final double[] data = new double[2];

  @Override
  CornerMatrix getSubMatrix(int index) {
    throw new IllegalArgumentException("Cannot get sub-matrix, dimension 1");
  }

  @Override
  public void setValueAtIndices(double value, int... indices) {
    data[indices[indices.length - getDimension()]] = value;
  }

  @Override
  public double get(int... indices) {
    return data[indices[indices.length - getDimension()]];
  }

  @Override
  public int getDimension() {
    return 1;
  }

  @Override
  protected String getStringRepresentationForIndices(int... indices) {
    int index = indices[indices.length - getDimension()];
    return "[" + index + "] = " + data[index];
  }
}
