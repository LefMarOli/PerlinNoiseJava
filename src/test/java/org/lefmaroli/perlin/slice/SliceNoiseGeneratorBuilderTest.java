package org.lefmaroli.perlin.slice;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

class SliceNoiseGeneratorBuilderTest {

  private static final int sliceWidth = 400;
  private static final int sliceHeight = 400;

  @Test
  void testBuildNoiseSliceNotNull() throws NoiseBuilderException {
    SliceNoiseGenerator noiseLineGenerator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight).build();
    Assertions.assertNotNull(noiseLineGenerator);
  }

  @Test
  void testBuildNoiseLineCreateSameFromSameBuilder() throws NoiseBuilderException {
    SliceNoiseGeneratorBuilder sliceNoiseGeneratorBuilder =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight);
    SliceNoiseGenerator noisePointGenerator = sliceNoiseGeneratorBuilder.build();
    SliceNoiseGenerator noisePointGenerator2 = sliceNoiseGeneratorBuilder.build();
    Assertions.assertNotNull(noisePointGenerator2);
    Assertions.assertEquals(noisePointGenerator, noisePointGenerator2);
  }

  @Test
  void testSmoothVisuals() throws NoiseBuilderException { // NOSONAR
    DoubleGenerator widthStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.9);
    DoubleGenerator heightStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.7);
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.5);
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    int sliceWidth = 200;
    int sliceHeight = 200;
    ExecutorService executorService = Executors.newFixedThreadPool(numLayers,
        new ThreadFactoryBuilder().setNameFormat("layer-thread-%d").build());
    SliceNoiseGenerator generator =
        new SliceNoiseGeneratorBuilder(sliceWidth, sliceHeight)
            .withWidthInterpolationPointGenerator(widthStepSizeGenerator)
            .withHeightInterpolationPointGenerator(heightStepSizeGenerator)
            .withNoiseStepSizeGenerator(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudeGenerator(amplitudeGenerator)
            .withLayerExecutorService(executorService)
            .build();
    try{
    double[][] slice = generator.getNext();

    AtomicReference<SimpleGrayScaleImage> im = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    if (isDisplaySupported) {
      im.set(new SimpleGrayScaleImage(slice, 5));
      im.get().setVisible();
    }
    double[] column = new double[generator.getSliceHeight()];
    int[] rowPlaceholder = new int[generator.getSliceWidth() - 1];
    int[] columnPlaceholder = new int[generator.getSliceHeight() - 1];
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

              for (double[] row : next) {
                AssertUtils.valuesContinuousInArray(row, rowPlaceholder);
                System.arraycopy(row, 0, column, 0, row.length);
                AssertUtils.valuesContinuousInArray(column, columnPlaceholder);
              }
            },
            30,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) {
            im.get().dispose();
          }
        });
    }finally{
      if(!executorService.isShutdown())
        executorService.shutdownNow();
    }
  }
}
