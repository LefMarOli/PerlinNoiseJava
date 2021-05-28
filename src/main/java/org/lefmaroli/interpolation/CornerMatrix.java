package org.lefmaroli.interpolation;

public abstract class CornerMatrix {

  @Override
  public String toString() {
    var builder = new StringBuilder("CornerMatrix{");
    var indices = new int[getDimension()];
    int allRowsCount = 1 << getDimension();
    for (var i = 0; i < allRowsCount; i++) {
      builder.append(System.lineSeparator());
      for (var j = 0; j < getDimension(); j++) {
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
}
