package org.lefmaroli.perlin.generators;

public abstract class StepSizeException extends IllegalArgumentException {

  StepSizeException(String message) {
    super(message);
  }
}
