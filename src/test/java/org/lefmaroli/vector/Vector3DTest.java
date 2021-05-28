package org.lefmaroli.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Vector3DTest {

  @Test
  void testLength() {
    double x = 0.5;
    double y = 0.6;
    double z = 0.1;
    Vector3D vector = new Vector3D(x, y, z);
    assertEquals(Math.sqrt((x * x + y * y + z * z)), vector.getLength());
  }

  @Test
  void testNormalize() {
    Vector3D vector = new Vector3D(0.5, 0.6, 0.2);
    Vector3D normalized = vector.normalize();
    assertEquals(1.0, normalized.getLength(), 1E-10);
    assertEquals(normalized, normalized.normalize());
  }

  @Test
  void testGetDimension() {
    Vector3D vector = new Vector3D(0.5, 0.6, 0.1);
    assertEquals(3, vector.getDimension());
  }

  @Test
  void testVectorProduct() {
    double x = 0.5;
    double y = 0.6;
    double z = 0.1;
    Vector3D vector = new Vector3D(x, y, z);
    double u = 0.1;
    double v = 0.2;
    double w = 0.8;
    double vectorProduct = vector.getVectorProduct(u, v, w);
    assertEquals(x * u + y * v + z * w, vectorProduct);
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("wrongNumberOfArguments")
  @SuppressWarnings("unused")
  void testVectorProductWrongNumberOfArguments(double[] args, String title) {
    Vector3D vector = new Vector3D(0.5, 0.6, 0.1);
    assertThrows(IllegalArgumentException.class, () -> vector.getVectorProduct(args));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> wrongNumberOfArguments(){
    return Stream.of(
        Arguments.of(new double[]{0.2, 0.1}, "too few"),
        Arguments.of(new double[]{0.2, 0.1, 0.3, 0.5}, "too many")
    );
  }

}