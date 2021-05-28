package org.lefmaroli.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class VectorFactoryTest {

  @ParameterizedTest
  @ValueSource(ints = {0, 6})
  void testIllegalDimension(int dimension) {
    assertThrows(
        IllegalArgumentException.class,
        () -> VectorFactory.getVectorForCoordinates(new double[dimension]));
  }

  @ParameterizedTest
  @MethodSource("classArgs")
  void testRightReturnedClass(Class<? extends DimensionalVector> clazz, double[] args) {
    assertEquals(clazz, VectorFactory.getVectorForCoordinates(args).getClass());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> classArgs() {
    return Stream.of(
        Arguments.of(Vector1D.class, new double[] {0.2}),
        Arguments.of(Vector2D.class, new double[] {0.2, 0.3}),
        Arguments.of(Vector3D.class, new double[] {0.2, 0.3, 0.1}),
        Arguments.of(Vector4D.class, new double[] {0.2, 0.3, 0.1, 0.5}),
        Arguments.of(Vector5D.class, new double[] {0.2, 0.3, 0.1, 0.5, 0.9}));
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource("notNulls")
  @SuppressWarnings("unused")
  void testNotNull(double[] args, String title) {
    assertNotNull(VectorFactory.getVectorForCoordinates(args));
  }

  @SuppressWarnings({"unused"})
  private static Stream<Arguments> notNulls() {
    return Stream.of(
        Arguments.of(new double[] {0.2}, "1 dimension"),
        Arguments.of(new double[] {0.2, 0.3}, "2 dimension"),
        Arguments.of(new double[] {0.2, 0.3, 0.1}, "3 dimension"),
        Arguments.of(new double[] {0.2, 0.3, 0.1, 0.5}, "4 dimension"),
        Arguments.of(new double[] {0.2, 0.3, 0.1, 0.5, 0.9}, "5 dimension"));
  }
}
