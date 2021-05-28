package org.lefmaroli.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Vector1DTest {

  @Test
  void testLength() {
    double length = 0.5;
    Vector1D vector1D = new Vector1D(length);
    assertEquals(length, vector1D.getLength());
  }

  @Test
  void testNormalize() {
    Vector1D vector = new Vector1D(0.5);
    Vector1D normalized = vector.normalize();
    assertEquals(1.0, normalized.getLength());
    assertEquals(normalized, normalized.normalize());
  }

  @Test
  void testGetDimension() {
    Vector1D vector = new Vector1D(0.5);
    assertEquals(1, vector.getDimension());
  }

  @Test
  void testVectorProduct() {
    Vector1D vector = new Vector1D(0.5);
    double vectorProduct = vector.getVectorProduct(0.6);
    assertEquals(0.5 * 0.6, vectorProduct);
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("wrongNumberOfArguments")
  @SuppressWarnings("unused")
  void testVectorProductWrongNumberOfArguments(double[] args, String title) {
    Vector1D vector = new Vector1D(0.5);
    assertThrows(IllegalArgumentException.class, () -> vector.getVectorProduct(args));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> wrongNumberOfArguments() {
    return Stream.of(
        Arguments.of(new double[] {}, "too few"),
        Arguments.of(new double[] {0.2, 0.1}, "too many"));
  }
}
