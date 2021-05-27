package org.lefmaroli.perlin.generators.line;

import java.awt.GraphicsEnvironment;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
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
import org.lefmaroli.configuration.JitterTrait;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.execution.TestJitterStrategy;
import org.lefmaroli.perlin.generators.StepSizeException;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

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
  }

  private static LineGeneratorBuilder resetBuilder(LineGeneratorBuilder builder)
      throws StepSizeException {
    builder
        .withNoiseStepSize(defaultNoiseStepSize)
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
    defaultGenerator = defaultBuilder.build();
  }

  @Test
  void testBuildNoiseLineNotNull() {
    Assertions.assertNotNull(new LineGeneratorBuilder(lineLength).build());
  }

  @Test
  void testCreateInvalidLineLength() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> new LineGeneratorBuilder(-5));
  }

  @Test
  void testCreateInvalidLineStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withLineStepSize(-5));
  }

  @Test
  void testCreateInvalidNoiseStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withNoiseStepSize(-1));
  }

  @Test
  void getNextLinesCorrectSize() {
    double[] lines = defaultGenerator.getNext();
    Assertions.assertEquals(lineLength, lines.length, 0);
  }

  @Test
  void testGetLineStepSize() {
    Assertions.assertEquals(defaultLineStepSize, defaultGenerator.getLineStepSize(), 1E-9);
  }

  @Test
  void testGetNoiseStepSize() {
    Assertions.assertEquals(defaultNoiseStepSize, defaultGenerator.getNoiseStepSize(), 1E-8);
  }

  @Test
  void testGetLineLength() {
    Assertions.assertEquals(lineLength, defaultGenerator.getLineLength());
  }

  @Test
  void testGetMaxAmplitude() {
    Assertions.assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testValuesBounded() {
    double[] line = defaultGenerator.getNext();
    for (double value : line) {
      Assertions.assertTrue(value > 0.0);
      Assertions.assertTrue(value < maxAmplitude);
    }
  }

  @Test
  void testValuesMultipliedByMaxAmplitude() {
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

    Assertions.assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      Assertions.assertEquals(nextSegment1[i], nextSegment2[i], 0.0);
    }
  }

  @Test
  void testCreateSameGeneratedLinesWithPool() throws StepSizeException {
    JitterTrait.setJitterStrategy(new TestJitterStrategy());
    int lineLength = 8000;
    LineGeneratorBuilder builder = new LineGeneratorBuilder(lineLength);
    resetBuilder(builder);

    LineGenerator layer = builder.build();
    builder.withForkJoinPool(ForkJoinPool.commonPool());
    LineGenerator same = builder.build();
    double[] unforked = layer.getNext();
    double[] forked = same.getNext();

    Assertions.assertEquals(unforked.length, forked.length, 0);
    for (int i = 0; i < unforked.length; i++) {
      Assertions.assertEquals(unforked[i], forked[i], 0.0);
    }
    JitterTrait.resetJitterStrategy();
  }

  @Test
  void testCreateDifferentPointsForDifferentSeed() {
    LineGenerator differentSeedGenerator = defaultBuilder.withRandomSeed(randomSeed + 1).build();
    double[] nextSegment1 = defaultGenerator.getNext();
    double[] nextSegment2 = differentSeedGenerator.getNext();
    Assertions.assertEquals(nextSegment1.length, nextSegment2.length, 0);
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
    Assertions.assertEquals(first, second);
    Assertions.assertEquals(first.hashCode(), second.hashCode());
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
            resetBuilder(defaultBuilder).withNoiseStepSize(defaultNoiseStepSize + 1.0 / 2),
            "Different noise step size"),
        Arguments.of(
            resetBuilder(defaultBuilder).withAmplitude(maxAmplitude + 1.0 / 2),
            "Different amplitude"),
        Arguments.of(
            resetBuilder(defaultBuilder).withRandomSeed(randomSeed + 1), "Different random seed"),
        Arguments.of(
            resetBuilder(defaultBuilder).withCircularBounds(!isCircular), "Different circularity"));
  }

  //  @Test
  //  void testToString() {
  //    ToStringVerifier.forClass(LineGeneratorBuilder.LineGeneratorImpl.class)
  //        .withClassName(NameStyle.SIMPLE_NAME)
  //        .withPreset(Presets.INTELLI_J)
  //        .withIgnoredFields(
  //            "perlinData",
  //            "currentPosition",
  //            "generated",
  //            "containers",
  //            "containersCount",
  //            "lineAngleFactor",
  //            "recycler",
  //            "lineLengthThreshold",
  //            "pool",
  //            "numberAvailableProcessors")
  //        .verify();
  //  }

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
          Assertions.assertEquals(ref, line[k * numInterpolationPointsPerCycle + j], 1E-12);
        }
      }
    }
  }

  @Test
  void testSmoothVisuals() throws StepSizeException { // NOSONAR
    int lineLength = 200;
    LineGenerator generator =
        new LineGeneratorBuilder(lineLength)
            .withNoiseStepSize(1.0 / 50)
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
