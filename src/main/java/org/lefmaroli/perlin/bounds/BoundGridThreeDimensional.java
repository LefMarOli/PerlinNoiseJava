package org.lefmaroli.perlin.bounds;

import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

class BoundGridThreeDimensional extends BoundGrid {

  private final VectorMultiD[][][] bounds;

  BoundGridThreeDimensional(int dimension, int numberOfBounds) {
    super(dimension);
    bounds = new VectorMultiD[numberOfBounds][numberOfBounds][numberOfBounds];
    var generator = new RandomGenerator(System.currentTimeMillis());
    for (var i = 0; i < numberOfBounds; i++) {
      for (var j = 0; j < numberOfBounds; j++) {
        for (int k = 0; k < numberOfBounds; k++) {
          bounds[i][j][k] = generator.getRandomUnitVectorOfDim(dimension);
        }
      }
    }
  }

  @Override
  VectorMultiD getBoundForCoordinatesSpecificDim(int... coordinates) {
    return bounds[wrapIndexToBounds(coordinates[0], bounds.length)][
        wrapIndexToBounds(coordinates[1], bounds.length)][
            wrapIndexToBounds(coordinates[2], bounds.length)];
  }
}
