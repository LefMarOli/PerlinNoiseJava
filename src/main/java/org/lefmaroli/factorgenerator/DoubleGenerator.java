package org.lefmaroli.factorgenerator;

public class DoubleGenerator extends MultipliedByNumberGenerator<Double> {
  public DoubleGenerator(Double initialValue, double factor) {
    super(initialValue, factor);
  }

  @Override
  public DoubleGenerator getCopy() {
    return new DoubleGenerator(initialValue, factor);
  }

  @Override
  protected Double getXMultipliedByY(Double x, double y) {
    return x * y;
  }
}
