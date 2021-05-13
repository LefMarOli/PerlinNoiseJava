package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.utils.ScheduledUpdater;

public class SliceGeneratorTest {

  private static final double noiseStepSize = 1.0 / 10.0;
  private static final double widthStepSize = 1.0 / 100;
  private static final double heightStepSize = 1.0 / 200;
  private static final int sliceWidth = 50;
  private static final int sliceHeight = 75;
  private static final double maxAmplitude = 1.0;
  private static final boolean isCircular = false;
  private final long randomSeed = System.currentTimeMillis();
  private SliceGenerator defaultGenerator;

  @Before
  public void setup() {
    defaultGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidNoiseStepSize() {
    new SliceGenerator(
        -5,
        widthStepSize,
        heightStepSize,
        sliceWidth,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidWidthStepSize() {
    new SliceGenerator(
        noiseStepSize,
        -4,
        heightStepSize,
        sliceWidth,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidHeightStepSize() {
    new SliceGenerator(
        noiseStepSize,
        widthStepSize,
        -5,
        sliceWidth,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidSliceWidth() {
    new SliceGenerator(
        noiseStepSize,
        widthStepSize,
        heightStepSize,
        -9,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidSliceHeight() {
    new SliceGenerator(
        noiseStepSize,
        widthStepSize,
        heightStepSize,
        sliceWidth,
        -7,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test
  public void getNextSlicesCorrectSize() {
    double[][] noiseData = defaultGenerator.getNext();
    assertEquals(sliceWidth, noiseData.length, 0);
    for (double[] line : noiseData) {
      assertEquals(sliceHeight, line.length, 0);
    }
  }

  @Test
  public void testGetNoiseStepSize() {
    assertEquals(noiseStepSize, defaultGenerator.getNoiseStepSize(), 1E-9);
  }

  @Test
  public void testGetWidthStepSize() {
    assertEquals(widthStepSize, defaultGenerator.getWidthStepSize(), 1E-9);
  }

  @Test
  public void testGetHeightStepSize() {
    assertEquals(heightStepSize, defaultGenerator.getHeightStepSize(), 1E-9);
  }

  @Test
  public void testGetSliceWidth() {
    assertEquals(sliceWidth, defaultGenerator.getSliceWidth());
  }

  @Test
  public void testGetSliceHeight() {
    assertEquals(sliceHeight, defaultGenerator.getSliceHeight());
  }

  @Test
  public void testGetMaxAmplitude() {
    assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  public void testHugeSlice() {
    SliceGenerator sliceGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            10000,
            10000,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotNull(sliceGenerator);
  }

  @Test
  public void testValuesBounded() {
    double[][] slice = defaultGenerator.getNext();
    for (double[] line : slice) {
      for (double value : line) {
        assertTrue("Value " + value + "not bounded by 0", value > 0.0);
        assertTrue("Value " + value + "not bounded by max amplitude", value < maxAmplitude);
      }
    }
  }

  @Test
  public void testValuesMultipliedByMaxAmplitude() {
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    SliceGenerator amplifiedLayer =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            newMaxAmplitude,
            randomSeed,
            isCircular);

    double[][] slice = defaultGenerator.getNext();
    double[][] amplifiedSlice = amplifiedLayer.getNext();

    for (double[] lines : slice) {
      for (int j = 0; j < lines.length; j++) {
        lines[j] = lines[j] * newMaxAmplitude;
      }
    }

    for (int i = 0; i < slice.length; i++) {
      for (int j = 0; j < slice[0].length; j++) {
        assertEquals(slice[i][j], amplifiedSlice[i][j], 1e-18);
      }
    }
  }

  @Test
  public void testCreateSameGeneratedSlices() {
    SliceGenerator same =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    double[][] nextSegment1 = defaultGenerator.getNext();
    double[][] nextSegment2 = same.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      for (int j = 0; j < nextSegment1[0].length; j++) {
        assertEquals(nextSegment1[i][j], nextSegment2[i][j], 0.0);
      }
    }
  }

  @Test
  public void testCreateDifferentGeneratedSlicesForDifferentRandomSeed() {
    SliceGenerator diffRandSeed =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed + 1,
            isCircular);
    double[][] nextSegment1 = defaultGenerator.getNext();
    double[][] nextSegment2 = diffRandSeed.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      for (int j = 0; j < nextSegment1[0].length; j++) {
        double val = nextSegment1[i][j];
        assertNotEquals(
            "Values are equal for i: " + i + ", j: " + j + ", val: " + val,
            val,
            nextSegment2[i][j],
            0.0);
      }
    }
  }

  @Test
  public void testEquals() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertEquals(defaultGenerator, otherGenerator);
    assertEquals(defaultGenerator.hashCode(), otherGenerator.hashCode());
  }

  @Test
  public void testNotEqualsNotSameNoiseInterpolationPoints() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize + 1.0 / 6,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameWidthInterpolationPoints() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize + 1.0 / 9,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameHeightInterpolationPoints() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize + 1.0 / 15,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameSliceWidth() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth + 5,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameSliceHeight() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight + 6,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameMaxAmplitude() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude * 1.5,
            randomSeed,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameRandomSeed() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed + 5L,
            isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameCircularity() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            !isCircular);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(SliceGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "randomGenerator",
            "generated",
            "randomBoundsXCount",
            "randomBoundsYCount",
            "previousBounds",
            "currentBounds",
            "containers",
            "containersCount",
            "line",
            "noiseSegmentLength",
            "currentPosInNoiseInterpolation",
            "corners",
            "distances",
            "stepSize",
            "circularWidthResolution",
            "circularHeightResolution",
            "perlin",
            "perlinData")
        .verify();
  }

  @Test
  public void testCircularBounds() {
    SliceGenerator generator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] line = generator.getNext();
    double[] firstLine = line[0];
    double[] secondLine = line[1];
    double[] lastLine = line[generator.getSliceWidth() - 1];
    for (int i = 0; i < firstLine.length; i++) {
      double mu = secondLine[i] - firstLine[i];
      double otherMu = firstLine[i] - lastLine[i];
      assertEquals(mu, otherMu, widthStepSize);
    }

    double[] firstColumn = new double[generator.getSliceWidth()];
    double[] secondColumn = new double[generator.getSliceWidth()];
    double[] lastColumn = new double[generator.getSliceWidth()];
    for (int i = 0; i < generator.getSliceWidth(); i++) {
      firstColumn[i] = line[i][0];
      secondColumn[i] = line[i][1];
      lastColumn[i] = line[i][generator.getSliceHeight() - 1];
    }
    for (int i = 0; i < firstColumn.length; i++) {
      double mu = secondColumn[i] - firstColumn[i];
      double otherMu = firstColumn[i] - lastColumn[i];
      assertEquals(mu, otherMu, heightStepSize);
    }
  }

  @Ignore("Skipped, only used to visualize results")
  @Test
  public void testCircularity() {
    SliceGenerator generator =
        new SliceGenerator(
            noiseStepSize,
            1/50.0,
            1/250.0,
            150,
            150,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] slices = generator.getNext();
    int patchFactor = 2;
    double[][] patched = new double[generator.getSliceWidth() * patchFactor][generator.getSliceHeight() * patchFactor];
    for (int i = 0; i < generator.getSliceWidth() * patchFactor; i++) {
      for (int j = 0; j < generator.getSliceHeight() * patchFactor; j++) {
        patched[i][j] = slices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(patched, 5);
    image.setVisible();

    final double[][] previous = new double[generator.getSliceWidth() * patchFactor][generator.getSliceHeight() * patchFactor];
    for (int i = 0; i < generator.getSliceWidth() * patchFactor; i++) {
      System.arraycopy(patched[i], 0, previous[i], 0, generator.getSliceHeight() * patchFactor);
    }

    final double maxNoiseRate = Interpolation.getMaxStepWithFadeForStep(generator.getNoiseStepSize());
    final double maxWidthRate = Interpolation.getMaxStepWithFadeForStep(generator.getWidthStepSize());
    final double maxHeightRate = Interpolation.getMaxStepWithFadeForStep(generator.getHeightStepSize());
    final double maxHeightWidthRate = Math.sqrt((maxWidthRate*maxWidthRate) + (maxHeightRate*maxHeightRate));
    LogManager.getLogger(this.getClass()).info("MaxNoiseRate:" + maxNoiseRate);
    LogManager.getLogger(this.getClass()).info("MaxWidthRate:" + maxHeightWidthRate);
    LogManager.getLogger(this.getClass()).info("MaxHeightRate:" + maxHeightWidthRate);

    ScheduledUpdater.updateAtRateForDuration(
        () -> {
          double[][] newSlices = generator.getNext();
          if (Thread.interrupted()) {
            return;
          }
          for (int i = 0; i < generator.getSliceWidth() * patchFactor; i++) {
            for (int j = 0; j < generator.getSliceHeight() * patchFactor; j++) {
              patched[i][j] =
                  newSlices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
            }
          }
          image.updateImage(patched);
//          for (int i = 0; i < patched.length - 1; i++) {
//            for (int j = 0; j < patched[i].length; j++) {
//              double first = patched[i][j];
//              double second = patched[i + 1][j];
//              assertEquals(
//                  "Values differ more than " + maxWidthRate + " for width:" + Math.abs(first - second),
//                  first,
//                  second,
//                  maxWidthRate);
//            }
//          }
          if (Thread.interrupted()) {
            return;
          }
//          for (double[] rows : patched) {
//            for (int j = 0; j < rows.length - 1; j++) {
//              double first = rows[j];
//              double second = rows[j + 1];
//              assertEquals(
//                  "Values differ more than " + maxHeightWidthRate + " for height:" + Math
//                      .abs(first - second),
//                  first,
//                  second,
//                  maxHeightWidthRate);
//            }
//          }
          for (int i = 0; i < patched.length; i++) {
            for (int j = 0; j < patched[i].length; j++) {
              double first = previous[i][j];
              double second = patched[i][j];
              assertEquals(
                  "Values differ more than " + maxNoiseRate + " for noise:" + Math.abs(first - second),
                  first,
                  second,
                  maxNoiseRate);
            }
          }
          for (int i = 0; i < patched.length; i++) {
            System.arraycopy(patched[i], 0, previous[i], 0, patched[i].length);
          }
        },
        200,
        TimeUnit.MILLISECONDS,
        200,
        TimeUnit.SECONDS);
  }

  @Test
  public void testVisualizeMorphingImage() {
    int sliceWidth = 200;
    int sliceHeight = 200;
    SliceGenerator generator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            sliceWidth,
            sliceHeight,
            1.0,
            System.currentTimeMillis(),
            false);
    double[][] slice = generator.getNext();
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slice, 5);
    image.setVisible();

    final double[][] previous = new double[sliceWidth][sliceHeight];
    for (int i = 0; i < sliceWidth; i++) {
      System.arraycopy(slice[i], 0, previous[i], 0, sliceHeight);
    }

    final double maxNoiseRate = Interpolation.getMaxStepWithFadeForStep(noiseStepSize);
    final double maxWidthRate = Interpolation.getMaxStepWithFadeForStep(widthStepSize);
    final double maxHeightRate = Interpolation.getMaxStepWithFadeForStep(heightStepSize);

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
              double second = next[i + 1][j];
              assertEquals(
                  "Values differ more than " + maxWidthRate + " for width:" + Math.abs(first - second),
                  first,
                  second,
                  maxWidthRate);
            }
          }
          if (Thread.interrupted()) {
            return;
          }
          for (int i = 0; i < sliceWidth; i++) {
            for (int j = 0; j < sliceHeight - 1; j++) {
              double first = next[i][j];
              double second = next[i][j + 1];
              assertEquals(
                  "Values differ more than " + maxHeightRate + " for height:" + Math.abs(first - second),
                  first,
                  second,
                  maxHeightRate);
            }
          }
          for (int i = 0; i < sliceWidth; i++) {
            for (int j = 0; j < sliceHeight; j++) {
              double first = previous[i][j];
              double second = next[i][j];
              assertEquals(
                  "Values differ more than " + maxNoiseRate + " for noise:" + Math.abs(first - second),
                  first,
                  second,
                  maxNoiseRate);
            }
          }
          for (int i = 0; i < sliceWidth; i++) {
            System.arraycopy(next[i], 0, previous[i], 0, sliceHeight);
          }
        },
        100,
        TimeUnit.MILLISECONDS,
        15,
        TimeUnit.SECONDS);
  }
}
