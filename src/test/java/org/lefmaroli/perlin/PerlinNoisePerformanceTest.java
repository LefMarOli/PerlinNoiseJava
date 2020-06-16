package org.lefmaroli.perlin;

import org.junit.Before;
import org.junit.Test;

public class PerlinNoisePerformanceTest {

  private PerlinNoise perlinNoise;

  @Before
  public void setup() {
    perlinNoise = new PerlinNoise(1);
  }

  @Test(timeout = 20)
  public void testPerformance() {
    int numIterations = 10000;
    for (int i = 0; i < numIterations; i++) {
      perlinNoise.getFor(i * 0.5);
    }
  }
}
