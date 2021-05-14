package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.utils.AssertUtils;
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
  public void testSmoothVisuals() throws NoiseBuilderException { // NOSONAR
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

    AtomicReference<SimpleGrayScaleImage> im = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    if (isDisplaySupported) {
      im.set(new SimpleGrayScaleImage(slice, 5));
      im.get().setVisible();
    }

    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              double[][] next = generator.getNext();
              if (Thread.interrupted()) {
                return;
              }
              if (isDisplaySupported) {
                im.get().updateImage(next);
              }
              double[] column = new double[next[0].length];
              for (double[] row : next) {
                AssertUtils.valuesContinuousInArray(row);
                System.arraycopy(row, 0, column, 0, row.length);
                AssertUtils.valuesContinuousInArray(column);
              }
            },
            60,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) {
            im.get().dispose();
          }
        });
  }
}
