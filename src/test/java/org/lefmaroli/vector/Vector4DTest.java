package org.lefmaroli.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Vector4DTest {

  @Test
  void testLength() {
    double x = 0.5;
    double y = 0.6;
    double z = 0.1;
    double t = 0.3;
    Vector4D vector = new Vector4D(x, y, z, t);
    assertEquals(Math.sqrt((x * x + y * y + z * z + t * t)), vector.getLength());
  }

  @Test
  void testNormalize() {
    Vector4D vector = new Vector4D(0.5, 0.6, 0.2, 0.3);
    Vector4D normalized = vector.normalize();
    assertEquals(1.0, normalized.getLength());
    assertEquals(normalized, normalized.normalize());
  }

  @Test
  void testGetDimension() {
    Vector4D vector = new Vector4D(0.5, 0.6, 0.1, 0.6);
    assertEquals(4, vector.getDimension());
  }

  @Test
  void testVectorProduct() {
    double x = 0.5;
    double y = 0.6;
    double z = 0.1;
    double t = 0.6;
    Vector4D vector = new Vector4D(x, y, z, t);
    double u = 0.1;
    double v = 0.2;
    double w = 0.8;
    double a = 0.9;
    double vectorProduct = vector.getVectorProduct(u, v, w, a);
    assertEquals(x * u + y * v + z * w + t * a, vectorProduct);
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("wrongNumberOfArguments")
  @SuppressWarnings("unused")
  void testVectorProductWrongNumberOfArguments(double[] args, String title) {
    Vector4D vector = new Vector4D(0.5, 0.6, 0.1, 0.9);
    assertThrows(IllegalArgumentException.class, () -> vector.getVectorProduct(args));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> wrongNumberOfArguments() {
    return Stream.of(
        Arguments.of(new double[] {0.2, 0.1, 0.3}, "too few"),
        Arguments.of(new double[] {0.2, 0.1, 0.3, 0.5, 0.8}, "too many"));
  }
}
