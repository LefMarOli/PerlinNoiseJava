package org.lefmaroli.perlin.generators.line;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
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
import org.lefmaroli.perlin.generators.LayeredGeneratorBuilderException;
import org.lefmaroli.perlin.generators.slice.LayeredSliceGeneratorBuilder;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

class LayeredLineGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultLineLength = 125;
  private static final int numLayers = 3;
  private static final boolean isCircularDefault = false;
  private static final long randomSeed = System.currentTimeMillis();
  private static LayeredLineGeneratorBuilder defaultBuilder;
  private static LayeredLineGenerator defaultGenerator;

  @BeforeAll
  static void init() throws LayeredGeneratorBuilderException {
    defaultBuilder = new LayeredLineGeneratorBuilder(defaultLineLength);
    resetBuilder(defaultBuilder);
    defaultGenerator = defaultBuilder.build();
  }

  private static LayeredLineGeneratorBuilder resetBuilder(LayeredLineGeneratorBuilder builder) {
    builder
        .withNumberOfLayers(numLayers)
        .withRandomSeed(randomSeed)
        .withNoiseStepSizes(List.of(1 / 2048.0, 1.0 / 1024, 1.0 / 512))
        .withLineStepSizes(List.of(1 / 2048.0, 1.0 / 1024, 1.0 / 512))
        .withAmplitudes(List.of(1.0, 0.5, 0.25))
        .withCircularBounds(isCircularDefault)
        .withForkJoinPool(null)
        .withLayerExecutorService(null);
    return builder;
  }

  @BeforeEach
  void setup() {
    resetBuilder(defaultBuilder);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(2, defaultGenerator.getDimensions());
  }

  @Test
  void testNotNull() throws LayeredGeneratorBuilderException {
    Assertions.assertNotNull(defaultBuilder.build());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1, 89})
  void testCreateWrongNumberOfLayers(int numberOfLayers) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withNumberOfLayers(numberOfLayers));
  }

  @Test
  void testValuesLength() {
    double[] nextLine = defaultGenerator.getNext();
    Assertions.assertEquals(defaultLineLength, nextLine.length, 0);
  }

  @Test
  void testGetNextBoundedValues() {
    double[] line = defaultGenerator.getNext();
    for (double value : line) {
      Assertions.assertTrue(value >= 0.0, "Actual value smaller than 0.0: " + value);
      Assertions.assertTrue(value <= 1.0, "Actual value greater than 1.0: " + value);
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
    defaultGenerator = resetBuilder(defaultBuilder).build();
    LayeredLineGeneratorBuilder other =
        resetBuilder(new LayeredLineGeneratorBuilder(defaultLineLength));
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
    LayeredLineGeneratorBuilder other =
        resetBuilder(new LayeredLineGeneratorBuilder(defaultLineLength));
    return Stream.of(
        Arguments.of(
            resetBuilder(new LayeredLineGeneratorBuilder(defaultLineLength + 1)).build(),
            "Different line length"),
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
            resetBuilder(defaultBuilder).withLineStepSizes(List.of(0.2, 0.02, 0.002)).build(),
            "Different line step sizes"),
        Arguments.of(
            resetBuilder(defaultBuilder).withNoiseStepSizes(List.of(0.2, 0.02, 0.002)).build(),
            "Different noise step sizes"),
        Arguments.of(null, "null"),
        Arguments.of(new Random(), "object from different class"));
  }

  @Test
  void testLineLength() {
    Assertions.assertEquals(defaultLineLength, defaultGenerator.getLineLength());
  }

  //  @Test
  //  void testToString() {
  //    ToStringVerifier.forClass(LayeredLineGeneratorBuilder.LayeredLineGeneratorImpl.class)
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
    Assertions.assertFalse(defaultGenerator.isCircular());
  }

  @Test
  void testSmoothVisuals() throws LayeredGeneratorBuilderException { // NOSONAR
    double lineStepSizeInitialValue = 1.0 / 50;
    DoubleGenerator lineStepSizeGenerator =
        new DoubleGenerator(lineStepSizeInitialValue, 1.0 / 0.9);
    double noiseStepSizeInitialValue = 1.0 / 80;
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(noiseStepSizeInitialValue, 2.0);
    int lineLength = 200;
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    LayeredLineGenerator generator =
        new LayeredLineGeneratorBuilder(lineLength)
            .withLineStepSizes(lineStepSizeGenerator)
            .withNoiseStepSizes(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudes(amplitudeGenerator)
            .build();
    int requestedLines = 200;
    final double[][] image = new double[requestedLines][lineLength];
    for (int i = 0; i < requestedLines; i++) {
      double[] nextLine = generator.getNext();
      System.arraycopy(nextLine, 0, image[i], 0, lineLength);
    }

    AtomicReference<SimpleGrayScaleImage> im = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    if (isDisplaySupported) {
      im.set(new SimpleGrayScaleImage(image, 5));
      im.get().setVisible();
    }
    double[] row = new double[image.length];
    int[] placeholder = new int[lineLength - 1];
    int[] rowPlaceholder = new int[image.length - 1];
    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              for (int i = 0; i < requestedLines - 1; i++) {
                System.arraycopy(image[i + 1], 0, image[i], 0, lineLength);
              }
              if (Thread.interrupted()) {
                return;
              }
              double[] nextLine = generator.getNext();
              System.arraycopy(nextLine, 0, image[requestedLines - 1], 0, lineLength);
              if (isDisplaySupported) im.get().updateImage(image);
              try {
                AssertUtils.valuesContinuousInArray(nextLine, placeholder);

                for (int i = 0; i < lineLength; i++) {
                  System.arraycopy(image[i], 0, row, 0, image.length);
                  AssertUtils.valuesContinuousInArray(row, rowPlaceholder);
                }
              } catch (AssertionError e) {
                LogManager.getLogger(this.getClass())
                    .error("Error with line smoothness for line generator " + generator, e);
                throw e;
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
  }
}
