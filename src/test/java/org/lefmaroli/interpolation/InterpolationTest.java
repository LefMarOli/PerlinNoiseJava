package org.lefmaroli.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InterpolationTest {

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength() {
    CornerMatrix matrix = CornerMatrix.getForDimension(4);
    Interpolation.linear(matrix, new double[3]);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void testNotBoundedDistance() {
    CornerMatrix matrix = CornerMatrix.getForDimension(4);
    double[] distances = new double[4];
    distances[0] = 0.4;
    distances[1] = 4.4;
    distances[2] = 0.6;
    distances[3] = 0.75;
    Interpolation.linear(matrix, distances);
  }

  @Test
  public void testLinearSmallToBigInterpolation() {
    // 1 and 2 as values, distance of 0.5
    assertEquals(1.5, Interpolation.linear(1, 2, 0.5), 0.0);
    assertEquals(1.25, Interpolation.linear(1, 2, 0.25), 0.0);
  }

  @Test
  public void testLinearBigToSmallInterpolation() {
    // 1 and 2 as values, distance of 0.5
    assertEquals(1.5, Interpolation.linear(2, 1, 0.5), 0.0);
    assertEquals(1.75, Interpolation.linear(2, 1, 0.25), 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLinearIllegalValue() {
    Interpolation.linear(1, 5, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLinearIllegalValue2() {
    Interpolation.linear(1, 5, 5);
  }

  @Test
  public void testLinearWithFade() {
    // 1.5 should not change here
    assertEquals(1.5, Interpolation.linearWithFade(1, 2, 0.5), 0);
    assertEquals(1.103515625, Interpolation.linearWithFade(1, 2, 0.25), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLinearWithFadeIllegalValue() {
    Interpolation.linearWithFade(1, 5, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLinearWithFadeIllegalValue2() {
    Interpolation.linearWithFade(1, 5, 5);
  }

  @Test
  public void testFade() {
    // 0.5 should return exactly 0.5
    assertEquals(0.5, Interpolation.fade(0.5), 0);
    assertEquals(0.103515625, Interpolation.fade(0.25), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFadeIllegalValue() {
    Interpolation.fade(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFadeIllegalValue2() {
    Interpolation.fade(5);
  }
}
