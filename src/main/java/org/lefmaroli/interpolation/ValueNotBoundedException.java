package org.lefmaroli.interpolation;

public class ValueNotBoundedException extends IllegalArgumentException {

  private static final String SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT =
      "%s should be bounded between [0.0, 1.0]";

  public ValueNotBoundedException(String value) {
    super(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, value));
  }
}
