package org.lefmaroli.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class Interpolation3DTest {

  private final double[][][] corners3D = new double[2][2][2];
  private final double[] distances3D = new double[3];

  @Before
  public void init() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners3D[i][j][k] = 0.0;
        }
      }
    }
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength() {
    double[][][] corners = new double[1][2][2];
    Interpolation.linear3D(corners, distances3D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength2() {
    double[][][] corners = new double[2][3][2];
    Interpolation.linear3D(corners, distances3D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength3() {
    double[][][] corners = new double[2][2][5];
    Interpolation.linear3D(corners, distances3D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade() {
    double[][][] corners = new double[1][2][2];
    Interpolation.linear3DWithFade(corners, distances3D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade2() {
    double[][][] corners = new double[2][3][2];
    Interpolation.linear3DWithFade(corners, distances3D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade3() {
    double[][][] corners = new double[2][2][5];
    Interpolation.linear3DWithFade(corners, distances3D);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength() {
    Interpolation.linear3D(corners3D, new double[5]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength2() {
    Interpolation.linear3D(corners3D, new double[1]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLengthWithFade() {
    Interpolation.linear3DWithFade(corners3D, new double[5]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLengthWithFade2() {
    Interpolation.linear3DWithFade(corners3D, new double[1]);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DIllegalMuX() {
    distances3D[0] = -5;
    distances3D[1] = 0.1;
    distances3D[2] = 0.2;
    Interpolation.linear3D(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DIllegalMuX2() {
    distances3D[0] = 4;
    distances3D[1] = 0.1;
    distances3D[2] = 0.2;
    Interpolation.linear3D(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DIllegalMuY() {
    distances3D[0] = 0.3;
    distances3D[1] = -8;
    distances3D[2] = 0.2;
    Interpolation.linear3D(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DIllegalMuY2() {
    distances3D[0] = 0.3;
    distances3D[1] = 2;
    distances3D[2] = 0.2;
    Interpolation.linear3D(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DIllegalMuZ() {
    distances3D[0] = 0.3;
    distances3D[1] = 0.2;
    distances3D[2] = -2;
    Interpolation.linear3D(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DIllegalMuZ2() {
    distances3D[0] = 0.3;
    distances3D[1] = 0.2;
    distances3D[2] = 21;
    Interpolation.linear3D(corners3D, distances3D);
  }

  @Test
  public void test3DTrivialInZ() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners3D[i][j][1] = 1.0;
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = 0.5;
    }
    double actual = Interpolation.linear3D(corners3D, distances3D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test3DTrivialInX() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners3D[1][j][i] = 1.0;
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = 0.5;
    }
    double actual = Interpolation.linear3D(corners3D, distances3D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test3DTrivialInY() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners3D[i][1][j] = 1.0;
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = 0.5;
    }
    double actual = Interpolation.linear3D(corners3D, distances3D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test3DNonTrivial() {
    double value = 0.0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners3D[i][j][k] = value;
          value += 1.0;
        }
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = i * 0.1;
    }
    double actual = Interpolation.linear3D(corners3D, distances3D);
    assertEquals(0.4, actual, 1E-9);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DWithFadeIllegalMuX() {
    distances3D[0] = -1;
    distances3D[1] = 0.2;
    distances3D[2] = 0.5;
    Interpolation.linear3DWithFade(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DWithFadeIllegalMuX2() {
    distances3D[0] = 5;
    distances3D[1] = 0.2;
    distances3D[2] = 0.5;
    Interpolation.linear3DWithFade(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DWithFadeIllegalMuY() {
    distances3D[0] = 0.3;
    distances3D[1] = -2;
    distances3D[2] = 0.2;
    Interpolation.linear3DWithFade(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DWithFadeIllegalMuY2() {
    distances3D[0] = 0.3;
    distances3D[1] = 4;
    distances3D[2] = 0.2;
    Interpolation.linear3DWithFade(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DWithFadeIllegalMuZ() {
    distances3D[0] = 0.3;
    distances3D[1] = 0.2;
    distances3D[2] = -21;
    Interpolation.linear3DWithFade(corners3D, distances3D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test3DWithFadeIllegalMuZ2() {
    distances3D[0] = 0.3;
    distances3D[1] = 0.2;
    distances3D[2] = 21;
    Interpolation.linear3DWithFade(corners3D, distances3D);
  }

  @Test
  public void test3DTrivialInZWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners3D[i][j][1] = 1.0;
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = 0.5;
    }
    double actual = Interpolation.linear3DWithFade(corners3D, distances3D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test3DTrivialInXWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners3D[1][j][i] = 1.0;
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = 0.5;
    }
    double actual = Interpolation.linear3DWithFade(corners3D, distances3D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test3DTrivialInYWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        corners3D[i][1][j] = 1.0;
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = 0.5;
    }
    double actual = Interpolation.linear3DWithFade(corners3D, distances3D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test3DWithFadeNonTrivial() {
    double value = 0.0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners3D[i][j][k] = value;
          value += 1.0;
        }
      }
    }
    for (int i = 0; i < 3; i++) {
      distances3D[i] = i * 0.1;
    }
    double expected = 0.07504;
    double actual = Interpolation.linear3DWithFade(corners3D, distances3D);
    assertEquals(expected, actual, 1E-9);
  }
}
