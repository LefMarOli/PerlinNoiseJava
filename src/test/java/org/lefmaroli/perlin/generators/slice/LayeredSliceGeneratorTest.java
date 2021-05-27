package org.lefmaroli.perlin.generators.slice;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.generators.LayeredBuilder;
import org.lefmaroli.perlin.generators.LayeredGeneratorBuilderException;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

class LayeredSliceGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultSliceWidth = 200;
  private static final int defaultSliceHeight = 200;
  private static final boolean isCircularDefault = false;
  private static final long randomSeed = System.currentTimeMillis();
  private static final int numLayers = 3;
  private static LayeredSliceGenerator defaultGenerator;
  private static LayeredSliceGeneratorBuilder defaultBuilder;

  @BeforeAll
  static void init() throws LayeredGeneratorBuilderException {
    defaultBuilder = new LayeredSliceGeneratorBuilder(defaultSliceWidth, defaultSliceHeight);
    resetBuilder(defaultBuilder);
    defaultGenerator = defaultBuilder.build();
  }

  private static LayeredSliceGeneratorBuilder resetBuilder(LayeredSliceGeneratorBuilder builder) {
    return builder
        .withCircularBounds(isCircularDefault)
        .withForkJoinPool(null)
        .withAmplitudes(List.of(1.0, 0.5, 0.25))
        .withNumberOfLayers(numLayers)
        .withRandomSeed(randomSeed)
        .withNoiseStepSizes(List.of(1.0 / 100, 1.0 / 50, 1.0 / 25))
        .withWidthStepSizes(List.of(1.0 / 100, 1.0 / 50, 1.0 / 25))
        .withHeightStepSizes(List.of(1.0 / 100, 1.0 / 50, 1.0 / 25))
        .withLayerExecutorService(null);
  }

  @BeforeEach
  void setup() {
    resetBuilder(defaultBuilder);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(3, defaultGenerator.getDimensions());
  }

  @Test
  void testNotNull() throws LayeredGeneratorBuilderException {
    Assertions.assertNotNull(defaultBuilder.build());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1, 75})
  void testCreateWrongNumberOfLayers(int numberOfLayers) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withNumberOfLayers(numberOfLayers));
  }

  @Test
  void testGetNextCount() {
    double[][] slice = defaultGenerator.getNext();
    Assertions.assertEquals(defaultSliceWidth, slice.length, 0);
    for (double[] line : slice) {
      Assertions.assertEquals(defaultSliceHeight, line.length, 0);
    }
  }

  @Test
  void testGetNextBoundedValues() {
    double[][] slice = defaultGenerator.getNext();
    for (double[] lines : slice) {
      for (double value : lines) {
        Assertions.assertTrue(value >= 0.0, "Actual value smaller than 0.0: " + value);
        Assertions.assertTrue(value <= 1.0, "Actual value greater than 1.0:" + value);
      }
    }
  }

  @Test
  void testGetMaxAmplitude() {
    Assertions.assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testNumLayersGenerated() {
    Assertions.assertEquals(numLayers, defaultGenerator.getNumberOfLayers(), 0);
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testEqualsArgs")
  @SuppressWarnings("unused")
  void testEquals(Object first, Object second, String title) {
    Assertions.assertEquals(first, second);
    Assertions.assertEquals(first.hashCode(), second.hashCode());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testEqualsArgs() throws LayeredGeneratorBuilderException {
    LayeredSliceGeneratorBuilder other =
        resetBuilder(new LayeredSliceGeneratorBuilder(defaultSliceWidth, defaultSliceHeight));
    defaultGenerator = resetBuilder(defaultBuilder).build();
    return Stream.of(
        Arguments.of(
            defaultGenerator, defaultBuilder.build(), "Different generators from same builder"),
        Arguments.of(
            defaultGenerator, other.build(), "Different generators from different builders"),
        Arguments.of(defaultGenerator, defaultGenerator, "Same generator"));
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("testNotEqualsArgs")
  @SuppressWarnings("unused")
  void testNotEquals(Object other, String title) {
    Assertions.assertNotEquals(defaultGenerator, other);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testNotEqualsArgs() throws LayeredGeneratorBuilderException {
    LayeredSliceGeneratorBuilder other =
        resetBuilder(new LayeredSliceGeneratorBuilder(defaultSliceWidth, defaultSliceHeight));
    return Stream.of(
        Arguments.of(
            resetBuilder(
                    new LayeredSliceGeneratorBuilder(defaultSliceWidth + 1, defaultSliceHeight))
                .build(),
            "Different slice width"),
        Arguments.of(
            resetBuilder(
                    new LayeredSliceGeneratorBuilder(defaultSliceWidth, defaultSliceHeight + 1))
                .build(),
            "Different slice height"),
        Arguments.of(
            resetBuilder(defaultBuilder).withCircularBounds(!isCircularDefault).build(),
            "Different circularity"),
        Arguments.of(
            resetBuilder(defaultBuilder).withNumberOfLayers(numLayers - 1).build(),
            "Different number of layers"),
        Arguments.of(
            resetBuilder(defaultBuilder).withAmplitudes(List.of(1.0, 5.0, 6.0)).build(),
            "Different amplitudes"),
        Arguments.of(
            resetBuilder(defaultBuilder).withRandomSeed(randomSeed + 1).build(),
            "Different random seed"),
        Arguments.of(
            resetBuilder(defaultBuilder).withWidthStepSizes(List.of(0.2, 0.02, 0.002)).build(),
            "Different width step sizes"),
        Arguments.of(
            resetBuilder(defaultBuilder).withHeightStepSizes(List.of(0.2, 0.02, 0.002)).build(),
            "Different height step sizes"),
        Arguments.of(
            resetBuilder(defaultBuilder).withNoiseStepSizes(List.of(0.2, 0.02, 0.002)).build(),
            "Different noise step sizes"),
        Arguments.of(null, "null"),
        Arguments.of(new Random(), "object from different class"));
  }

  @Test
  void testSliceWidth() {
    Assertions.assertEquals(defaultSliceWidth, defaultGenerator.getSliceWidth());
  }

  @Test
  void testSliceHeight() {
    Assertions.assertEquals(defaultSliceHeight, defaultGenerator.getSliceHeight());
  }

  //  @Test
  //  void testToString() {
  //    ToStringVerifier.forClass(LayeredSliceGeneratorBuilder.LayeredSliceGeneratorImpl.class)
  //        .withClassName(NameStyle.SIMPLE_NAME)
  //        .withPreset(Presets.INTELLI_J)
  //        .withIgnoredFields(
  //            "scheduler",
  //            "jitterStrategy",
  //            "logger",
  //            "containers",
  //            "generated",
  //            "containersCount",
  //            "pool",
  //            "futures",
  //            "totalSize",
  //            "timeout",
  //            "executorService")
  //        .verify();
  //  }

  @Test
  void testNonCircularity() {

    LayeredBuilder.increaseLayerLimit(15);

    Assertions.assertFalse(defaultGenerator.isCircular());
  }

  @Test
  void testSmoothVisuals() throws LayeredGeneratorBuilderException { // NOSONAR
    DoubleGenerator widthStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.9);
    DoubleGenerator heightStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.7);
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(1.0 / 128, 1.0 / 0.5);
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    int sliceWidth = 200;
    int sliceHeight = 200;
    ExecutorService executorService =
        Executors.newFixedThreadPool(
            numLayers, new ThreadFactoryBuilder().setNameFormat("layer-thread-%d").build());
    LayeredSliceGenerator generator =
        new LayeredSliceGeneratorBuilder(sliceWidth, sliceHeight)
            .withWidthStepSizes(widthStepSizeGenerator)
            .withHeightStepSizes(heightStepSizeGenerator)
            .withNoiseStepSizes(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudes(amplitudeGenerator)
            .withLayerExecutorService(executorService)
            .build();
    try {
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
    } finally {
      if (!executorService.isShutdown()) executorService.shutdownNow();
    }
  }
}
