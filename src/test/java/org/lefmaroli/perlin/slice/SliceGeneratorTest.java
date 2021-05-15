package org.lefmaroli.perlin.slice;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.awt.GraphicsEnvironment;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

class SliceGeneratorTest {

  private static final double noiseStepSize = 1.0 / 10.0;
  private static final double widthStepSize = 1.0 / 100;
  private static final double heightStepSize = 1.0 / 200;
  private static final int sliceWidth = 50;
  private static final int sliceHeight = 75;
  private static final double maxAmplitude = 1.0;
  private static final boolean isCircular = false;
  private final long randomSeed = System.currentTimeMillis();
  private SliceGenerator defaultGenerator;

  private static final ErrorMessageSupplier errorMessageSupplier = new ErrorMessageSupplier();

  private static class ErrorMessageSupplier implements Supplier<String> {
    private int i;
    private int j;
    private double val;

    @Override
    public String get() {
      return "Values are equal for i: " + i + ", j: " + j + ", val: " + val;
    }

    public ErrorMessageSupplier setI(int i) {
      this.i = i;
      return this;
    }

    public ErrorMessageSupplier setJ(int j) {
      this.j = j;
      return this;
    }

    public ErrorMessageSupplier setVal(double val) {
      this.val = val;
      return this;
    }
  }

  @BeforeEach
  void setup() {
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

  @Test
  void testCreateInvalidNoiseStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new SliceGenerator(
                -5,
                widthStepSize,
                heightStepSize,
                sliceWidth,
                sliceHeight,
                maxAmplitude,
                randomSeed,
                isCircular));
  }

  @Test
  void testCreateInvalidWidthStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new SliceGenerator(
                noiseStepSize,
                -4,
                heightStepSize,
                sliceWidth,
                sliceHeight,
                maxAmplitude,
                randomSeed,
                isCircular));
  }

  @Test
  void testCreateInvalidHeightStepSize() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new SliceGenerator(
                noiseStepSize,
                widthStepSize,
                -5,
                sliceWidth,
                sliceHeight,
                maxAmplitude,
                randomSeed,
                isCircular));
  }

  @Test
  void testCreateInvalidSliceWidth() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new SliceGenerator(
                noiseStepSize,
                widthStepSize,
                heightStepSize,
                -9,
                sliceHeight,
                maxAmplitude,
                randomSeed,
                isCircular));
  }

  @Test
  void testCreateInvalidSliceHeight() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new SliceGenerator(
                noiseStepSize,
                widthStepSize,
                heightStepSize,
                sliceWidth,
                -7,
                maxAmplitude,
                randomSeed,
                isCircular));
  }

  @Test
  void getNextSlicesCorrectSize() {
    double[][] noiseData = defaultGenerator.getNext();
    Assertions.assertEquals(sliceWidth, noiseData.length, 0);
    for (double[] line : noiseData) {
      Assertions.assertEquals(sliceHeight, line.length, 0);
    }
  }

  @Test
  void testGetNoiseStepSize() {
    Assertions.assertEquals(noiseStepSize, defaultGenerator.getNoiseStepSize(), 1E-9);
  }

  @Test
  void testGetWidthStepSize() {
    Assertions.assertEquals(widthStepSize, defaultGenerator.getWidthStepSize(), 1E-9);
  }

  @Test
  void testGetHeightStepSize() {
    Assertions.assertEquals(heightStepSize, defaultGenerator.getHeightStepSize(), 1E-9);
  }

  @Test
  void testGetSliceWidth() {
    Assertions.assertEquals(sliceWidth, defaultGenerator.getSliceWidth());
  }

  @Test
  void testGetSliceHeight() {
    Assertions.assertEquals(sliceHeight, defaultGenerator.getSliceHeight());
  }

  @Test
  void testGetMaxAmplitude() {
    Assertions.assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testHugeSlice() {
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
    Assertions.assertNotNull(sliceGenerator);
  }

  @Test
  void testValuesBounded() {
    double[][] slice = defaultGenerator.getNext();
    for (double[] line : slice) {
      for (double value : line) {
        Assertions.assertTrue(value > 0.0, "Value " + value + "not bounded by 0");
        Assertions.assertTrue(
            value < maxAmplitude, "Value " + value + "not bounded by max amplitude");
      }
    }
  }

  @Test
  void testValuesMultipliedByMaxAmplitude() {
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
        Assertions.assertEquals(slice[i][j], amplifiedSlice[i][j], 1e-18);
      }
    }
  }

  @Test
  void testCreateSameGeneratedSlices() {
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

    Assertions.assertEquals(nextSegment1.length, nextSegment2.length, 0);
    Assertions.assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      for (int j = 0; j < nextSegment1[0].length; j++) {
        Assertions.assertEquals(nextSegment1[i][j], nextSegment2[i][j], 0.0);
      }
    }
  }

  @Test
  void testCreateDifferentGeneratedSlicesForDifferentRandomSeed() {
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

    Assertions.assertEquals(nextSegment1.length, nextSegment2.length, 0);
    Assertions.assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      for (int j = 0; j < nextSegment1[0].length; j++) {
        double val = nextSegment1[i][j];
        Assertions.assertNotEquals(val, nextSegment2[i][j], 0.0, getErrorMessage(i, j, val));
      }
    }
  }

  private static Supplier<String> getErrorMessage(int i, int j, double val) {
    return errorMessageSupplier.setI(i).setJ(j).setVal(val);
  }

  @Test
  void testEquals() {
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
    Assertions.assertEquals(defaultGenerator, otherGenerator);
    Assertions.assertEquals(defaultGenerator.hashCode(), otherGenerator.hashCode());
  }

  @Test
  void testNotEqualsNotSameNoiseInterpolationPoints() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameWidthInterpolationPoints() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameHeightInterpolationPoints() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameSliceWidth() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameSliceHeight() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameMaxAmplitude() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameRandomSeed() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testNotEqualsNotSameCircularity() {
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
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(SliceGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "widthAngleFactor",
            "heightAngleFactor",
            "perlin",
            "perlinData",
            "currentPosInNoiseInterpolation",
            "generated",
            "containers",
            "containersCount")
        .verify();
  }

  @Test
  void testSliceCircularity() {
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

    int numCyclesInWidth = (int) (generator.getSliceWidth() * generator.getWidthStepSize());
    int numInterpolationPointsPerCycleInWidth = (int) (1.0 / generator.getWidthStepSize());
    int numCyclesInHeight = (int) (generator.getSliceHeight() * generator.getHeightStepSize());
    int numInterpolationPointsPerCycleInHeight = (int) (1.0 / generator.getHeightStepSize());

    for (int i = 0; i < 100; i++) {
      double[][] slice = generator.getNext();

      for (int row = 0; row < generator.getSliceHeight(); row++) {
        for (int j = 0; j < numInterpolationPointsPerCycleInWidth; j++) {
          double ref = slice[j][row];
          for (int k = 1; k < numCyclesInWidth; k++) {
            Assertions.assertEquals(
                ref, slice[k * numInterpolationPointsPerCycleInWidth + j][row], 1E-12);
          }
        }
      }

      for (int column = 0; column < generator.getSliceWidth(); column++) {
        for (int j = 0; j < numInterpolationPointsPerCycleInHeight; j++) {
          double ref = slice[column][j];
          for (int k = 1; k < numCyclesInHeight; k++) {
            Assertions.assertEquals(
                ref, slice[column][k * numInterpolationPointsPerCycleInHeight + j], 1E-12);
          }
        }
      }
    }
  }

  @Test
  void testSmoothCircularity() { // NOSONAR
    SliceGenerator generator =
        new SliceGenerator(
            noiseStepSize, 1 / 200.0, 1 / 250.0, 150, 150, 1.0, System.currentTimeMillis(), true);
    double[][] slices = generator.getNext();
    int patchFactor = 2;
    double[][] patched =
        new double[generator.getSliceWidth() * patchFactor]
            [generator.getSliceHeight() * patchFactor];
    for (int i = 0; i < generator.getSliceWidth() * patchFactor; i++) {
      for (int j = 0; j < generator.getSliceHeight() * patchFactor; j++) {
        patched[i][j] = slices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
      }
    }

    AtomicReference<SimpleGrayScaleImage> im = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    if (isDisplaySupported) {
      im.set(new SimpleGrayScaleImage(patched, 5));
      im.get().setVisible();
    }

    double[] column = new double[generator.getSliceHeight()];
    int[] rowPlaceholder = new int[generator.getSliceWidth() - 1];
    int[] columnPlaceholder = new int[generator.getSliceHeight() - 1];

    CompletableFuture<Void> completed =
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
              if (isDisplaySupported) {
                im.get().updateImage(patched);
              }

              for (double[] row : newSlices) {
                AssertUtils.valuesContinuousInArray(row, rowPlaceholder);
                System.arraycopy(row, 0, column, 0, row.length);
                AssertUtils.valuesContinuousInArray(column, columnPlaceholder);
              }
            },
            200,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) im.get().dispose();
        });
  }

  @Test
  void testSmoothVisuals() { // NOSONAR
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
            100,
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
