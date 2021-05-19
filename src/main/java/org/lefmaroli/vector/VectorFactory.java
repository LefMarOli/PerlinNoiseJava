package org.lefmaroli.vector;

public class VectorFactory {

  public static DimensionalVector getVectorForCoordinates(double[] coordinates) {
    if (coordinates.length > 5 || coordinates.length == 0) {
      throw new IllegalArgumentException("Coordinates length should be between 1 and 5");
    }

    return switch (coordinates.length) {
      case 1 -> new Vector1D(coordinates[0]);
      case 2 -> new Vector2D(coordinates[0], coordinates[1]);
      case 3 -> new Vector3D(coordinates[0], coordinates[1], coordinates[2]);
      case 4 -> new Vector4D(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
      case 5 -> new Vector5D(
          coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4]);
      default -> throw new IllegalArgumentException("Won't happen");
    };
  }
}
