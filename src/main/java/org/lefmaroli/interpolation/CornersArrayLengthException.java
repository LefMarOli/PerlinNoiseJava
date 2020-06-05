package org.lefmaroli.interpolation;

public class CornersArrayLengthException extends IllegalArgumentException {

  private static final String CORNERS_ARRAY_WRONG_DIMENSIONS_MSG_FORMAT =
      "%s corners array should have %s dimensions of 2 length, ordered in %s dimensions";

  public CornersArrayLengthException(String cubeType, int dimensions, String dimensionsOrder) {
    super(
        String.format(
            CORNERS_ARRAY_WRONG_DIMENSIONS_MSG_FORMAT, cubeType, dimensions, dimensionsOrder));
  }
}
