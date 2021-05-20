package org.lefmaroli.interpolation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lefmaroli.interpolation.Interpolation.Dimension;

class InterpolationTest {

  @Test
  void testWrongDistanceArrayLength() {
    CornerMatrix matrix = CornerMatrix.getForDimension(4);
    Assertions.assertThrows(
        DistancesArrayLengthException.class, () -> Interpolation.linear(matrix, new double[3]));
  }

  @Test
  void testNotBoundedDistance() {
    CornerMatrix matrix = CornerMatrix.getForDimension(4);
    double[] distances = new double[4];
    distances[0] = 0.4;
    distances[1] = 4.4;
    distances[2] = 0.6;
    distances[3] = 0.75;
    Assertions.assertThrows(
        DistanceNotBoundedException.class, () -> Interpolation.linear(matrix, distances));
  }

  @Test
  void testLinearSmallToBigInterpolation() {
    // 1 and 2 as values, distance of 0.5
    Assertions.assertEquals(1.5, Interpolation.linear(1, 2, 0.5), 0.0);
    Assertions.assertEquals(1.25, Interpolation.linear(1, 2, 0.25), 0.0);
  }

  @Test
  void testLinearBigToSmallInterpolation() {
    // 1 and 2 as values, distance of 0.5
    Assertions.assertEquals(1.5, Interpolation.linear(2, 1, 0.5), 0.0);
    Assertions.assertEquals(1.75, Interpolation.linear(2, 1, 0.25), 0.0);
  }

  @Test
  void testLinearIllegalValue() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Interpolation.linear(1, 5, -1));
  }

  @Test
  void testLinearIllegalValue2() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Interpolation.linear(1, 5, 5));
  }

  @Test
  void testLinearWithFade() {
    // 1.5 should not change here
    Assertions.assertEquals(1.5, Interpolation.linearWithFade(1, 2, 0.5), 0);
    Assertions.assertEquals(1.103515625, Interpolation.linearWithFade(1, 2, 0.25), 0);
  }

  @Test
  void testLinearWithFadeIllegalValue() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> Interpolation.linearWithFade(1, 5, -1));
  }

  @Test
  void testLinearWithFadeIllegalValue2() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> Interpolation.linearWithFade(1, 5, 5));
  }

  @Test
  void testFade() {
    // 0.5 should return exactly 0.5
    Assertions.assertEquals(0.5, Interpolation.fade(0.5), 0);
    Assertions.assertEquals(0.103515625, Interpolation.fade(0.25), 0);
  }

  @Test
  void testFadeIllegalValue() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Interpolation.fade(-1));
  }

  @Test
  void testFadeIllegalValue2() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Interpolation.fade(5));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 4})
  void testDimensionGetFromIllegalIndex(int index){
    Assertions.assertThrows(IllegalArgumentException.class, ()-> Dimension.getFromIndex(index));
  }
}
