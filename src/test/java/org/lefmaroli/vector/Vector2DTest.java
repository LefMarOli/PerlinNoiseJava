package org.lefmaroli.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Vector2DTest {

  @Test
  void testLength() {
    double x = 0.5;
    double y = 0.6;
    Vector2D vector = new Vector2D(x, y);
    assertEquals(Math.sqrt((x * x + y * y)), vector.getLength());
  }

  @Test
  void testNormalize() {
    Vector2D vector = new Vector2D(0.5, 0.6);
    Vector2D normalized = vector.normalize();
    assertEquals(1.0, normalized.getLength());
    assertEquals(normalized, normalized.normalize());
  }

  @Test
  void testGetDimension() {
    Vector2D vector = new Vector2D(0.5, 0.6);
    assertEquals(2, vector.getDimension());
  }

  @Test
  void testVectorProduct() {
    double x = 0.5;
    double y = 0.6;
    Vector2D vector = new Vector2D(x, y);
    double u = 0.1;
    double v = 0.2;
    double vectorProduct = vector.getVectorProduct(u, v);
    assertEquals(x * u + y * v, vectorProduct);
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("wrongNumberOfArguments")
  @SuppressWarnings("unused")
  void testVectorProductWrongNumberOfArguments(double[] args, String title) {
    Vector2D vector = new Vector2D(0.5, 0.6);
    assertThrows(IllegalArgumentException.class, () -> vector.getVectorProduct(args));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> wrongNumberOfArguments(){
    return Stream.of(
        Arguments.of(new double[]{0.2}, "too few"),
        Arguments.of(new double[]{0.2, 0.1, 0.3}, "too many")
    );
  }

}