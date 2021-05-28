package org.lefmaroli.perlin.bounds;

public class BoundGridFactory {

  private BoundGridFactory() {}

  private static final int MAX_DIMENSION = 5;

  public static BoundGrid getNewBoundGridForDimension(int dimension, int numberOfBounds) {
    if (numberOfBounds < 1) {
      throw new IllegalArgumentException(
          "Number of bounds should be greater than 0, provided " + numberOfBounds);
    }
    if (!isPowerOfTwo(numberOfBounds)) {
      throw new IllegalArgumentException(
          "Only power-of-two number of bounds allowed for faster processing, provided "
              + numberOfBounds);
    }
    return switch (dimension) {
      case 1 -> new BoundGridOneDimensional(dimension, numberOfBounds);
      case 2 -> new BoundGridTwoDimensional(dimension, numberOfBounds);
      case 3 -> new BoundGridThreeDimensional(dimension, numberOfBounds);
      case 4 -> new BoundGridFourDimensional(dimension, numberOfBounds);
      case 5 -> new BoundGridFiveDimensional(dimension, numberOfBounds);
      default -> throw new IllegalArgumentException(
          "Dimension should be in range [1, " + MAX_DIMENSION + "]");
    };
  }

  private static boolean isPowerOfTwo(int numberOfBounds) {
    return (numberOfBounds & (numberOfBounds - 1)) == 0;
  }
}
