package org.lefmaroli.perlin.generators;

public class StepSizeException extends IllegalArgumentException {

  StepSizeException() {
    super("Step size smaller than 0");
  }
}
