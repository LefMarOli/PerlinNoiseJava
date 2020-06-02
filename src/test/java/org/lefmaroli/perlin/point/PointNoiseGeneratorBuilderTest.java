package org.lefmaroli.perlin.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class PointNoiseGeneratorBuilderTest {

  Logger logger = LogManager.getLogger(PointNoiseGeneratorBuilderTest.class);

  @Test
  public void testBuildNoisePointNotNull() throws NoiseBuilderException {
    PointNoiseGenerator noisePointGenerator = new PointNoiseGeneratorBuilder().build();
    assertNotNull(noisePointGenerator);
  }

  @Test
  public void testBuildNoisePointCreateSameFromSameBuilder() throws NoiseBuilderException {
    PointNoiseGeneratorBuilder pointNoiseGeneratorBuilder = new PointNoiseGeneratorBuilder();
    PointNoiseGenerator noisePointGenerator = pointNoiseGeneratorBuilder.build();
    PointNoiseGenerator noisePointGenerator2 = pointNoiseGeneratorBuilder.build();
    assertNotNull(noisePointGenerator2);
    assertEquals(noisePointGenerator, noisePointGenerator2);
  }

  @Ignore
  @Test
  public void benchmarkPerformance() throws NoiseBuilderException {
    PointNoiseGenerator noiseGenerator =
        new PointNoiseGeneratorBuilder()
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseInterpolationPointGenerator(new IntegerGenerator(50, 2.0))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();

    double duration = 0.0;
    int numberOfIterations = 1000;
    int count = 5000;
    for (int i = 0; i < numberOfIterations; i++) {
      long start = System.currentTimeMillis();
      noiseGenerator.getNext(count);
      long end = System.currentTimeMillis();
      duration += end - start;
    }
    duration /= numberOfIterations;

    logger.info("Mean duration: " + duration);
    // Current performance: 1.012
  }
}
