package org.lefmaroli.perlin.bounds;

import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

class BoundGridOneDimensional extends BoundGrid {
  private final VectorMultiD[] bounds;

  BoundGridOneDimensional(int dimension, int numberOfBounds) {
    super(dimension);
    bounds = new VectorMultiD[numberOfBounds];
    var generator = new RandomGenerator(System.currentTimeMillis());
    for (var i = 0; i < numberOfBounds; i++) {
      bounds[i] = generator.getRandomUnitVectorOfDim(dimension);
    }
  }

  @Override
  VectorMultiD getBoundForCoordinatesSpecificDim(int[] coordinates, int[] boundIndices) {
    return bounds[wrapIndexToBounds(coordinates[0] + boundIndices[0], bounds.length)];
  }
}
