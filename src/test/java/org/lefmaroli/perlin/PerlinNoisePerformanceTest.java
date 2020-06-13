package org.lefmaroli.perlin;

import org.junit.Before;
import org.junit.Test;

public class PerlinNoisePerformanceTest {

  @Before
  public void setup() {
    PerlinNoise.initializeBounds();
  }

  @Test(timeout = 20)
  public void testPerformance() {
    int numIterations = 10000;
    for (int i = 0; i < numIterations; i++) {
      PerlinNoise.perlin(i * 0.5);
    }
  }
}
