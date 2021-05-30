package org.lefmaroli.perlin.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.awt.GraphicsEnvironment;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.perlin.configuration.TestJitterStrategy;
import org.lefmaroli.testutils.AssertUtils;
import org.lefmaroli.testutils.ScheduledUpdater;

class LineGeneratorTest {

  private static final int lineLength = 100;
  private static final double maxAmplitude = 1.0;
  private static final double defaultLineStepSize = 1.0 / 25;
  private static final double defaultNoiseStepSize = 1.0 / 100;
  private static final boolean isCircular = false;
  private static final long randomSeed = System.currentTimeMillis();
  private static LineGeneratorBuilder defaultBuilder;
  private static LineGenerator defaultGenerator;

  @BeforeAll
  static void init() throws StepSizeException {
    defaultBuilder = new LineGeneratorBuilder(lineLength);
    resetBuilder(defaultBuilder);
    defaultGenerator = defaultBuilder.build();
  }

  private static LineGeneratorBuilder resetBuilder(LineGeneratorBuilder builder)
      throws StepSizeException {
    builder
        .withTimeStepSize(defaultNoiseStepSize)
        .withLineStepSize(defaultLineStepSize)
        .withAmplitude(maxAmplitude)
        .withRandomSeed(randomSeed)
        .withCircularBounds(isCircular)
        .withForkJoinPool(null);
    return builder;
  }

  @BeforeEach
  void setup() {
    resetBuilder(defaultBuilder);
  }

  @Test
  void testInvalidLineStepSizeWithCircularity() {
    defaultBuilder.withLineStepSize(5.0).withCircularBounds(true);
    Assertions.assertThrows(IllegalArgumentException.class, () -> defaultBuilder.build());
  }

  @Test
  void testBuildNoiseLineNotNull() {
    Assertions.assertNotNull(new LineGeneratorBuilder(lineLength).build());
  }

  @Test
  void testCreateInvalidLineLength() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> new LineGeneratorBuilder(-5));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-5, 0})
  void testCreateInvalidLineStepSize(double lineStepSize) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withLineStepSize(lineStepSize));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-5, 0})
  void testCreateInvalidNoiseStepSize(double noiseStepSize) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withTimeStepSize(noiseStepSize));
  }

  @Test
  void testGetDimension() {
    assertEquals(2, defaultGenerator.getDimensions());
  }

  @Test
  void testGetPool() {
    ForkJoinPool pool = ForkJoinPool.commonPool();
    LineGenerator generator = defaultBuilder.withForkJoinPool(pool).build();
    assertEquals(pool, generator.getExecutionPool());
  }

  @Test
  void testHasProcessingEnabled() {
    Assumptions.assumeTrue(ForkJoinPool.commonPool().getParallelism() > 1);
    LineGenerator generator = defaultBuilder.withForkJoinPool(ForkJoinPool.commonPool()).build();
    assertTrue(generator.hasParallelProcessingEnabled());
  }

  @Test
  void testHasProcessingDisabled() {
    assertFalse(defaultGenerator.hasParallelProcessingEnabled());
  }

  @Test
  void testHasProcessingDisabledNotEnoughParallelism() {
    ForkJoinPool pool = new ForkJoinPool(1);
    LineGenerator generator = defaultBuilder.withForkJoinPool(pool).build();
    assertFalse(generator.hasParallelProcessingEnabled());
    pool.shutdown();
  }

  @Test
  void getNextLinesCorrectSize() {
    double[] lines = defaultGenerator.getNext();
    assertEquals(lineLength, lines.length, 0);
  }

  @Test
  void testGetLineStepSize() {
    assertEquals(defaultLineStepSize, defaultGenerator.getLineStepSize(), 1E-9);
  }

  @Test
  void testGetNoiseStepSize() {
    assertEquals(defaultNoiseStepSize, defaultGenerator.getTimeStepSize(), 1E-8);
  }

  @Test
  void testGetLineLength() {
    assertEquals(lineLength, defaultGenerator.getLineLength());
  }

  @Test
  void testGetMaxAmplitude() {
    assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testValuesBounded() {
    double[] line = defaultGenerator.getNext();
    for (double value : line) {
      assertTrue(value > 0.0);
      assertTrue(value < maxAmplitude);
    }
  }

  @Test
  void testValuesMultipliedByMaxAmplitude() {
    defaultGenerator = defaultBuilder.build();
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    LineGenerator amplifiedLayer = defaultBuilder.withAmplitude(newMaxAmplitude).build();

    double[] line = defaultGenerator.getNext();
    double[] amplifiedLine = amplifiedLayer.getNext();

    for (int j = 0; j < line.length; j++) {
      line[j] = line[j] * newMaxAmplitude;
    }

    Assertions.assertArrayEquals(line, amplifiedLine);
  }

  @Test
  void testCreateSameGeneratedLines() {
    LineGenerator same = defaultBuilder.build();
    double[] nextSegment1 = defaultGenerator.getNext();
    double[] nextSegment2 = same.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      assertEquals(nextSegment1[i], nextSegment2[i], 0.0);
    }
  }

  @Test
  void testCreateSameGeneratedLinesWithPool() throws StepSizeException {
    TestJitterStrategy jitterStrategy = new TestJitterStrategy();
    try {
      int lineLength = 8000;
      LineGeneratorBuilder builder = new LineGeneratorBuilder(lineLength);
      resetBuilder(builder);

      LineGenerator layer = builder.build();
      builder.withForkJoinPool(ForkJoinPool.commonPool()).withJitterStrategy(jitterStrategy);
      LineGenerator same = builder.build();
      double[] unforked = layer.getNext();
      double[] forked = same.getNext();

      assertEquals(unforked.length, forked.length, 0);
      for (int i = 0; i < unforked.length; i++) {
        assertEquals(unforked[i], forked[i], 0.0);
      }
    } finally {
      jitterStrategy.shutdown();
    }
  }

  @Test
  void testCreateDifferentPointsForDifferentSeed() {
    LineGenerator differentSeedGenerator = defaultBuilder.withRandomSeed(randomSeed + 1).build();
    double[] nextSegment1 = defaultGenerator.getNext();
    double[] nextSegment2 = differentSeedGenerator.getNext();
    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      double val = nextSegment1[i];
      int index = i;
      Assertions.assertNotEquals(
          val, nextSegment2[i], () -> "Values are equal for i: " + index + ", value: " + val);
    }
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testEqualsSource")
  @SuppressWarnings("unused")
  void testEquals(Object first, Object second, String title) {
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testEqualsSource() throws StepSizeException {
    LineGeneratorBuilder other = resetBuilder(new LineGeneratorBuilder(lineLength));
    defaultGenerator = resetBuilder(defaultBuilder).build();
    return Stream.of(
        Arguments.of(
            other.build(), defaultBuilder.build(), "Different generators from different builders"),
        Arguments.of(
            defaultGenerator, defaultBuilder.build(), "Different generators from same builder"),
        Arguments.of(defaultGenerator, defaultGenerator, "Same generator"));
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("testNotEqualsSource")
  @SuppressWarnings("unused")
  void testNotEquals(Object other, String title) {
    Assertions.assertNotEquals(defaultGenerator, other);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testNotEqualsSource() throws StepSizeException {
    return Stream.of(
        Arguments.of(null, "null"),
        Arguments.of(new Random(), "Different object class"),
        Arguments.of(new LineGeneratorBuilder(lineLength + 1).build(), "Different line length"),
        Arguments.of(
            resetBuilder(defaultBuilder).withLineStepSize(defaultLineStepSize + 1.0 / 2),
            "Different line step size"),
        Arguments.of(
            resetBuilder(defaultBuilder).withTimeStepSize(defaultNoiseStepSize + 1.0 / 2),
            "Different noise step size"),
        Arguments.of(
            resetBuilder(defaultBuilder).withAmplitude(maxAmplitude + 1.0 / 2),
            "Different amplitude"),
        Arguments.of(
            resetBuilder(defaultBuilder).withRandomSeed(randomSeed + 1), "Different random seed"),
        Arguments.of(
            resetBuilder(defaultBuilder).withCircularBounds(!isCircular), "Different circularity"));
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(defaultGenerator.getClass())
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "perlinData",
            "currentPosition",
            "generated",
            "containers",
            "containersCount",
            "lineAngleFactor",
            "recycler",
            "lineLengthThreshold",
            "pool",
            "numberAvailableProcessors")
        .verify();
  }

  @Test
  void testLineCircularity() throws StepSizeException {
    LineGenerator generator =
        defaultBuilder
            .withLineStepSize(1 / 5.0)
            .withAmplitude(1.0)
            .withCircularBounds(true)
            .build();

    int numCyclesInLine = (int) (generator.getLineLength() * generator.getLineStepSize());
    int numInterpolationPointsPerCycle = (int) (1.0 / generator.getLineStepSize());

    for (int i = 0; i < 1000; i++) {
      double[] line = generator.getNext();

      for (int j = 0; j < numInterpolationPointsPerCycle; j++) {
        double ref = line[j];
        for (int k = 1; k < numCyclesInLine; k++) {
          assertEquals(ref, line[k * numInterpolationPointsPerCycle + j], 1E-12);
        }
      }
    }
  }

  @Test
  void testSmoothVisuals() throws StepSizeException { // NOSONAR
    int lineLength = 200;
    LineGenerator generator =
        new LineGeneratorBuilder(lineLength)
            .withTimeStepSize(1.0 / 50)
            .withLineStepSize(1.0 / 500)
            .withAmplitude(1.0)
            .withRandomSeed(randomSeed)
            .withCircularBounds(true)
            .withForkJoinPool(null)
            .build();
    int requested = 200;

    final double[][] image = new double[requested][lineLength * 2];
    double[] line;
    for (int i = 0; i < requested; i++) {
      line = generator.getNext();
      for (int j = 0; j < lineLength; j++) {
        image[i][j] = line[j];
        image[i][j + lineLength] = line[j];
      }
    }

    AtomicReference<SimpleGrayScaleImage> im = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    if (isDisplaySupported) {
      im.set(new SimpleGrayScaleImage(image, 5));
      im.get().setVisible();
    }
    double[] row = new double[image.length];
    int[] rowPlaceholder = new int[image.length - 1];
    int[] columnPlaceholder = new int[lineLength - 1];
    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              double[] newline = generator.getNext();
              if (Thread.interrupted()) {
                return;
              }
              for (int j = 0; j < requested - 1; j++) {
                for (int k = 0; k < newline.length; k++) {
                  image[j][k] = image[j + 1][k];
                  image[j][k + lineLength] = image[j + 1][k + lineLength];
                }
              }
              for (int i = 0; i < newline.length; i++) {
                image[image.length - 1][i] = newline[i];
                image[image.length - 1][i + lineLength] = newline[i];
              }
              try {
                AssertUtils.valuesContinuousInArray(newline, columnPlaceholder);
                for (int i = 0; i < lineLength; i++) {
                  System.arraycopy(image[i], 0, row, 0, image.length);
                  AssertUtils.valuesContinuousInArray(row, rowPlaceholder);
                }
              } catch (AssertionError e) {
                LogManager.getLogger(this.getClass())
                    .error("Error with line smoothness for line generator " + generator, e);
                throw e;
              }

              if (isDisplaySupported) im.get().updateImage(image);
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
