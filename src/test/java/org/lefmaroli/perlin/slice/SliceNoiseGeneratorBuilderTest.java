package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.line.LineNoiseGeneratorBuilderTest;

public class SliceNoiseGeneratorBuilderTest {

  private static final int sliceWidth = 400;
  private static final int sliceHeight = 400;
  Logger logger = LogManager.getLogger(LineNoiseGeneratorBuilderTest.class);

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

  @Ignore("Skipped, only used to visualize results")
  @Test
  public void getNextSlices() throws NoiseBuilderException, InterruptedException {
    DoubleGenerator widthInterpolationPointGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.9);
    DoubleGenerator heightInterpolationPointGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.7);
    DoubleGenerator noiseInterpolationPointGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.5);
    SliceNoiseGenerator generator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight)
            .withWidthInterpolationPointGenerator(widthInterpolationPointGenerator)
            .withHeightInterpolationPointGenerator(heightInterpolationPointGenerator)
            .withNoiseStepSizeGenerator(noiseInterpolationPointGenerator)
            .withNumberOfLayers(4)
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.95))
            .build();
    double[][] slice = generator.getNext();
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slice, 5);
    image.setVisible();
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 5) {
        previousTime = System.currentTimeMillis();
        slice = generator.getNext();
        image.updateImage(slice);
      } else {
        Thread.sleep(2);
      }
    }
  }
}
