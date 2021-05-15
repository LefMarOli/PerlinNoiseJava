package org.lefmaroli.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lefmaroli.vector.VectorMultiD;

class RandomGeneratorTest {

  @Test
  void getRandomUnitVector2D() {
    VectorMultiD unitVector = new RandomGenerator().getRandomUnitVectorOfDim(2);
    Assertions.assertEquals(1.0, unitVector.getLength(), 1E-9);
  }
}
