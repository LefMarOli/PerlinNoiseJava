package org.lefmaroli.interpolation;

public class DistancesArrayLengthException extends IllegalArgumentException {

  private static final String WRONG_LENGTH_DISTANCE_ARRAY_MSG_FORMAT =
      "Distances array should have %s values, ordered in %s dimensions";

  public DistancesArrayLengthException(int numberOfDimensions, String dimensionsOrder) {
    super(
        String.format(WRONG_LENGTH_DISTANCE_ARRAY_MSG_FORMAT, numberOfDimensions, dimensionsOrder));
  }
}
