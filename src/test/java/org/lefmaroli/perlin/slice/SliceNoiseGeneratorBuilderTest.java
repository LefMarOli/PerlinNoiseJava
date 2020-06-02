package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.line.LineNoiseGeneratorBuilderTest;

public class SliceNoiseGeneratorBuilderTest {

  Logger logger = LogManager.getLogger(LineNoiseGeneratorBuilderTest.class);

  private int sliceWidth = 400;
  private int sliceHeight = 400;

  @Test
  public void testBuildNoiseSliceNotNull() throws NoiseBuilderException {
    SliceNoiseGenerator noiseLineGenerator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight).build();
    assertNotNull(noiseLineGenerator);
  }

  @Test
  public void testBuildNoiseLineCreateSameFromSameBuilder() throws NoiseBuilderException {
    SliceNoiseGeneratorBuilder sliceNoiseGeneratorBuilder =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight);
    SliceNoiseGenerator noisePointGenerator = sliceNoiseGeneratorBuilder.build();
    SliceNoiseGenerator noisePointGenerator2 = sliceNoiseGeneratorBuilder.build();
    assertNotNull(noisePointGenerator2);
    assertEquals(noisePointGenerator, noisePointGenerator2);
  }

  @Test
  public void testBuilderPatternForSubclass() {
    new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight)
        .withWidthInterpolationPointGenerator(new IntegerGenerator(5, 0.5))
        .withHeightInterpolationPointGenerator(new IntegerGenerator(4, 0.5));
  }

  @Test
  public void testBuilderPatternForCircularity() {
    new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight).withCircularBounds();
  }

  // Fake test to visualize data, doesn't assert anything
  @Ignore
  @Test
  public void getNextSlices() throws NoiseBuilderException, InterruptedException {
    IntegerGenerator widthInterpolationPointGenerator = new IntegerGenerator(128, 0.9);
    IntegerGenerator heightInterpolationPointGenerator = new IntegerGenerator(128, 0.7);
    IntegerGenerator noiseInterpolationPointGenerator = new IntegerGenerator(128, 0.5);
    SliceNoiseGenerator generator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight)
            .withWidthInterpolationPointGenerator(widthInterpolationPointGenerator)
            .withHeightInterpolationPointGenerator(heightInterpolationPointGenerator)
            .withNoiseInterpolationPointGenerator(noiseInterpolationPointGenerator)
            .withNumberOfLayers(4)
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.95))
            .build();
    double[][] slice = generator.getNext(1).getAsRawData()[0];
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slice, 5);
    image.setVisible();
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 5) {
        previousTime = System.currentTimeMillis();
        slice = generator.getNext(1).getAsRawData()[0];
        image.updateImage(slice);
      } else {
        Thread.sleep(2);
      }
    }
  }

  @Ignore
  @Test
  public void benchmarkPerformance() throws NoiseBuilderException {
    SliceNoiseGenerator noiseGenerator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight)
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseInterpolationPointGenerator(new IntegerGenerator(1000, 0.5))
            .withWidthInterpolationPointGenerator(new IntegerGenerator(50, 2.0))
            .withHeightInterpolationPointGenerator(new IntegerGenerator(50, 2.0))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();

    double duration = 0.0;
    int numberOfIterations = 5;
    int count = 10;
    for (int i = 0; i < numberOfIterations; i++) {
      long start = System.currentTimeMillis();
      noiseGenerator.getNext(count);
      long end = System.currentTimeMillis();
      duration += end - start;
      logger.info("Finished iteration " + i);
    }
    duration /= numberOfIterations;

    logger.info("Mean duration: " + duration);
    // Current performance is 482.6
  }
}