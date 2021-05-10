package org.lefmaroli.perlin.exceptions;

public class NoStepSizeException extends StepSizeException {

  private static final String NO_INTERPOLATION_MESSAGE =
      "Step size smaller than 0";

  public NoStepSizeException() {
    super(NO_INTERPOLATION_MESSAGE);
  }
}
