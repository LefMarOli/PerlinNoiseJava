package org.lefmaroli.interpolation;

class CornerMatrixMultiD extends CornerMatrix {
  private final CornerMatrix[] subMatrices = new CornerMatrix[2];
  private final int dimension;

  CornerMatrixMultiD(int dimension) {
    this.dimension = dimension;
    if (dimension == 2) {
      for (var i = 0; i < 2; i++) {
        subMatrices[i] = new CornerMatrix1D();
      }
    } else {
      for (var i = 0; i < 2; i++) {
        subMatrices[i] = new CornerMatrixMultiD(dimension - 1);
      }
    }
  }

  @Override
  CornerMatrix getSubMatrix(int index) {
    return subMatrices[index];
  }

  @Override
  public void setValueAtIndices(double value, int... indices) {
    subMatrices[indices[indices.length - dimension]].setValueAtIndices(value, indices);
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  @Override
  protected String getStringRepresentationForIndices(int... indices) {
    int index = indices[indices.length - dimension];
    return "[" + index + "]" + subMatrices[index].getStringRepresentationForIndices(indices);
  }

  @Override
  public double get(int... indices) {
    return subMatrices[indices[indices.length - dimension]].get(indices);
  }
}
