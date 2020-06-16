package org.lefmaroli.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.lefmaroli.vector.Vector2D;
import org.lefmaroli.vector.VectorMultiD;

public class RandomGeneratorTest {

  @Test
  public void getRandomUnitVector2D() {
    VectorMultiD unitVector = new RandomGenerator().getRandomUnitVectorOfDim(2);
    assertEquals(1.0, unitVector.getLength(), 1E-9);
  }
}
