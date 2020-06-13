package org.lefmaroli.perlin;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class PerlinNoiseTest {

  @BeforeClass
  public static void setup() {
    PerlinNoise.initializeBounds();
  }

  @Test
  public void testValueBounded1D() {
    int numIterations = 10000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      value = PerlinNoise.perlin(i * 0.5);
      assertTrue(value >= 0.0);
      assertTrue(value <= 1.0);
    }
  }

  @Test
  public void testValueBounded2D() {
    int numIterations = 1000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        value = PerlinNoise.perlin(i * 0.5, j * 0.02);
        assertTrue(value >= 0.0);
        assertTrue(value <= 1.0);
      }
    }
  }

  @Test
  public void testValueBounded3D() {
    int numIterations = 100;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        for (int k = 0; k < numIterations; k++) {
          value = PerlinNoise.perlin(i * 0.5, j * 0.02, k * 0.04);
          assertTrue(value >= 0.0);
          assertTrue(value <= 1.0);
        }
      }
    }
  }

  @Test
  public void testValueBounded4D() {
    int numIterations = 10;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        for (int k = 0; k < numIterations; k++) {
          for (int m = 0; m < numIterations; m++) {
            value = PerlinNoise.perlin(i * 0.5, j * 0.02, k * 0.04, m * 0.8);
            assertTrue(value >= 0.0);
            assertTrue(value <= 1.0);
          }
        }
      }
    }
  }
}
