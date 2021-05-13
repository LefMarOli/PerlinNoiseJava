package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.utils.ScheduledUpdater;

public class SliceNoiseGeneratorBuilderTest {

  private static final int sliceWidth = 400;
  private static final int sliceHeight = 400;

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
  public void getNextSlices() throws NoiseBuilderException {
    DoubleGenerator widthStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.9);
    DoubleGenerator heightStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.7);
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.5);
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    int sliceWidth = 200;
    int sliceHeight = 200;
    SliceNoiseGenerator generator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight)
            .withWidthInterpolationPointGenerator(widthStepSizeGenerator)
            .withHeightInterpolationPointGenerator(heightStepSizeGenerator)
            .withNoiseStepSizeGenerator(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudeGenerator(amplitudeGenerator)
            .build();
    double[][] slice = generator.getNext();
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slice, 5);
    image.setVisible();

    amplitudeGenerator.reset();
    noiseStepSizeGenerator.reset();
    widthStepSizeGenerator.reset();
    heightStepSizeGenerator.reset();
    double maxWidthDiff = 0;
    double maxHeightDiff = 0;
    double maxNoiseDiff = 0;
    double maxValue = 0;

    for (int i = 0; i < numLayers; i++) {
      double layerMaxVal = amplitudeGenerator.getNext();
      maxValue += layerMaxVal;
      maxWidthDiff += widthStepSizeGenerator.getNext()/layerMaxVal;
      maxHeightDiff += heightStepSizeGenerator.getNext()/layerMaxVal;
      maxNoiseDiff += noiseStepSizeGenerator.getNext()/layerMaxVal;
    }
    maxWidthDiff/=maxValue;
    maxHeightDiff/=maxValue;
    maxNoiseDiff/=maxValue;
    LogManager.getLogger(this.getClass()).info("MaxWidthDiff:" + maxWidthDiff);
    LogManager.getLogger(this.getClass()).info("MaxHeightDiff:" + maxHeightDiff);
    LogManager.getLogger(this.getClass()).info("MaxNoiseDiff:" + maxNoiseDiff);
    final double widthDiff = maxWidthDiff;
    final double heightDiff = maxHeightDiff;
    final double noiseDiff = maxNoiseDiff;

    final double[][] previous = new double[sliceWidth][sliceHeight];
    for (int i = 0; i < sliceWidth; i++) {
      System.arraycopy(slice[i], 0, previous[i], 0, sliceHeight);
    }

    ScheduledUpdater.updateAtRateForDuration(
        () -> {
          double[][] next = generator.getNext();
          if (Thread.interrupted()) {
            return;
          }
          image.updateImage(next);
          for (int i = 0; i < sliceWidth - 1; i++) {
            for (int j = 0; j < sliceHeight; j++) {
              double first = next[i][j];
              double second = next[i+1][j];
              assertEquals(first, second, widthDiff);
            }
          }
          for (int i = 0; i < sliceWidth; i++) {
            for (int j = 0; j < sliceHeight - 1; j++) {
              double first = next[i][j];
              double second = next[i][j+1];
              assertEquals(first, second, heightDiff);
            }
          }
          for (int i = 0; i < sliceWidth; i++) {
            for (int j = 0; j < sliceHeight; j++) {
              double first = previous[i][j];
              double second = next[i][j];
              assertEquals(first, second, noiseDiff);
            }
          }
          for (int i = 0; i < sliceWidth; i++) {
            System.arraycopy(next[i], 0, previous[i], 0, sliceHeight);
          }
        },
        60,
        TimeUnit.MILLISECONDS,
        15,
        TimeUnit.SECONDS);
  }
}
