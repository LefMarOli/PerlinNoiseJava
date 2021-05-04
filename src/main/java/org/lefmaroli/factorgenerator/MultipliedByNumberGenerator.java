package org.lefmaroli.factorgenerator;

public abstract class MultipliedByNumberGenerator<N extends Number> implements NumberGenerator<N> {

  protected final double factor;
  protected final N initialValue;
  private N previousValue;
  private boolean firstCall = true;

  protected MultipliedByNumberGenerator(N initialValue, double factor) {
    this.initialValue = initialValue;
    this.factor = factor;
  }

  @Override
  public N getNext() {
    if (firstCall) {
      firstCall = false;
      previousValue = initialValue;
      return initialValue;
    } else {
      var newValue = getXMultipliedByY(previousValue, factor);
      previousValue = newValue;
      return newValue;
    }
  }

  @Override
  public void reset() {
    firstCall = true;
  }

  protected abstract N getXMultipliedByY(N x, double y);
}
