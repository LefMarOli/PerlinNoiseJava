package org.lefmaroli.interpolation;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lefmaroli.interpolation.Interpolation.Dimension;

class InterpolationTest {

  @Test
  void testWrongDistanceArrayLength() {
    CornerMatrix matrix = CornerMatrixFactory.getForDimension(4);
    Assertions.assertThrows(
        DistancesArrayLengthException.class, () -> Interpolation.linear(matrix, new double[3]));
  }

  @Test
  void testNotBoundedDistance() {
    CornerMatrix matrix = CornerMatrixFactory.getForDimension(4);
    double[] distances = new double[4];
    distances[0] = 0.4;
    distances[1] = 4.4;
    distances[2] = 0.6;
    distances[3] = 0.75;
    Assertions.assertThrows(
        DistanceNotBoundedException.class, () -> Interpolation.linear(matrix, distances));
  }

  @ParameterizedTest
  @MethodSource("testLinearArgs")
  void testLinear(double expected, double lowerBound, double higherBound, double mu) {
    Assertions.assertEquals(expected, Interpolation.linear(lowerBound, higherBound, mu), 0.0);
  }

  private static Stream<Arguments> testLinearArgs() {
    return Stream.of(
        Arguments.of(1.5, 1, 2, 0.5),
        Arguments.of(1.25, 1, 2, 0.25),
        Arguments.of(1.5, 2, 1, 0.5),
        Arguments.of(1.75, 2, 1, 0.25));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1, 5})
  void testLinearIllegalValue(double mu) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Interpolation.linear(1, 5, mu));
  }

  @ParameterizedTest
  @MethodSource("linearWithFadeArgs")
  void testLinearWithFade(double expectedValue, double lowerBound, double higherBound, double mu) {
    Assertions.assertEquals(
        expectedValue, Interpolation.linearWithFade(lowerBound, higherBound, mu), 0);
  }

  private static Stream<Arguments> linearWithFadeArgs() {
    return Stream.of(Arguments.of(1.5, 1, 2, 0.5), Arguments.of(1.103515625, 1, 2, 0.25));
  }

  @Test
  void testLinearWithFade() {
    // 1.5 should not change here
    Assertions.assertEquals(1.5, Interpolation.linearWithFade(1, 2, 0.5), 0);
    Assertions.assertEquals(1.103515625, Interpolation.linearWithFade(1, 2, 0.25), 0);
  }

  @ParameterizedTest(name = "{index} {0} - {1}")
  @MethodSource("linearWithFadeIllegalValues")
  void testLinearWithFadeIllegalValue(int[] values, String title) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> Interpolation.linearWithFade(1, 5, -1));
  }

  private static Stream<Arguments> linearWithFadeIllegalValues() {
    return Stream.of(
        Arguments.of(new int[] {1, 5, -1}, "Negative value for mu"),
        Arguments.of(new int[] {1, 5, 5}, "Value greater than 1 for mu"));
  }

  @ParameterizedTest
  @MethodSource("testFadeArgs")
  void testFade(double expectedValue, double testValue) {
    Assertions.assertEquals(expectedValue, Interpolation.fade(testValue), 0);
  }

  private static Stream<Arguments> testFadeArgs() {
    return Stream.of(Arguments.of(0.5, 0.5), Arguments.of(0.103515625, 0.25));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 5})
  void testFadeIllegalValue(int value) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Interpolation.fade(value));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 4})
  void testDimensionGetFromIllegalIndex(int index) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Dimension.getFromIndex(index));
  }
}
