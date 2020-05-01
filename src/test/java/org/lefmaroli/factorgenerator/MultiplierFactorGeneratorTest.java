package org.lefmaroli.factorgenerator;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MultiplierFactorGeneratorTest {

    @Test
    public void getNextFactor() {
        MultiplierFactorGenerator generator = new MultiplierFactorGenerator(0.0, 5489.5946);
        for (int i = 0; i < 50000; i++) {
            assertEquals(0.0, generator.getNextFactor(), 0.0);
        }
    }

    @Test
    public void getNextFactorTest2() {
        Random rand = new Random(System.currentTimeMillis());
        double initialValue = rand.nextDouble();
        double factor = rand.nextDouble() * 500;
        MultiplierFactorGenerator generator = new MultiplierFactorGenerator(initialValue, factor);
        assertEquals(initialValue, generator.getNextFactor(), 0.0);
        double expectedValue = initialValue * factor;
        for (int i = 0; i < 50000; i++) {
            assertEquals(expectedValue, generator.getNextFactor(), 0.0);
            expectedValue *= factor;
        }
    }

    @Test
    public void getNextFactorTest3() {
        Random rand = new Random(System.currentTimeMillis());
        double initialValue = rand.nextDouble();
        double factor = rand.nextDouble() * 500 * -1;
        MultiplierFactorGenerator generator = new MultiplierFactorGenerator(initialValue, factor);
        assertEquals(initialValue, generator.getNextFactor(), 0.0);
        double expectedValue = initialValue * factor;
        for (int i = 0; i < 50000; i++) {
            assertEquals(expectedValue, generator.getNextFactor(), 0.0);
            expectedValue *= factor;
        }
    }
}