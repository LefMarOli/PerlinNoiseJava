package org.lefmaroli.factorgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Random;
import org.junit.Test;

public class MultipliedByNumberGeneratorTest {

  @Test
  public void getNextFactorShouldAlwaysBeZero() {
    DoubleGenerator generator = new DoubleGenerator(0.0, 5489.5946);
    for (int i = 0; i < 50000; i++) {
      assertEquals(0.0, generator.getNext(), 0.0);
    }
  }

  @Test
  public void getNextFactorTestMultipliedByFactor() {
    Random rand = new Random(System.currentTimeMillis());
    double initialValue = rand.nextDouble();
    double factor = rand.nextDouble() * 500;
    DoubleGenerator generator = new DoubleGenerator(initialValue, factor);
    assertEquals(initialValue, generator.getNext(), 0.0);
    double expectedValue = initialValue * factor;
    for (int i = 0; i < 50000; i++) {
      assertEquals(expectedValue, generator.getNext(), 0.0);
      expectedValue *= factor;
    }
  }

  @Test
  public void getNextFactorTestAlternatePositiveNegative() {
    Random rand = new Random(System.currentTimeMillis());
    double initialValue = rand.nextDouble();
    double factor = rand.nextDouble() * 500 * -1;
    DoubleGenerator generator = new DoubleGenerator(initialValue, factor);
    assertEquals(initialValue, generator.getNext(), 0.0);
    double expectedValue = initialValue * factor;
    for (int i = 0; i < 50000; i++) {
      assertEquals(expectedValue, generator.getNext(), 0.0);
      expectedValue *= factor;
    }
  }

  @Test
  public void testReset() {
    IntegerGenerator generator = new IntegerGenerator(new Random().nextInt(), 5489.5946);
    double firstValue = generator.getNext();
    for (int i = 0; i < 50000; i++) {
      generator.reset(); // Always try to get the first value
      assertEquals(firstValue, generator.getNext(), 0.0);
    }
    for (int i = 0; i < 50000; i++) {
      assertNotEquals(firstValue, generator.getNext(), 0.0);
    }
  }

  @Test
  public void testDoubleCopy() {
    Random random = new Random();
    double initialValue = random.nextDouble();
    double factor = random.nextDouble();
    DoubleGenerator generator = new DoubleGenerator(initialValue, factor);
    DoubleGenerator copy = generator.getCopy();
    assertEquals(initialValue, copy.initialValue, 0.0);
    assertEquals(factor, copy.factor, 0.0);
  }

  @Test
  public void testIntegerCopy() {
    Random random = new Random();
    int initialValue = random.nextInt();
    double factor = random.nextDouble();
    IntegerGenerator generator = new IntegerGenerator(initialValue, factor);
    IntegerGenerator copy = generator.getCopy();
    assertEquals(initialValue, copy.initialValue, 0.0);
    assertEquals(factor, copy.factor, 0.0);
  }
}
