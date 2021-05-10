package org.lefmaroli.perlin.slice;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;

public class SliceGeneratorTest {

  private static final double noiseStepSize = 1.0 / 150.0;
  private static final double widthStepSize = 1.0 / 50;
  private static final double heightStepSize = 1.0 / 80;
  private static final int sliceWidth = 100;
  private static final int sliceHeight = 150;
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
            noiseStepSize + 1.0/6,
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
            widthStepSize + 1.0/9,
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
            heightStepSize + 1.0/15,
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
  public void visualizeLine() {
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
    double[][] slices = generator.getNext();
    double[][] patched = new double[generator.getSliceWidth() * 3][generator.getSliceHeight() * 3];
    for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
      for (int j = 0; j < generator.getSliceHeight() * 3; j++) {
        patched[i][j] = slices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
      }
    }
    LineChart chart = new LineChart("Morphing line", "length", "values");
    String label = "line";
    double[] ySlice = new double[generator.getSliceWidth() * 3];
    for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
      ySlice[i] = patched[i][25];
    }
    chart.addEquidistantDataSeries(ySlice, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> scheduledFuture =
        ses.scheduleAtFixedRate(
            () -> {
              double[][] newSlices = generator.getNext();
              for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
                for (int j = 0; j < generator.getSliceHeight() * 3; j++) {
                  patched[i][j] =
                      newSlices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
                }
              }
              for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
                ySlice[i] = patched[i][25];
              }
              chart.updateDataSeries(
                  dataSeries -> {
                    for (int i = 0; i < ySlice.length; i++) {
                      dataSeries.updateByIndex(i, ySlice[i]);
                    }
                  },
                  label);
            },
            5,
            15,
            TimeUnit.MILLISECONDS);

    ses.schedule(
        () -> {
          scheduledFuture.cancel(true);
          ses.shutdown();
        },
        15,
        TimeUnit.SECONDS);

    waitAtMost(17, TimeUnit.SECONDS).until(ses::isShutdown);
  }

  @Ignore("Skipped, only used to visualize results")
  @Test
  public void testCircularity() {
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
    double[][] slices = generator.getNext();
    double[][] patched = new double[generator.getSliceWidth() * 3][generator.getSliceHeight() * 3];
    for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
      for (int j = 0; j < generator.getSliceHeight() * 3; j++) {
        patched[i][j] = slices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(patched, 5);
    image.setVisible();

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> scheduledFuture =
        ses.scheduleAtFixedRate(
            () -> {
              double[][] newSlices = generator.getNext();
              for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
                for (int j = 0; j < generator.getSliceHeight() * 3; j++) {
                  patched[i][j] =
                      newSlices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
                }
              }
              image.updateImage(patched);
            },
            5,
            15,
            TimeUnit.MILLISECONDS);

    int testDurationInMs = 500;
    ses.schedule(
        () -> {
          scheduledFuture.cancel(true);
          ses.shutdown();
        },
        testDurationInMs,
        TimeUnit.SECONDS);

    waitAtMost(testDurationInMs + 1, TimeUnit.SECONDS).until(ses::isShutdown);
  }

  @Ignore("Skipped, only used to visualize results")
  @Test
  public void testVisualizeMorphingImage() {
    SliceGenerator generator =
        new SliceGenerator(
            noiseStepSize,
            widthStepSize,
            heightStepSize,
            500,
            500,
            1.0,
            System.currentTimeMillis(),
            false);
    double[][] slice = generator.getNext();
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slice, 5);
    image.setVisible();

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> scheduledFuture =
        ses.scheduleAtFixedRate(
            () -> image.updateImage(generator.getNext()), 5, 15, TimeUnit.MILLISECONDS);

    int testDurationInMs = 15;
    ses.schedule(
        () -> {
          scheduledFuture.cancel(true);
          ses.shutdownNow();
        },
        testDurationInMs,
        TimeUnit.SECONDS);

    waitAtMost(testDurationInMs + 1, TimeUnit.SECONDS).until(ses::isShutdown);
  }
}
