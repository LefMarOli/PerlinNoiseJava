package org.lefmaroli.random;

import org.junit.Test;
import org.lefmaroli.vector.UnitVector;

import static org.junit.Assert.assertEquals;

public class RandomGeneratorTest {

    @Test
    public void getRandomUnitVector2D() {

        UnitVector unitVector = new RandomGenerator().getRandomUnitVector2D();
        Double x = unitVector.getX();
        Double y = unitVector.getY();
        assertEquals(1.0, x * x + y * y, 1E-18);
    }
}