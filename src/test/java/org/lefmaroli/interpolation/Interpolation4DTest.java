package org.lefmaroli.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class Interpolation4DTest {

  private final double[][][][] corners4D = new double[2][2][2][2];
  private final double[] distances4D = new double[4];

  @Before
  public void init() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          for (int m = 0; m < 2; m++) {
            corners4D[i][j][k][m] = 0.0;
          }
        }
      }
    }
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength() {
    double[][][][] corners = new double[1][2][2][2];
    Interpolation.linear4D(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength2() {
    double[][][][] corners = new double[2][3][2][2];
    Interpolation.linear4D(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength3() {
    double[][][][] corners = new double[2][2][3][2];
    Interpolation.linear4D(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLength4() {
    double[][][][] corners = new double[2][2][2][3];
    Interpolation.linear4D(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade() {
    double[][][][] corners = new double[1][2][2][2];
    Interpolation.linear4DWithFade(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade2() {
    double[][][][] corners = new double[2][5][2][2];
    Interpolation.linear4DWithFade(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade3() {
    double[][][][] corners = new double[2][2][3][2];
    Interpolation.linear4DWithFade(corners, distances4D);
  }

  @Test(expected = CornersArrayLengthException.class)
  public void testWrongCornersArrayLengthWithFade4() {
    double[][][][] corners = new double[2][2][2][6];
    Interpolation.linear4DWithFade(corners, distances4D);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength() {
    Interpolation.linear4D(corners4D, new double[5]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLength2() {
    Interpolation.linear4D(corners4D, new double[1]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLengthWithFade() {
    Interpolation.linear4DWithFade(corners4D, new double[5]);
  }

  @Test(expected = DistancesArrayLengthException.class)
  public void testWrongDistanceArrayLengthWithFade2() {
    Interpolation.linear4DWithFade(corners4D, new double[1]);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuX() {
    distances4D[0] = -5;
    distances4D[1] = 0.1;
    distances4D[2] = 0.2;
    distances4D[3] = 0.7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuX2() {
    distances4D[0] = 4;
    distances4D[1] = 0.1;
    distances4D[2] = 0.2;
    distances4D[3] = 0.7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuY() {
    distances4D[0] = 0.3;
    distances4D[1] = -8;
    distances4D[2] = 0.2;
    distances4D[3] = 0.7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuY2() {
    distances4D[0] = 0.3;
    distances4D[1] = 2;
    distances4D[2] = 0.2;
    distances4D[3] = 0.7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuZ() {
    distances4D[0] = 0.3;
    distances4D[1] = 0.2;
    distances4D[2] = -2;
    distances4D[3] = 0.7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuZ2() {
    distances4D[0] = 0.3;
    distances4D[1] = 0.2;
    distances4D[2] = 21;
    distances4D[3] = 0.7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuT() {
    distances4D[0] = 0.3;
    distances4D[1] = 0.2;
    distances4D[2] = 0.2;
    distances4D[3] = -7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test(expected = DistanceNotBoundedException.class)
  public void test4DIllegalMuT2() {
    distances4D[0] = 0.3;
    distances4D[1] = 0.2;
    distances4D[2] = 0.5;
    distances4D[3] = 7;
    Interpolation.linear4D(corners4D, distances4D);
  }

  @Test
  public void test4DTrivialInT() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[i][j][k][1] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4D(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInX() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[1][i][j][k] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4D(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInY() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[i][1][k][j] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4D(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInZ() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[i][k][1][j] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4D(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DNonTrivial() {
    double value = 0.0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          for (int m = 0; m < 2; m++) {
            corners4D[i][k][1][j] = value;
            value += 1.0;
          }
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = i * 0.1;
    }
    double actual = Interpolation.linear4D(corners4D, distances4D);
    assertEquals(0.48, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInTWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[i][j][k][1] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4DWithFade(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInXWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[1][i][j][k] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4DWithFade(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInYWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[i][1][k][j] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4DWithFade(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DTrivialInZWithFade() {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          corners4D[i][k][1][j] = 1.0;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = 0.5;
    }
    double actual = Interpolation.linear4DWithFade(corners4D, distances4D);
    assertEquals(0.5, actual, 1E-9);
  }

  @Test
  public void test4DNonTrivialWithFade() {
    double value = 0.0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          for (int m = 0; m < 2; m++) {
            corners4D[i][k][1][j] = value;
            value += 1.0;
          }
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      distances4D[i] = i * 0.1;
    }
    double actual = Interpolation.linear4DWithFade(corners4D, distances4D);
    assertEquals(0.0966939648, actual, 1E-9);
  }
}
