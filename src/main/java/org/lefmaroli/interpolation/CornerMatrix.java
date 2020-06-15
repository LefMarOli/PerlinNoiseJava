package org.lefmaroli.interpolation;

public abstract class CornerMatrix {

  public static CornerMatrix getForDimension(int dimension) {
    if (dimension == 1) {
      return new CornerMatrix1D();
    } else {
      return new CornerMatrixMultiD(dimension);
    }
  }

  @Override
  public String toString(){
    StringBuilder builder = new StringBuilder().append("CornerMatrix{");
    int[] indices = new int[getDimension()];
    int allRowsCount = 1 << getDimension();
    for (int i = 0; i < allRowsCount; i++) {
      builder.append("\n");
      for (int j = 0; j < getDimension(); j++) {
        indices[getDimension() - 1 - j] = (i >> j) & 1;
      }
      builder.append(getStringRepresentationForIndices(indices));
    }

    builder.append("}");
    return builder.toString();
  }



  abstract CornerMatrix getSubMatrix(int index);

  public abstract void setValueAtIndices(double value, int... indices);

  public abstract int getDimension();

  protected abstract String getStringRepresentationForIndices(int... indices);

  public abstract double get(int... indices);

  private static class CornerMatrix1D extends CornerMatrix {
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

  private static class CornerMatrixMultiD extends CornerMatrix {
    private final CornerMatrix[] subMatrices = new CornerMatrix[2];
    private final int dimension;

    CornerMatrixMultiD(int dimension) {
      this.dimension = dimension;
      if (dimension == 2) {
        for (int i = 0; i < 2; i++) {
          subMatrices[i] = new CornerMatrix1D();
        }
      } else {
        for (int i = 0; i < 2; i++) {
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
      subMatrices[indices[indices.length - getDimension()]].setValueAtIndices(value, indices);
    }

    @Override
    public int getDimension() {
      return dimension;
    }

    @Override
    protected String getStringRepresentationForIndices(int... indices) {
      int index = indices[indices.length - getDimension()];
      return "[" + index + "]" + subMatrices[index].getStringRepresentationForIndices(indices);
    }

    @Override
    public double get(int... indices) {
      return subMatrices[indices[indices.length - getDimension()]].get(indices);
    }
  }
}
