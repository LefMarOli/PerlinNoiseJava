package org.lefmaroli.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class Interpolation2DTest {

  private final double[][] corners2D = new double[2][2];
  private final double[] distances2D = new double[2];

  @Before
  public void init() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners2D[i][j] = 0.0;
      }
    }
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength(){
    double[][] corners = new double[1][2];
    Interpolation.linear2D(corners, distances2D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength2(){
    double[][] corners = new double[2][3];
    Interpolation.linear2D(corners, distances2D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade(){
    double[][] corners = new double[1][2];
    Interpolation.linear2DWithFade(corners, distances2D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade2(){
    double[][] corners = new double[2][3];
    Interpolation.linear2DWithFade(corners, distances2D);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength(){
    Interpolation.linear2D(corners2D, new double[5]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength2(){
    Interpolation.linear2D(corners2D, new double[1]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLengthWithFade(){
    Interpolation.linear2DWithFade(corners2D, new double[5]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLengthWithFade2(){
    Interpolation.linear2DWithFade(corners2D, new double[1]);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DIllegalMux() {
    distances2D[0] = -6;
    distances2D[1] = 0.5;
    Interpolation.linear2D(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DIllegalMux2() {
    distances2D[0] = 4;
    distances2D[1] = 0.5;
    Interpolation.linear2D(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DIllegalMuy() {
    distances2D[0] = 0.5;
    distances2D[1] = -8;
    Interpolation.linear2D(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DIllegalMuy2() {
    distances2D[0] = 0.5;
    distances2D[1] = 4;
    Interpolation.linear2D(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DWithFadeIllegalMux() {
    distances2D[0] = -6;
    distances2D[1] = 0.5;
    Interpolation.linear2DWithFade(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DWithFadeIllegalMux2() {
    distances2D[0] = 4;
    distances2D[1] = 0.5;
    Interpolation.linear2DWithFade(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DWithFadeIllegalMuy() {
    distances2D[0] = 0.5;
    distances2D[1] = 7;
    Interpolation.linear2DWithFade(corners2D, distances2D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test2DWithFadeIllegalMuy2() {
    distances2D[0] = 0.5;
    distances2D[1] = -8;
    Interpolation.linear2DWithFade(corners2D, distances2D);
  }

  @Test
  public void test2DWithFadeTrivial() {
    // 0----1
    // |    |
    // 1----0
    corners2D[0][1] = 1.0;
    corners2D[1][0] = 1.0;

    distances2D[0] = 0.5;
    distances2D[1] = 0.5;
    assertEquals(0.5, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.0;
    distances2D[1] = 0.5;
    assertEquals(0.5, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 1.0;
    distances2D[1] = 0.5;
    assertEquals(0.5, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 0.0;
    assertEquals(0.5, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 1.0;
    assertEquals(0.5, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.3;
    distances2D[1] = 0.3;
    assertEquals(0.2729698272, Interpolation.linear2DWithFade(corners2D, distances2D), 1E-9);
  }

  @Test
  public void test2DTrivial() {
    // 0----1
    // |    |
    // 1----0
    corners2D[0][1] = 1.0;
    corners2D[1][0] = 1.0;

    distances2D[0] = 0.5;
    distances2D[1] = 0.5;
    assertEquals(0.5, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.0;
    distances2D[1] = 0.5;
    assertEquals(0.5, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 1.0;
    distances2D[1] = 0.5;
    assertEquals(0.5, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 0.0;
    assertEquals(0.5, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 1.0;
    assertEquals(0.5, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.3;
    distances2D[1] = 0.3;
    assertEquals(0.42, Interpolation.linear2D(corners2D, distances2D), 1E-9);
  }

  @Test
  public void test2DNonTrivial() {
    // 0.3----10.9
    // |      |
    // -7------64.3
    corners2D[0][0] = 0.3;
    corners2D[0][1] = 10.9;
    corners2D[1][0] = -7;
    corners2D[1][1] = 64.3;

    distances2D[0] = 0.5;
    distances2D[1] = 0.5;
    assertEquals(17.125, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.0;
    distances2D[1] = 0.5;
    assertEquals(5.6, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 1.0;
    distances2D[1] = 0.5;
    assertEquals(28.65, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 0.0;
    assertEquals(-3.35, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 1.0;
    assertEquals(37.6, Interpolation.linear2D(corners2D, distances2D), 0.0);
    distances2D[0] = 0.3;
    distances2D[1] = 0.3;
    assertEquals(6.753, Interpolation.linear2D(corners2D, distances2D), 1E-9);
  }

  @Test
  public void test2DWithFadeNonTrivial() {
    // 0.3----10.9
    // |      |
    // -7------64.3
    corners2D[0][0] = 0.3;
    corners2D[0][1] = 10.9;
    corners2D[1][0] = -7;
    corners2D[1][1] = 64.3;

    distances2D[0] = 0.5;
    distances2D[1] = 0.5;
    assertEquals(17.125, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.0;
    distances2D[1] = 0.5;
    assertEquals(5.6, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 1.0;
    distances2D[1] = 0.5;
    assertEquals(28.65, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 0.0;
    assertEquals(-3.35, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.5;
    distances2D[1] = 1.0;
    assertEquals(37.6, Interpolation.linear2DWithFade(corners2D, distances2D), 0.0);
    distances2D[0] = 0.3;
    distances2D[1] = 0.3;
    assertEquals(2.45248574448, Interpolation.linear2DWithFade(corners2D, distances2D), 1E-9);
  }
}
