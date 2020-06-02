package org.lefmaroli.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.lefmaroli.vector.Vector2D;

public class RandomGeneratorTest {

  @Test
  public void getRandomUnitVector2D() {
    Vector2D unitVector = new RandomGenerator().getRandomUnitVector2D();
    assertEquals(1.0, unitVector.getLength(), 1E-9);
  }
}
