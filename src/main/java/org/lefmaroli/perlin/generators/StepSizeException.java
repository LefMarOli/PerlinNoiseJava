package org.lefmaroli.perlin.generators;

class StepSizeException extends IllegalArgumentException {

  StepSizeException() {
    super("Step size smaller than 0");
  }
}
