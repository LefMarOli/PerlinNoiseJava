package org.lefmaroli.perlin;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class PerlinNoisePerformanceTest {

  private PerlinNoise perlinNoise;

  @Before
  public void setup() {
    perlinNoise = new PerlinNoise(1, System.currentTimeMillis());
  }

  @Test
  public void testPerformance() {
    int numIterations = 10000;
    for (int i = 0; i < numIterations; i++) {
      perlinNoise.getFor(i * 0.5);
    }
    await().atMost(Duration.ofMillis(20));
  }
}
