package org.lefmaroli.factorgenerator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MultipliedByNumberGeneratorTest {

  @Test
  void getNextFactorShouldAlwaysBeZero() {
    DoubleGenerator generator = new DoubleGenerator(0.0, 5489.5946);
    for (int i = 0; i < 50000; i++) {
      Assertions.assertEquals(0.0, generator.getNext(), 0.0);
    }
  }

  @Test
  void testReachLimit(){
    DoubleGenerator generator = new DoubleGenerator(0.0, 1.0);
    Iterator<Double> iterator = generator.iterator();
    for (int i = 0; i < MultipliedByNumberGenerator.DEFAULT_LIMIT; i++) {
      iterator.next();
    }
    Assertions.assertFalse(iterator.hasNext());
    Assertions.assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  void getNextFactorTestMultipliedByFactor() {
    Random rand = new Random(System.currentTimeMillis());
    double initialValue = rand.nextDouble();
    double factor = rand.nextDouble() * 500;
    DoubleGenerator generator = new DoubleGenerator(initialValue, factor);
    Assertions.assertEquals(initialValue, generator.getNext(), 0.0);
    double expectedValue = initialValue * factor;
    for (int i = 0; i < 50000; i++) {
      Assertions.assertEquals(expectedValue, generator.getNext(), 0.0);
      expectedValue *= factor;
    }
  }

  @Test
  void getNextFactorTestAlternatePositiveNegative() {
    Random rand = new Random(System.currentTimeMillis());
    double initialValue = rand.nextDouble();
    double factor = rand.nextDouble() * 500 * -1;
    DoubleGenerator generator = new DoubleGenerator(initialValue, factor);
    Assertions.assertEquals(initialValue, generator.getNext(), 0.0);
    double expectedValue = initialValue * factor;
    for (int i = 0; i < 50000; i++) {
      Assertions.assertEquals(expectedValue, generator.getNext(), 0.0);
      expectedValue *= factor;
    }
  }

  @Test
  void testReset() {
    IntegerGenerator generator = new IntegerGenerator(new Random().nextInt(), 5489.5946);
    double firstValue = generator.getNext();
    for (int i = 0; i < 50000; i++) {
      generator.reset(); // Always try to get the first value
      Assertions.assertEquals(firstValue, generator.getNext(), 0.0);
    }
    for (int i = 0; i < 50000; i++) {
      Assertions.assertNotEquals(firstValue, generator.getNext(), 0.0);
    }
  }

  @Test
  void testDoubleCopy() {
    Random random = new Random();
    double initialValue = random.nextDouble();
    double factor = random.nextDouble();
    DoubleGenerator generator = new DoubleGenerator(initialValue, factor);
    DoubleGenerator copy = generator.getCopy();
    Assertions.assertEquals(initialValue, copy.initialValue, 0.0);
    Assertions.assertEquals(factor, copy.factor, 0.0);
  }

  @Test
  void testIntegerCopy() {
    Random random = new Random();
    int initialValue = random.nextInt();
    double factor = random.nextDouble();
    IntegerGenerator generator = new IntegerGenerator(initialValue, factor);
    IntegerGenerator copy = generator.getCopy();
    Assertions.assertEquals(initialValue, copy.initialValue, 0.0);
    Assertions.assertEquals(factor, copy.factor, 0.0);
  }
}
