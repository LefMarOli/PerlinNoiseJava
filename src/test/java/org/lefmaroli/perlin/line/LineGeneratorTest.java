package org.lefmaroli.perlin.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.utils.ScheduledUpdater;

public class LineGeneratorTest {

  private static final int lineLength = 200;
  private static final double maxAmplitude = 5.0;
  private static final double defaultLineStepSize = 1.0 / 25;
  private static final double defaultNoiseStepSize = 1.0 / 50;
  private static final boolean isCircular = false;
  private final long randomSeed = System.currentTimeMillis();
  private LineGenerator defaultLineGenerator;

  @Before
  public void setup() {
    defaultLineGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidLineLength() {
    new LineGenerator(
        defaultNoiseStepSize, defaultLineStepSize, -5, 1.0, System.currentTimeMillis(), isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidLineStepSize() {
    new LineGenerator(
        defaultNoiseStepSize, -1, lineLength, 1.0, System.currentTimeMillis(), isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidNoiseStepSize() {
    new LineGenerator(
        -1, defaultLineStepSize, lineLength, 1.0, System.currentTimeMillis(), isCircular);
  }

  @Test
  public void getNextLinesCorrectSize() {
    double[] lines = defaultLineGenerator.getNext();
    assertEquals(lineLength, lines.length, 0);
  }

  @Test
  public void testGetLineStepSize() {
    assertEquals(defaultLineStepSize, defaultLineGenerator.getLineStepSize(), 1E-9);
  }

  @Test
  public void testGetNoiseStepSize() {
    assertEquals(defaultNoiseStepSize, defaultLineGenerator.getNoiseStepSize(), 1E-8);
  }

  @Test
  public void testGetLineLength() {
    assertEquals(lineLength, defaultLineGenerator.getLineLength());
  }

  @Test
  public void testGetMaxAmplitude() {
    assertEquals(maxAmplitude, defaultLineGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  public void testValuesBounded() {
    double[] line = defaultLineGenerator.getNext();
    for (double value : line) {
      assertTrue(value > 0.0);
      assertTrue(value < maxAmplitude);
    }
  }

  @Test
  public void testValuesMultipliedByMaxAmplitude() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular);
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    LineGenerator amplifiedLayer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, newMaxAmplitude, randomSeed, isCircular);

    double[] line = layer.getNext();
    double[] amplifiedLine = amplifiedLayer.getNext();

    for (int j = 0; j < line.length; j++) {
      line[j] = line[j] * newMaxAmplitude;
    }

    for (int i = 0; i < line.length; i++) {
      assertEquals(line[i], amplifiedLine[i], 1e-18);
    }
  }

  @Test
  public void testCreateSameGeneratedLines() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular);
    LineGenerator sameLayer =
        new LineGenerator(1.0 / 50, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular);
    double[] nextSegment1 = layer.getNext();
    double[] nextSegment2 = sameLayer.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      assertEquals(nextSegment1[i], nextSegment2[i], 0.0);
    }
  }

  @Test
  public void testCreateDifferentPointsForDifferentSeed() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer =
        new LineGenerator(1 / 50.0, 1.0 / 50, lineLength, 1.0, randomSeed, isCircular);
    LineGenerator sameLayer =
        new LineGenerator(1 / 50.0, 1.0 / 50, lineLength, 1.0, randomSeed + 1, isCircular);
    double[] nextSegment1 = layer.getNext();
    double[] nextSegment2 = sameLayer.getNext();
    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      double val = nextSegment1[i];
      assertNotEquals("Values are equal for i: " + i + ", value: " + val, val, nextSegment2[i]);
    }
  }

  @Test
  public void testEquals() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertEquals(defaultLineGenerator, otherGenerator);
    assertEquals(defaultLineGenerator.hashCode(), otherGenerator.hashCode());
  }

  @Test
  public void testNotEqualsNotSameLineLength() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength + 10,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameLineStepSize() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize + 1.0 / 5,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameNoiseStepSize() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize + 0.125,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameMaxAmplitude() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude * 2,
            randomSeed,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameRandomSeed() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed + 1,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameCircularity() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize,
            defaultLineStepSize,
            lineLength,
            maxAmplitude,
            randomSeed,
            !isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(LineGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "lineSegmentLength",
            "noiseSegmentLength",
            "randomGenerator",
            "generated",
            "randomBoundsCount",
            "previousBounds",
            "currentBounds",
            "containers",
            "containersCount",
            "lineData",
            "currentPosition",
            "corners",
            "distances",
            "stepSize",
            "circularResolution",
            "perlin",
            "perlinData")
        .verify();
  }

  @Test
  public void testHugeLine() {
    int lineLength = 2000000;
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize, defaultLineStepSize, lineLength, 1.0, randomSeed, false);
    double[] nextLine = otherGenerator.getNext();
    assertEquals(lineLength, nextLine.length);
  }

  @Test
  public void testCircularBounds() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultNoiseStepSize, defaultLineStepSize, lineLength, 1.0, randomSeed, true);
    double[] line = otherGenerator.getNext();
    double firstValue = line[0];
    double secondValue = line[1];
    assertEquals(
        firstValue, secondValue, Interpolation.getMaxStepWithFadeForStep(defaultLineStepSize));
    double lastValue = line[otherGenerator.getLineLength() - 1];
    assertEquals(
        firstValue, lastValue, Interpolation.getMaxStepWithFadeForStep(defaultLineStepSize));
  }

  @Test
  public void testSmoothVisuals() {
    LineGenerator generator =
        new LineGenerator(
            defaultNoiseStepSize, defaultLineStepSize, lineLength, 1.0, randomSeed, true);
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
    SimpleGrayScaleImage im = new SimpleGrayScaleImage(image, 5);
    im.setVisible();

    double maxLineStep = Interpolation.getMaxStepWithFadeForStep(defaultLineStepSize);
    double maxNoiseStep = Interpolation.getMaxStepWithFadeForStep(defaultNoiseStepSize);

    CompletableFuture<Void> completed = ScheduledUpdater.updateAtRateForDuration(
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
          im.updateImage(image);

          for (int i = 0; i < (lineLength * 2) - 1; i++) {
            double first = image[image.length - 1][i];
            double second = image[image.length - 1][i + 1];
            assertEquals("Values differ more than " + maxLineStep, first, second, maxLineStep);
          }

          for (int i = 0; i < lineLength * 2; i++) {
            double first = image[image.length - 1][i];
            double second = image[image.length - 2][i];
            assertEquals("Values differ more than " + maxNoiseStep, first, second, maxNoiseStep);
          }
        },
        30,
        TimeUnit.MILLISECONDS,
        15,
        TimeUnit.SECONDS);
    completed.thenRun(im::dispose);
  }
}
