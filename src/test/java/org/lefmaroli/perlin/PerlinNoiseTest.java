package org.lefmaroli.perlin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PerlinNoiseTest {

  private final long randomSeed = System.currentTimeMillis();

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNumberOfIndices() {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    perlinNoise.getFor(0.5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNumberOfIndices2() {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    perlinNoise.getFor(0.5, 0.3, 15.4);
  }

  @Test
  public void testValueBounded1D() {
    PerlinNoise perlinNoise = new PerlinNoise(1, randomSeed);
    int numIterations = 10000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      value = perlinNoise.getFor(i * 0.5);
      assertTrue(value >= 0.0);
      assertTrue(value <= 1.0);
    }
  }

  @Test
  public void testValueBounded2D() {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    int numIterations = 1000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        value = perlinNoise.getFor(i * 0.5, j * 0.02);
        assertTrue(value >= 0.0);
        assertTrue(value <= 1.0);
      }
    }
  }

  @Test
  public void testValueBounded3D() {
    PerlinNoise perlinNoise = new PerlinNoise(3, randomSeed);
    int numIterations = 100;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        for (int k = 0; k < numIterations; k++) {
          value = perlinNoise.getFor(i * 0.5, j * 0.02, k * 0.04);
          assertTrue(value >= 0.0);
          assertTrue(value <= 1.0);
        }
      }
    }
  }

  @Test
  public void testValueBounded4D() {
    PerlinNoise perlinNoise = new PerlinNoise(4, randomSeed);
    int numIterations = 10;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        for (int k = 0; k < numIterations; k++) {
          for (int m = 0; m < numIterations; m++) {
            value = perlinNoise.getFor(i * 0.5, j * 0.02, k * 0.04, m * 0.8);
            assertTrue(value >= 0.0);
            assertTrue(value <= 1.0);
          }
        }
      }
    }
  }

  @Test
  public void testValuesContinuity() {
    PerlinNoise perlin = new PerlinNoise(1, randomSeed);
    int size = 1024;
    double[] values = new double[size];
    double stepSize = 0.00001;
    for (int i = 0; i < values.length; i++) {
      values[i] = perlin.getFor(i * stepSize);
    }

    for (int i = 0; i < size - 2; i++) {
      double mu = values[i] - values[i+1];
      double nextMu = values[i+1] - values[i+2];
      assertEquals(mu, nextMu, stepSize);
    }
  }
}
