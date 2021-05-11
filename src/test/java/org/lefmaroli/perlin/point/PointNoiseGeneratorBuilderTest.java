package org.lefmaroli.perlin.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
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
}
