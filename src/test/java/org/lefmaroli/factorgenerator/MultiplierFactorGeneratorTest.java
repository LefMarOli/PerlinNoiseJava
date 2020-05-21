package org.lefmaroli.factorgenerator;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MultiplierFactorGeneratorTest {

    @Test
    public void getNextFactorShouldAlwaysBeZero() {
        MultiplierFactorGenerator generator = new MultiplierFactorGenerator(0.0, 5489.5946);
        for (int i = 0; i < 50000; i++) {
            assertEquals(0.0, generator.getNextFactor(), 0.0);
        }
    }

    @Test
    public void getNextFactorTestMultipliedByFactor() {
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
    public void getNextFactorTestAlternatePositiveNegative() {
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

    @Test
    public void testReset(){
        MultiplierFactorGenerator generator = new MultiplierFactorGenerator(new Random().nextInt(), 5489.5946);
        double firstValue = generator.getNextFactor();
        for (int i = 0; i < 50000; i++) {
            generator.reset();  //Always try to get the first value
            assertEquals(firstValue, generator.getNextFactor(), 0.0);
        }
        for (int i = 0; i < 50000; i++) {
            assertNotEquals(firstValue, generator.getNextFactor(), 0.0);
        }
    }
}