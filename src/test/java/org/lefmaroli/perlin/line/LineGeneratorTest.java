package org.lefmaroli.perlin.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;

public class LineGeneratorTest {

  private static final int lineLength = 200;
  private static final int requestedLines = 700;
  private LineGenerator defaultLineGenerator;
  private static final double maxAmplitude = 5.0;
  private final long randomSeed = System.currentTimeMillis();
  private static final int defaultInterpolationPointsAlongLine = 25;
  private static final int defaultInterpolationPointsAlongNoiseSpace = 50;
  private static final boolean isCircular = false;

  private static void assertExpectedArrayEqualsActual(
      double[] expected, double[] actual, double delta) {
    assertEquals(expected.length, actual.length, delta);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], actual[i], delta);
    }
  }

  @Before
  public void setup() {
    defaultLineGenerator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidLineLength() {
    new LineGenerator(
        defaultInterpolationPointsAlongNoiseSpace,
        defaultInterpolationPointsAlongLine,
        -5,
        1.0,
        System.currentTimeMillis(),
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidInterpolationPointsAlongLine() {
    new LineGenerator(
        defaultInterpolationPointsAlongNoiseSpace,
        -1,
        lineLength,
        1.0,
        System.currentTimeMillis(),
        isCircular);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidInterpolationPointsAlongNoiseSpace() {
    new LineGenerator(
        -1,
        defaultInterpolationPointsAlongLine,
        lineLength,
        1.0,
        System.currentTimeMillis(),
        isCircular);
  }

  @Test
  public void getNextLinesCorrectSize() {
    double[] lines = defaultLineGenerator.getNext();
    assertEquals(lineLength, lines.length, 0);

  }

  @Test
  public void testGetInterpolationPointsAlongLine() {
    assertEquals(
        defaultInterpolationPointsAlongLine,
        defaultLineGenerator.getLineInterpolationPointsCount());
  }

  @Test
  public void testGetInterpolationPointsAlongNoiseSpace() {
    assertEquals(
        defaultInterpolationPointsAlongNoiseSpace,
        defaultLineGenerator.getNoiseInterpolationPoints());
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
    LineGenerator layer = new LineGenerator(50, 50, lineLength, 1.0, randomSeed, isCircular);
    Random random = new Random(System.currentTimeMillis());
    double newMaxAmplitude = random.nextDouble() * 100;
    LineGenerator amplifiedLayer =
        new LineGenerator(50, 50, lineLength, newMaxAmplitude, randomSeed, isCircular);

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
    LineGenerator layer = new LineGenerator(50, 50, lineLength, 1.0, randomSeed, isCircular);
    LineGenerator sameLayer = new LineGenerator(50, 50, lineLength, 1.0, randomSeed, isCircular);
    double[] nextSegment1 = layer.getNext();
    double[] nextSegment2 = sameLayer.getNext();

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      assertEquals(nextSegment1[i], nextSegment2[i], 0.0);
    }
  }

  @Test
  public void testEquals() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
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
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
            lineLength + 10,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameInterpolationPointsAlongLine() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine + 5,
            lineLength,
            maxAmplitude,
            randomSeed,
            isCircular);
    assertNotEquals(defaultLineGenerator, otherGenerator);
  }

  @Test
  public void testNotEqualsNotSameInterpolationPointsAlongNoiseSpace() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace + 8,
            defaultInterpolationPointsAlongLine,
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
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
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
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
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
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
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
            "results",
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
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
            2000000,
            1.0,
            randomSeed,
            false);
    double[] nextLine = otherGenerator.getNext();
    assertEquals(lineLength, nextLine.length);
  }

  @Test
  public void testCircularBounds() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
            lineLength,
            1.0,
            randomSeed,
            true);
    double[] line = otherGenerator.getNext();
    double firstValue = line[0];
    double secondValue = line[1];
    double mu = secondValue - firstValue;
    double lastValue = line[otherGenerator.getLineLength() - 1];
    double otherMu = firstValue - lastValue;
    assertEquals(mu, otherMu, 1.0 / defaultInterpolationPointsAlongLine);
  }

  @Ignore("Fake test to visualize data, doesn't assert anything")
  @Test
  public void getNextLines() throws InterruptedException {
    LineGenerator generator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace, 500, lineLength, 1.0, randomSeed, true);
    int requested = 10;

    double[][] image = new double[requestedLines][lineLength * 2];
    double[] line;
    for (int i = 0; i < lineLength; i++) {
      line = generator.getNext();
      for (int j = 0; j < requested; j++) {
        image[j][i] = line[i];
        image[j][i + lineLength] = line[i];
      }
    }
    SimpleGrayScaleImage im = new SimpleGrayScaleImage(image, 5);
    im.setVisible();

    double[][] newImage = new double[requestedLines][lineLength * 2];
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 5) {
        previousTime = System.currentTimeMillis();
        System.arraycopy(image, 1, newImage, 0, image.length - 1);
        line = generator.getNext();
        for (int i = 0; i < line.length; i++) {
          newImage[image.length - 1][i] = line[i];
          newImage[image.length - 1][i + lineLength] = line[i];
        }
        im.updateImage(newImage);
        image = newImage;
       } else {
        Thread.sleep(2);
      }
    }
  }

  @Ignore("Fake test to visualize data, doesn't assert anything")
  @Test
  public void testMorphingLine() throws InterruptedException {
    // Transform into testable test like circular bounds
    LineGenerator layer2D =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            100,
            lineLength,
            1.0,
            System.currentTimeMillis(),
            true);
    double[] line = layer2D.getNext();
    LineChart chart = new LineChart("Morphing line", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(line, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);

    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 15) {
        previousTime = System.currentTimeMillis();
        double[] newline = layer2D.getNext();
        EventQueue.invokeLater(
            () ->
                chart.updateDataSeries(
                    dataSeries -> {
                      for (int i = 0; i < newline.length; i++) {
                        dataSeries.updateByIndex(i, newline[i]);
                      }
                    },
                    label));
      } else {
        Thread.sleep(2);
      }
    }
  }
}
