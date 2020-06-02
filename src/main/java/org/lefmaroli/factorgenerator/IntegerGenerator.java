package org.lefmaroli.factorgenerator;

public class IntegerGenerator extends MultipliedByNumberGenerator<Integer> {

  public IntegerGenerator(Integer initialValue, double factor) {
    super(initialValue, factor);
  }

  @Override
  public IntegerGenerator getCopy() {
    return new IntegerGenerator(initialValue, factor);
  }

  @Override
  protected Integer getXMultipliedByY(Integer x, double y) {
    return (int) (x * y);
  }
}
