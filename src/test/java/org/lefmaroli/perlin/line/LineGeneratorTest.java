package org.lefmaroli.perlin.line;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

class LineGeneratorTest {

  private static final int lineLength = 10;
  private static final double maxAmplitude = 5.0;
  private static final double defaultLineStepSize = 1.0 / 25;
  private static final double defaultNoiseStepSize = 1.0 / 50;
  private static final boolean isCircular = false;
  private final long randomSeed = System.currentTimeMillis();

  @Test
  void testCreateInvalidLineLength() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new LineGenerator(
                defaultNoiseStepSize, defaultLineStepSize, -5, 1.0, randomSeed, isCircular, null));
  }

  @Test
  void testCreateInvalidLineStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new LineGenerator(
                defaultNoiseStepSize, -1, lineLength, 1.0, randomSeed, isCircular, null));
  }

  @Test
  void testCreateInvalidNoiseStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new LineGenerator(
                -1, defaultLineStepSize, lineLength, 1.0, randomSeed, isCircular, null));
  }

  @Test
  void getNextLinesCorrectSize() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    double[] lines = generator.getNext();
    Assertions.assertEquals(lineLength, lines.length, 0);
  }

  @Test
  void testGetLineStepSize() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertEquals(defaultLineStepSize, generator.getLineStepSize(), 1E-9);
  }

  @Test
  void testGetNoiseStepSize() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertEquals(defaultNoiseStepSize, generator.getNoiseStepSize(), 1E-8);
  }

  @Test
  void testGetLineLength() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertEquals(lineLength, generator.getLineLength());
  }

  @Test
  void testGetMaxAmplitude() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertEquals(maxAmplitude, generator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testValuesBounded() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            100,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    double[] line = generator.getNext();
    for (double value : line) {
      Assertions.assertTrue(value > 0.0);
      Assertions.assertTrue(value < maxAmplitude);
    }
  }

  @Test
  void testValuesMultipliedByMaxAmplitude() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular, null);
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    LineGenerator amplifiedLayer =
        new LineGenerator(
            1.0 / 50, 1.0 / 50, lineLength, newMaxAmplitude, randomSeed, isCircular, null);

    double[] line = layer.getNext();
    double[] amplifiedLine = amplifiedLayer.getNext();

    for (int j = 0; j < line.length; j++) {
      line[j] = line[j] * newMaxAmplitude;
    }

    for (int i = 0; i < line.length; i++) {
      Assertions.assertEquals(line[i], amplifiedLine[i], 1e-18);
    }
  }

  @Test
  void testCreateSameGeneratedLines() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular, null);
    LineGenerator sameLayer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular, null);
    double[] nextSegment1 = layer.getNext();
    double[] nextSegment2 = sameLayer.getNext();

    Assertions.assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      Assertions.assertEquals(nextSegment1[i], nextSegment2[i], 0.0);
    }
  }

  @Test
  void testCreateDifferentPointsForDifferentSeed() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer =
        new LineGenerator(1 / 50.0, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular, null);
    LineGenerator sameLayer =
        new LineGenerator(1 / 50.0, 1.0 / 50, lineLength, 1.0, randomSeed + 1, isCircular, null);
    double[] nextSegment1 = layer.getNext();
    double[] nextSegment2 = sameLayer.getNext();
    Assertions.assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      double val = nextSegment1[i];
      int index = i;
      Assertions.assertNotEquals(
          val, nextSegment2[i], () -> "Values are equal for i: " + index + ", value: " + val);
    }
  }

  @Test
  void testEquals() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertEquals(generator, otherGenerator);
    Assertions.assertEquals(generator.hashCode(), otherGenerator.hashCode());
  }

  @Test
  void testNotEqualsNotSameLineLength() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength + 10,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertNotEquals(generator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameLineStepSize() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize + 1.0 / 5,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertNotEquals(generator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameNoiseStepSize() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize + 0.125,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    Assertions.assertNotEquals(generator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameMaxAmplitude() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude * 2,
            randomSeed,
            isCircular,
            null);
    Assertions.assertNotEquals(generator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameRandomSeed() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed + 1,
            isCircular,
            null);
    Assertions.assertNotEquals(generator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameCircularity() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular,
            null);
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            !isCircular,
            null);
    Assertions.assertNotEquals(generator, otherGenerator);
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(LineGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "perlinData",
            "currentPosition",
            "generated",
            "containers",
            "containersCount",
            "lineAngleFactor")
        .verify();
  }

  @Test
  void testLineCircularity() {
    LineGenerator generator =
        new LineGenerator(1.0 / 100, 1 / 5.0, lineLength, 1.0, randomSeed, true, null);

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
  void testSmoothVisuals() { // NOSONAR
    int lineLength = 200;
    LineGenerator generator =
        new LineGenerator(1.0 / 50, 1 / 500.0, lineLength, 1.0, randomSeed, true, null);
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

  @ParameterizedTest
  @MethodSource("forkingtest")
  void testForkingThreshold(int lineLength, int lineSizeThreshold) {

    double noiseStepSize = 0.01;
    double lineStepSize = 0.05;
    double maxAmplitude = 1.0;
    LineGenerator generator =
        new LineGenerator(
            noiseStepSize, lineStepSize, lineLength, maxAmplitude, randomSeed, false, null);
    ForkJoinPool pool = null;
    try {
      pool = new ForkJoinPool(2);
      LineGenerator forkedGenerator =
          new LineGenerator(
              noiseStepSize, lineStepSize, lineLength, maxAmplitude, randomSeed, false, pool);

      int numIterations = 50000;
      long start, forkedStart, mean, forkedMean;
      ArrayList<Long> durations = new ArrayList<>(numIterations);
      ArrayList<Long> forkedDurations = new ArrayList<>(numIterations);
      for (int i = 0; i < numIterations; i++) {
        start = System.nanoTime();
        generator.getNext();
        durations.add(System.nanoTime() - start);
        forkedStart = System.nanoTime();
        forkedGenerator.getNext();
        forkedDurations.add(System.nanoTime() - forkedStart);
      }
      mean = durations.stream().reduce(Long::sum).get() / numIterations;
      forkedMean = forkedDurations.stream().reduce(Long::sum).get() / numIterations;

      Long sum = durations.stream().map(x -> (x - mean) * (x - mean)).reduce(Long::sum).get();
      double std = Math.sqrt(sum / ((double) numIterations));

      Long forkedSum =
          forkedDurations.stream()
              .map(x -> (x - forkedMean) * (x - forkedMean))
              .reduce(Long::sum)
              .get();
      double forkedStd = Math.sqrt(forkedSum / ((double) numIterations));

      LogManager.getLogger(this.getClass()).info("Unforked mean: " + mean + "±" + std);
      LogManager.getLogger(this.getClass()).info("Forked mean: " + forkedMean + "±" + forkedStd);

      long diff = mean - forkedMean;
      Assertions.assertTrue(diff > 0);

    } finally {
      if (pool != null) {
        pool.shutdown();
      }
    }
  }
}
