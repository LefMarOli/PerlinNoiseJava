package org.lefmaroli.perlin.generators.multidimensional.slice;

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
import java.util.function.Supplier;
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
import org.lefmaroli.perlin.configuration.JitterTrait;
import org.lefmaroli.perlin.configuration.TestJitterStrategy;
import org.lefmaroli.perlin.generators.StepSizeException;
import org.lefmaroli.testutils.AssertUtils;
import org.lefmaroli.testutils.ScheduledUpdater;

class SliceGeneratorTest {

  private static final double noiseStepSize = 1.0 / 10.0;
  private static final double widthStepSize = 1.0 / 100;
  private static final double heightStepSize = 1.0 / 200;
  private static final int sliceWidth = 50;
  private static final int sliceHeight = 75;
  private static final double maxAmplitude = 1.0;
  private static final boolean isCircular = false;
  private static final long randomSeed = System.currentTimeMillis();
  private static SliceGenerator defaultGenerator;
  private static SliceGeneratorBuilder defaultBuilder;

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

  @BeforeAll
  static void init() throws StepSizeException {
    defaultBuilder = new SliceGeneratorBuilder(sliceWidth, sliceHeight);
    resetBuilder(defaultBuilder);
    defaultGenerator = defaultBuilder.build();
  }

  @BeforeEach
  void setup() throws StepSizeException {
    resetBuilder(defaultBuilder);
  }

  static SliceGeneratorBuilder resetBuilder(SliceGeneratorBuilder builder)
      throws StepSizeException {
    builder
        .withNoiseStepSize(noiseStepSize)
        .withWidthStepSize(widthStepSize)
        .withHeightStepSize(heightStepSize)
        .withAmplitude(maxAmplitude)
        .withRandomSeed(randomSeed)
        .withCircularBounds(isCircular)
        .withForkJoinPool(null);
    return builder;
  }

  @Test
  void testDimension() {
    assertEquals(3, defaultGenerator.getDimensions());
  }

  @Test
  void testHasProcessingEnabled() {
    SliceGenerator generator = defaultBuilder.withForkJoinPool(ForkJoinPool.commonPool()).build();
    assertTrue(generator.hasParallelProcessingEnabled());
  }

  @Test
  void testHasProcessingDisabled() {
    assertFalse(defaultGenerator.hasParallelProcessingEnabled());
  }

  @Test
  void testHasProcessingDisabledNotEnoughParallelism() {
    ForkJoinPool pool = new ForkJoinPool(1);
    SliceGenerator generator = defaultBuilder.withForkJoinPool(pool).build();
    assertFalse(generator.hasParallelProcessingEnabled());
    pool.shutdown();
  }

  @Test
  void testInvalidHeightStepSizeWithCircularity() {
    defaultBuilder.withHeightStepSize(5.0).withCircularBounds(true);
    Assertions.assertThrows(IllegalArgumentException.class, () -> defaultBuilder.build());
  }

  @Test
  void testInvalidWidthStepSizeWithCircularity() {
    defaultBuilder.withWidthStepSize(5.0).withCircularBounds(true);
    Assertions.assertThrows(IllegalArgumentException.class, () -> defaultBuilder.build());
  }

  @Test
  void testBuildNoiseSliceNotNull() {
    Assertions.assertNotNull(new SliceGeneratorBuilder(sliceWidth, sliceHeight).build());
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("invalidDimensions")
  @SuppressWarnings("unused")
  void testInvalidDimensions(int sliceWidth, int sliceHeight, String title) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new SliceGeneratorBuilder(sliceWidth, sliceHeight));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidDimensions() {
    return Stream.of(Arguments.of(-5, 5, "invalid width"), Arguments.of(5, -5, "invalid height"));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-5, 0})
  void testCreateInvalidNoiseStepSize(double noiseStepSize) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withNoiseStepSize(noiseStepSize));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-5, 0})
  void testCreateInvalidWidthStepSize(double widthStepSize) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withWidthStepSize(widthStepSize));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-5, 0})
  void testCreateInvalidHeightStepSize(double heightStepSize) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withHeightStepSize(heightStepSize));
  }

  @Test
  void getNextSlicesCorrectSize() {
    double[][] noiseData = defaultGenerator.getNext();
    assertEquals(sliceWidth, noiseData.length, 0);
    for (double[] line : noiseData) {
      assertEquals(sliceHeight, line.length, 0);
    }
  }

  @Test
  void testGetNoiseStepSize() {
    assertEquals(noiseStepSize, defaultGenerator.getNoiseStepSize(), 1E-9);
  }

  @Test
  void testGetWidthStepSize() {
    assertEquals(widthStepSize, defaultGenerator.getWidthStepSize(), 1E-9);
  }

  @Test
  void testGetHeightStepSize() {
    assertEquals(heightStepSize, defaultGenerator.getHeightStepSize(), 1E-9);
  }

  @Test
  void testGetSliceWidth() {
    assertEquals(sliceWidth, defaultGenerator.getSliceWidth());
  }

  @Test
  void testGetSliceHeight() {
    assertEquals(sliceHeight, defaultGenerator.getSliceHeight());
  }

  @Test
  void testGetMaxAmplitude() {
    assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testHugeSlice() {
    SliceGenerator sliceGenerator = new SliceGeneratorBuilder(10000, 10000).build();
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
    defaultGenerator = defaultBuilder.build();
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    SliceGenerator amplifiedLayer = defaultBuilder.withAmplitude(newMaxAmplitude).build();

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
  void testCreateSameGeneratedSlices() {
    defaultGenerator = defaultBuilder.build();
    SliceGenerator same = defaultBuilder.build();
    double[][] nextSegment1 = defaultGenerator.getNext();
    double[][] nextSegment2 = same.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      Assertions.assertArrayEquals(nextSegment1[i], nextSegment2[i], 0.0);
    }
  }

  @Test
  void testCreateSameGeneratedSlicesWithPool() throws StepSizeException {
    JitterTrait.setJitterStrategy(new TestJitterStrategy());
    int width = 200;
    int height = 200;
    SliceGeneratorBuilder builder = new SliceGeneratorBuilder(width, height);
    resetBuilder(builder);

    SliceGenerator layer = builder.build();
    builder.withForkJoinPool(ForkJoinPool.commonPool());
    SliceGenerator same = builder.build();
    double[][] unforked = layer.getNext();
    double[][] forked = same.getNext();

    assertEquals(unforked.length, forked.length, 0);
    for (int i = 0; i < unforked.length; i++) {
      Assertions.assertArrayEquals(unforked[i], forked[i], 0.0);
    }
    JitterTrait.resetJitterStrategy();
  }

  @Test
  void testCreateDifferentGeneratedSlicesForDifferentRandomSeed() {
    SliceGenerator diffRandSeed = defaultBuilder.withRandomSeed(randomSeed + 1).build();
    double[][] nextSegment1 = defaultGenerator.getNext();
    double[][] nextSegment2 = diffRandSeed.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
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

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testEqualsSource")
  @SuppressWarnings("unused")
  void testEquals(Object first, Object second, String title) {
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testEqualsSource() throws StepSizeException {
    SliceGeneratorBuilder other = resetBuilder(new SliceGeneratorBuilder(sliceWidth, sliceHeight));
    defaultGenerator = resetBuilder(defaultBuilder).build();
    return Stream.of(
        Arguments.of(
            other.build(), defaultBuilder.build(), "Different generators from different builders"),
        Arguments.of(
            defaultGenerator, defaultBuilder.build(), "Different generators from same builder"),
        Arguments.of(defaultGenerator, defaultGenerator, "Same generator"));
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("notEquals")
  @SuppressWarnings("unused")
  void testNotEqual(Object other, String title) {
    Assertions.assertNotEquals(defaultGenerator, other);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> notEquals() throws StepSizeException {
    return Stream.of(
        Arguments.of(
            resetBuilder(defaultBuilder).withNoiseStepSize(noiseStepSize + 1.0 / 6).build(),
            "different noise step size"),
        Arguments.of(
            resetBuilder(defaultBuilder).withWidthStepSize(widthStepSize + 1.0 / 6).build(),
            "different width step size"),
        Arguments.of(
            resetBuilder(defaultBuilder).withHeightStepSize(heightStepSize + 1.0 / 6).build(),
            "different height step size"),
        Arguments.of(
            resetBuilder(defaultBuilder).withAmplitude(maxAmplitude + 2.0).build(),
            "different amplitude"),
        Arguments.of(
            resetBuilder(defaultBuilder).withRandomSeed(randomSeed + 1).build(),
            "different random seed"),
        Arguments.of(
            resetBuilder(defaultBuilder).withCircularBounds(!isCircular).build(),
            "different circularity"),
        Arguments.of(
            resetBuilder(new SliceGeneratorBuilder(sliceWidth + 1, sliceHeight)).build(),
            "different slice width"),
        Arguments.of(
            resetBuilder(new SliceGeneratorBuilder(sliceWidth, sliceHeight + 1)).build(),
            "different slice height"),
        Arguments.of(null, "null"),
        Arguments.of(new Random(), "object from different class"));
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(defaultGenerator.getClass())
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "widthAngleFactor",
            "heightAngleFactor",
            "perlinData",
            "recycler",
            "currentPosInNoiseInterpolation",
            "lengthThreshold",
            "pool",
            "numberAvailableProcessors",
            "generated",
            "containers",
            "containersCount")
        .verify();
  }

  @Test
  void testSliceCircularity() {
    SliceGenerator generator = defaultBuilder.withCircularBounds(true).build();

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
            assertEquals(ref, slice[k * numInterpolationPointsPerCycleInWidth + j][row], 1E-12);
          }
        }
      }

      for (int column = 0; column < generator.getSliceWidth(); column++) {
        for (int j = 0; j < numInterpolationPointsPerCycleInHeight; j++) {
          double ref = slice[column][j];
          for (int k = 1; k < numCyclesInHeight; k++) {
            assertEquals(ref, slice[column][k * numInterpolationPointsPerCycleInHeight + j], 1E-12);
          }
        }
      }
    }
  }

  @Test
  void testSmoothCircularity() throws StepSizeException { // NOSONAR
    SliceGenerator generator =
        resetBuilder(new SliceGeneratorBuilder(150, 150))
            .withWidthStepSize(1 / 200.0)
            .withHeightStepSize(1 / 250.0)
            .withCircularBounds(true)
            .withForkJoinPool(ForkJoinPool.commonPool())
            .build();
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
            30,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) im.get().dispose();
        });
  }

  @Test
  void testSmoothVisuals() throws StepSizeException { // NOSONAR
    int sliceWidth = 200;
    int sliceHeight = 200;
    SliceGenerator generator =
        resetBuilder(new SliceGeneratorBuilder(sliceWidth, sliceHeight))
            .withForkJoinPool(ForkJoinPool.commonPool())
            .build();
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
  }
}
