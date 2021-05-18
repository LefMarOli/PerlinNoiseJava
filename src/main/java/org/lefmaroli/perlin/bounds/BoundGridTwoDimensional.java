package org.lefmaroli.perlin.bounds;

import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

class BoundGridTwoDimensional extends BoundGrid {

  private final VectorMultiD[][] bounds;

  BoundGridTwoDimensional(int dimension, int numberOfBounds) {
    super(dimension);
    bounds = new VectorMultiD[numberOfBounds][numberOfBounds];
    var generator = new RandomGenerator(System.currentTimeMillis());
    for (var i = 0; i < numberOfBounds; i++) {
      for (var j = 0; j < numberOfBounds; j++) {
        bounds[i][j] = generator.getRandomUnitVectorOfDim(dimension);
      }
    }
  }

  @Override
  VectorMultiD getBoundForCoordinatesSpecificDim(int[] coordinates, int[] boundIndices) {
    return bounds[wrapIndexToBounds(coordinates[0] + boundIndices[0], bounds.length)][
        wrapIndexToBounds(coordinates[1] + boundIndices[1], bounds.length)];
  }
}
