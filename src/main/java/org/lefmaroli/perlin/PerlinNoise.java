package org.lefmaroli.perlin;

import java.util.concurrent.atomic.AtomicBoolean;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.Vector4D;

public class PerlinNoise {

  private static final int NUMBER_OF_BOUNDS_4D = 2 << 5;
  private static final double MAX_VALUE_VECTOR_PRODUCT = 0.5;
  private static final Vector4D[][][][] BOUNDS_4D =
      new Vector4D[NUMBER_OF_BOUNDS_4D][NUMBER_OF_BOUNDS_4D][NUMBER_OF_BOUNDS_4D]
          [NUMBER_OF_BOUNDS_4D];
  private static final double[][][][] CORNERS = new double[2][2][2][2];
  private static final double[] DISTANCES = new double[4];
  private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

  private PerlinNoise() {}

  public static void initializeBounds(){
    initializeBoundsWithSeed(System.currentTimeMillis());
  }

  public static void initializeBoundsWithSeed(long seed) {
    // Initialize bounds
    if (!INITIALIZED.compareAndExchange(false, true)) {
      RandomGenerator generator = new RandomGenerator(seed);
      for (int i = 0; i < NUMBER_OF_BOUNDS_4D; i++) {
        for (int j = 0; j < NUMBER_OF_BOUNDS_4D; j++) {
          for (int k = 0; k < NUMBER_OF_BOUNDS_4D; k++) {
            for (int m = 0; m < NUMBER_OF_BOUNDS_4D; m++) {
              BOUNDS_4D[i][j][k][m] = generator.getRandomUnitVector4D();
            }
          }
        }
      }
    }
  }

  public static double perlin(double x) {
    return perlin(x, 0.0);
  }

  public static double perlin(double x, double y) {
    return perlin(x, y, 0.0);
  }

  public static double perlin(double x, double y, double z) {
    return perlin(x, y, z, 0.0);
  }

  public static double perlin(double x, double y, double z, double t) {
    if(!INITIALIZED.get()){
      throw new IllegalStateException("Perlin noise needs to be initialized before first use.");
    }
    int intPartX = (int) x;
    int intPartY = (int) y;
    int intPartZ = (int) z;
    int intPartT = (int) t;
    double distX = x - intPartX;
    double distY = y - intPartY;
    double distZ = z - intPartZ;
    double distT = t - intPartT;
    DISTANCES[0] = distX;
    DISTANCES[1] = distY;
    DISTANCES[2] = distZ;
    DISTANCES[3] = distT;

    for (int xIndex = 0; xIndex < 2; xIndex++) {
      int xBoundIndex = getIndexBound(intPartX + xIndex);
      for (int yIndex = 0; yIndex < 2; yIndex++) {
        int yBoundIndex = getIndexBound(intPartY + yIndex);
        for (int zIndex = 0; zIndex < 2; zIndex++) {
          int zBoundIndex = getIndexBound(intPartZ + zIndex);
          for (int tIndex = 0; tIndex < 2; tIndex++) {
            int tBoundIndex = getIndexBound(intPartT + tIndex);
            CORNERS[xIndex][yIndex][zIndex][tIndex] =
                BOUNDS_4D[xBoundIndex][yBoundIndex][zBoundIndex][tBoundIndex].getVectorProduct(
                    distX - xIndex, distY - yIndex, distZ - zIndex, distT - tIndex);
          }
        }
      }
    }
    double interpolated = Interpolation.linear4DWithFade(CORNERS, DISTANCES);
    double adjusted = adjustInRange(interpolated);
    if (adjusted > 1.0) {
      adjusted = 1.0;
    } else if (adjusted < 0.0) {
      adjusted = 0.0;
    }
    return adjusted;
  }

  protected static double adjustInRange(double interpolated) {
    return ((interpolated / MAX_VALUE_VECTOR_PRODUCT) + 1.0) / 2.0;
  }

  protected static int getIndexBound(int intPart) {
    return intPart & (NUMBER_OF_BOUNDS_4D - 1);
  }
}
