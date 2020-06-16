package org.lefmaroli.perlin;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PerlinNoiseTest {

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNumberOfIndices(){
    PerlinNoise perlinNoise = new PerlinNoise(2);
    perlinNoise.perlin(0.5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNumberOfIndices2(){
    PerlinNoise perlinNoise = new PerlinNoise(2);
    perlinNoise.perlin(0.5, 0.3, 15.4);
  }

  @Test
  public void testValueBounded1D() {
    PerlinNoise perlinNoise = new PerlinNoise(1);
    int numIterations = 10000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      value = perlinNoise.perlin(i * 0.5);
      assertTrue(value >= 0.0);
      assertTrue(value <= 1.0);
    }
  }

  @Test
  public void testValueBounded2D() {
    PerlinNoise perlinNoise = new PerlinNoise(2);
    int numIterations = 1000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        value = perlinNoise.perlin(i * 0.5, j * 0.02);
        assertTrue(value >= 0.0);
        assertTrue(value <= 1.0);
      }
    }
  }

  @Test
  public void testValueBounded3D() {
    PerlinNoise perlinNoise = new PerlinNoise(3);
    int numIterations = 100;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        for (int k = 0; k < numIterations; k++) {
          value = perlinNoise.perlin(i * 0.5, j * 0.02, k * 0.04);
          assertTrue(value >= 0.0);
          assertTrue(value <= 1.0);
        }
      }
    }
  }

  @Test
  public void testValueBounded4D() {
    PerlinNoise perlinNoise = new PerlinNoise(4);
    int numIterations = 10;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < numIterations; j++) {
        for (int k = 0; k < numIterations; k++) {
          for (int m = 0; m < numIterations; m++) {
            value = perlinNoise.perlin(i * 0.5, j * 0.02, k * 0.04, m * 0.8);
            assertTrue(value >= 0.0);
            assertTrue(value <= 1.0);
          }
        }
      }
    }
  }
}
