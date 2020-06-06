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

  private final int lineLength = 200;
  private final int requestedLines = 700;
  private LineGenerator defaultLineGenerator;
  private double maxAmplitude = 5.0;
  private long randomSeed = System.currentTimeMillis();
  private int defaultInterpolationPointsAlongLine = 25;
  private int defaultInterpolationPointsAlongNoiseSpace = 50;
  private boolean isCircular = false;

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
    double[][] lines = defaultLineGenerator.getNext(requestedLines);
    assertEquals(requestedLines, lines.length, 0);
    for (double[] line : lines) {
      assertEquals(lineLength, line.length, 0);
    }
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

  @Test(expected = IllegalArgumentException.class)
  public void testGetNextLinesInvalidCount() {
    defaultLineGenerator.getNext(-5);
  }

  @Test
  public void testValuesBounded() {
    double[][] lines = defaultLineGenerator.getNext(requestedLines);
    for (double[] line : lines) {
      for (double value : line) {
        assertTrue(value > 0.0);
        assertTrue(value < maxAmplitude);
      }
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

    double[][] lines = layer.getNext(requestedLines);
    double[][] amplifiedLines = amplifiedLayer.getNext(requestedLines);

    for (double[] line : lines) {
      for (int j = 0; j < line.length; j++) {
        line[j] = line[j] * newMaxAmplitude;
      }
    }

    for (int i = 0; i < lines.length; i++) {
      assertExpectedArrayEqualsActual(lines[i], amplifiedLines[i], 1e-18);
    }
  }

  @Test
  public void testCreateSameGeneratedLines() {
    long randomSeed = System.currentTimeMillis();
    LineGenerator layer = new LineGenerator(50, 50, lineLength, 1.0, randomSeed, isCircular);
    LineGenerator sameLayer = new LineGenerator(50, 50, lineLength, 1.0, randomSeed, isCircular);
    double[][] nextSegment1 = layer.getNext(requestedLines);
    double[][] nextSegment2 = sameLayer.getNext(requestedLines);

    assertEquals(nextSegment1.length, nextSegment2.length, 0);
    for (int i = 0; i < nextSegment1.length; i++) {
      assertExpectedArrayEqualsActual(nextSegment1[i], nextSegment2[i], 0.0);
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
            "currentPosInNoiseInterpolation",
            "corners",
            "distances")
        .verify();
  }

  @Test
  public void testHugeLine() {
    LineGenerator otherGenerator =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            defaultInterpolationPointsAlongLine,
            2000000,
            1.0,
            randomSeed,
            false);
    int expected = 10;
    double[][] nextLines = otherGenerator.getNext(expected);
    assertEquals(expected, nextLines.length);
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
    double[] line = otherGenerator.getNext(1)[0];
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
            defaultInterpolationPointsAlongLine,
            defaultInterpolationPointsAlongNoiseSpace,
            lineLength,
            1.0,
            randomSeed,
            true);
    double[][] lines = generator.getNext(requestedLines);
    double[][] appended = new double[requestedLines][lineLength * 2];
    for (int i = 0; i < lineLength; i++) {
      for (int j = 0; j < requestedLines; j++) {
        appended[j][i] = lines[j][i];
        appended[j][i + lineLength] = lines[j][i];
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(appended, 5);
    image.setVisible();
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 5) {
        previousTime = System.currentTimeMillis();
        System.arraycopy(appended, 1, appended, 0, appended.length - 1);
        double[] newValues = generator.getNext(1)[0];
        double[] appendedNewValues = new double[lineLength * 2];
        for (int i = 0; i < newValues.length; i++) {
          appendedNewValues[i] = newValues[i];
          appendedNewValues[i + lineLength] = newValues[i];
        }
        appended[lines.length - 1] = appendedNewValues;
        image.updateImage(appended);
      } else {
        Thread.sleep(2);
      }
    }
  }

  @Ignore("Fake test to visualize data, doesn't assert anything")
  @Test
  public void testAppendCircularLines() {
    LineGenerator layer2D =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            100,
            lineLength,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] lines = layer2D.getNext(1);
    List<Double> values = new ArrayList<>();
    double[] line = lines[0];
    for (Double aDouble : line) {
      values.add(aDouble);
    }
    for (Double aDouble : line) {
      values.add(aDouble);
    }
    LineChart chart = new LineChart("Morphing line", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(values, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);

    long previousTime = System.currentTimeMillis();
    while (true)
      ;
  }

  @Ignore("Fake test to visualize data, doesn't assert anything")
  @Test
  public void testMorphingLine() throws InterruptedException {
    //Tranform into testable test like circualr bounds
    LineGenerator layer2D =
        new LineGenerator(
            defaultInterpolationPointsAlongNoiseSpace,
            100,
            lineLength,
            1.0,
            System.currentTimeMillis(),
            true);
    double[][] lines = layer2D.getNext(1);
    LineChart chart = new LineChart("Morphing line", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(lines[0], label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);

    long previousTime = System.currentTimeMillis();
    //        while(true);
    while (true) {
      if (System.currentTimeMillis() - previousTime > 15) {
        previousTime = System.currentTimeMillis();
        System.arraycopy(lines, 1, lines, 0, lines.length - 1);
        lines[lines.length - 1] = layer2D.getNext(1)[0];
        EventQueue.invokeLater(
            () ->
                chart.updateDataSeries(
                    dataSeries -> {
                      for (int i = 0; i < lines[0].length; i++) {
                        dataSeries.updateByIndex(i, lines[0][i]);
                      }
                    },
                    label));
      } else {
        Thread.sleep(2);
      }
    }
  }
}
