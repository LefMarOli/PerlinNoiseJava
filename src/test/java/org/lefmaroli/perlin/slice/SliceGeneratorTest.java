package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;

public class SliceGeneratorTest {

  private static final int noiseInterpolationPoints = 150;
  private static final int widthInterpolationPoints = 50;
  private static final int heightInterpolationPoints = 80;
  private static final int sliceWidth = 100;
  private static final int sliceHeight = 150;
  private static final double maxAmplitude = 1.0;
  private static final boolean isCircular = false;
  private final long randomSeed = System.currentTimeMillis();
  int requestedCount = 10;
  private SliceGenerator defaultGenerator;

  private static void assertExpectedArrayEqualsActual(
      double[] expected, double[] actual, double delta) {
    assertEquals(expected.length, actual.length, delta);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], actual[i], delta);
    }
  }

  @Before
  public void setup() {
    defaultGenerator =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidNoiseInterpolationPoints() {
    new SliceGenerator(
        -5,
        widthInterpolationPoints,
        heightInterpolationPoints,
        sliceWidth,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidWidthInterpolationPoints() {
    new SliceGenerator(
        noiseInterpolationPoints,
        -4,
        heightInterpolationPoints,
        sliceWidth,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidHeightInterpolationPoints() {
    new SliceGenerator(
        noiseInterpolationPoints,
        widthInterpolationPoints,
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
        noiseInterpolationPoints,
        widthInterpolationPoints,
        heightInterpolationPoints,
        -9,
        sliceHeight,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidSliceHeight() {
    new SliceGenerator(
        noiseInterpolationPoints,
        widthInterpolationPoints,
        heightInterpolationPoints,
        sliceWidth,
        -7,
        maxAmplitude,
        randomSeed,
        isCircular);
  }

  @Test
  public void getNextSlicesCorrectSize() {
    SliceNoiseData[] noiseData = defaultGenerator.getNext(requestedCount).getAsArray();
    assertEquals(requestedCount, noiseData.length, 0);
    for (SliceNoiseData slice : noiseData) {
      assertEquals(sliceWidth, slice.getSliceWidth(), 0);
      assertEquals(sliceHeight, slice.getSliceHeight(), 0);
    }
  }

  @Test
  public void testGetNoiseInterpolationPoints() {
    assertEquals(noiseInterpolationPoints, defaultGenerator.getNoiseInterpolationPoints());
  }

  @Test
  public void testGetWidthInterpolationPoints() {
    assertEquals(widthInterpolationPoints, defaultGenerator.getWidthInterpolationPoints());
  }

  @Test
  public void testGetHeightInterpolationPoints() {
    assertEquals(heightInterpolationPoints, defaultGenerator.getHeightInterpolationPoints());
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

  @Test(expected = IllegalArgumentException.class)
  public void testGetNextLinesInvalidCount() {
    defaultGenerator.getNext(-5);
  }

  @Test
  public void testHugeSlice() {
    SliceGenerator amplifiedLayer =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            10000,
            10000,
            maxAmplitude,
            randomSeed,
            isCircular);
  }

  @Test
  public void testValuesBounded() {
    double[][][] slices = defaultGenerator.getNext(10).getAsRawData();
    //        double min = Double.MAX_VALUE;
    //        double max = Double.MIN_VALUE;
    for (double[][] slice : slices) {
      for (double[] line : slice) {
        for (double value : line) {
          assertTrue("Value " + value + "not bounded by 0", value > 0.0);
          assertTrue("Value " + value + "not bounded by max amplitude", value < maxAmplitude);
          //                    if (value > max) {
          //                        max = value;
          //                    }
          //                    if (value < min) {
          //                        min = value;
          //                    }
        }
      }
    }
    //        LogManager.getLogger(this.getClass()).info("Min: " + min);
    //        LogManager.getLogger(this.getClass()).info("Max: " + max);
  }

  @Test
  public void testValuesMultipliedByMaxAmplitude() {
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    SliceGenerator amplifiedLayer =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            newMaxAmplitude,
            randomSeed,
            isCircular);

    double[][][] slices = defaultGenerator.getNext(requestedCount).getAsRawData();
    double[][][] amplifiedSlices = amplifiedLayer.getNext(requestedCount).getAsRawData();

    for (double[][] slice : slices) {
      for (double[] lines : slice) {
        for (int j = 0; j < lines.length; j++) {
          lines[j] = lines[j] * newMaxAmplitude;
        }
      }
    }

    for (int i = 0; i < slices.length; i++) {
      for (int j = 0; j < slices[0].length; j++) {
        assertExpectedArrayEqualsActual(slices[i][j], amplifiedSlices[i][j], 1e-18);
      }
    }
  }

  @Test
  public void testCreateSameGeneratedSlices() {
    SliceGenerator same =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            maxAmplitude,
            randomSeed,
            isCircular);
    double[][][] nextSegment1 = defaultGenerator.getNext(requestedCount).getAsRawData();
    double[][][] nextSegment2 = same.getNext(requestedCount).getAsRawData();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      for (int j = 0; j < nextSegment1[0].length; j++) {
        assertExpectedArrayEqualsActual(nextSegment1[i][j], nextSegment2[i][j], 0.0);
      }
    }
  }

  @Test
  public void testEquals() {
    SliceGenerator otherGenerator =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            noiseInterpolationPoints + 6,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            noiseInterpolationPoints,
            widthInterpolationPoints + 9,
            heightInterpolationPoints,
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
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints + 15,
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
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
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
            "results",
            "noiseSegmentLength",
            "currentPosInNoiseInterpolation")
        .verify();
  }

  @Test
  public void testCircularBounds() {
    SliceGenerator generator =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] line = generator.getNext(1).getAsRawData()[0];
    double[] firstLine = line[0];
    double[] secondLine = line[1];
    double[] lastLine = line[generator.getSliceWidth() - 1];
    for (int i = 0; i < firstLine.length; i++) {
      double mu = secondLine[i] - firstLine[i];
      double otherMu = firstLine[i] - lastLine[i];
      assertEquals(mu, otherMu, 1.0 / widthInterpolationPoints);
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
      assertEquals(mu, otherMu, 1.0 / heightInterpolationPoints);
    }
  }

  @Ignore
  @Test
  public void visualizeLine() {
    SliceGenerator generator =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] slices = generator.getNext(1).getAsRawData()[0];
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
    while (true) ;
  }

  @Ignore
  @Test
  public void testCircularity() throws InterruptedException {
    SliceGenerator generator =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] slices = generator.getNext(1).getAsRawData()[0];
    double[][] patched = new double[generator.getSliceWidth() * 3][generator.getSliceHeight() * 3];
    for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
      for (int j = 0; j < generator.getSliceHeight() * 3; j++) {
        patched[i][j] = slices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(patched, 1);
    image.setVisible();
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 1) {
        previousTime = System.currentTimeMillis();
        double[][] newSlices = generator.getNext(1).getAsRawData()[0];
        for (int i = 0; i < generator.getSliceWidth() * 3; i++) {
          for (int j = 0; j < generator.getSliceHeight() * 3; j++) {
            patched[i][j] =
                newSlices[i % generator.getSliceWidth()][j % generator.getSliceHeight()];
          }
        }
        image.updateImage(patched);
      } else {
        Thread.sleep(1);
      }
    }
  }

  @Ignore
  @Test
  public void testVisualizeMorphingImage() throws InterruptedException {
    SliceGenerator generator =
        new SliceGenerator(
            noiseInterpolationPoints,
            widthInterpolationPoints,
            heightInterpolationPoints,
            sliceWidth,
            sliceHeight,
            1.0,
            System.currentTimeMillis(),
            false);
    int count = 1;
    double[][][] slices = generator.getNext(count).getAsRawData();
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slices[0], 1);
    image.setVisible();
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 5) {
        previousTime = System.currentTimeMillis();
        double[][][] newSlices = generator.getNext(1).getAsRawData();
        image.updateImage(newSlices[0]);
      } else {
        Thread.sleep(1);
      }
    }
  }
}