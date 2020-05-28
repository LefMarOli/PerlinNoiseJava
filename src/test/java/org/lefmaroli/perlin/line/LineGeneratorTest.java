package org.lefmaroli.perlin.line;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class LineGeneratorTest {

    private final int lineLength = 200;
    private final int requestedLines = 700;
    private LineGenerator defaultLineGenerator;
    private double maxAmplitude = 5.0;
    private long randomSeed = System.currentTimeMillis();
    private int defaultInterpolationPointsAlongLine = 25;
    private int defaultInterpolationPointsAlongNoiseSpace = 50;
    private boolean isCircular = false;

    @Before
    public void setup() {
        defaultLineGenerator = new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                defaultInterpolationPointsAlongNoiseSpace, maxAmplitude, randomSeed, isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidLineLength() {
        new LineGenerator(-5, defaultInterpolationPointsAlongLine, defaultInterpolationPointsAlongNoiseSpace, 1.0,
                System.currentTimeMillis(), isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidInterpolationPointsAlongLine() {
        new LineGenerator(lineLength, -1, defaultInterpolationPointsAlongNoiseSpace, 1.0,
                System.currentTimeMillis(), isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidInterpolationPointsAlongNoiseSpace() {
        new LineGenerator(lineLength, defaultInterpolationPointsAlongLine, -1, 1.0,
                System.currentTimeMillis(), isCircular);
    }

    @Test
    public void getNextLinesCorrectSize() {
        Double[][] lines = defaultLineGenerator.getNext(requestedLines);
        assertEquals(requestedLines, lines.length, 0);
        for (Double[] line : lines) {
            assertEquals(lineLength, line.length, 0);
        }
    }

    @Test
    public void testGetInterpolationPointsAlongLine() {
        assertEquals(defaultInterpolationPointsAlongLine, defaultLineGenerator.getLineInterpolationPointsCount());
    }

    @Test
    public void testGetInterpolationPointsAlongNoiseSpace() {
        assertEquals(defaultInterpolationPointsAlongNoiseSpace,
                defaultLineGenerator.getNoiseInterpolationPointsCount());
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
        Double[][] lines = defaultLineGenerator.getNext(requestedLines);
        for (Double[] line : lines) {
            for (Double value : line) {
                assertNotNull(value);
                assertTrue(value > 0.0);
                assertTrue(value < maxAmplitude);
            }
        }
    }

    @Test
    public void testValuesMultipliedByMaxAmplitude() {
        long randomSeed = System.currentTimeMillis();
        LineGenerator layer = new LineGenerator(lineLength, 50, 50, 1.0, randomSeed, isCircular);
        Random random = new Random(System.currentTimeMillis());
        double newMaxAmplitude = random.nextDouble() * 100;
        LineGenerator amplifiedLayer = new LineGenerator(lineLength, 50, 50, newMaxAmplitude, randomSeed, isCircular);

        Double[][] lines = layer.getNext(requestedLines);
        Double[][] amplifiedLines = amplifiedLayer.getNext(requestedLines);

        for (Double[] line : lines) {
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
        LineGenerator layer = new LineGenerator(lineLength, 50, 50, 1.0, randomSeed, isCircular);
        LineGenerator sameLayer = new LineGenerator(lineLength, 50, 50, 1.0, randomSeed, isCircular);
        Double[][] nextSegment1 = layer.getNext(requestedLines);
        Double[][] nextSegment2 = sameLayer.getNext(requestedLines);

        assertEquals(nextSegment1.length, nextSegment2.length, 0);
        for (int i = 0; i < nextSegment1.length; i++) {
            assertExpectedArrayEqualsActual(nextSegment1[i], nextSegment2[i], 0.0);
        }
    }

    @Test
    public void testEquals() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace, maxAmplitude, randomSeed, isCircular);
        assertEquals(defaultLineGenerator, otherGenerator);
        assertEquals(defaultLineGenerator.hashCode(), otherGenerator.hashCode());
    }

    @Test
    public void testNotEqualsNotSameLineLength() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength + 10, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace, maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameInterpolationPointsAlongLine() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine + 5,
                        defaultInterpolationPointsAlongNoiseSpace, maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameInterpolationPointsAlongNoiseSpace() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace + 8, maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameMaxAmplitude() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace, maxAmplitude * 2, randomSeed, isCircular);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameRandomSeed() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace, maxAmplitude, randomSeed + 1, isCircular);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameCircularity(){
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace, maxAmplitude, randomSeed, !isCircular);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testToString() {
        ToStringVerifier.forClass(LineGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .withIgnoredFields("lineSegmentLength", "noiseSegmentLength", "randomGenerator", "generated",
                        "randomBounds", "previousBounds")
                .verify();
    }

    @Test
    public void testCircularBounds(){
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongLine,
                        defaultInterpolationPointsAlongNoiseSpace, 1.0, randomSeed, true);
        Double[] line = otherGenerator.getNext(1)[0];
        double firstValue = line[0];
        double secondValue = line[1];
        double mu = secondValue - firstValue;
        double lastValue = line[otherGenerator.getLineLength() - 1];
        double otherMu = firstValue - lastValue;
        assertEquals(mu, otherMu, 0.001);
    }

    //Fake test to visualize data, doesn't assert anything
    @Ignore
    @Test
    public void getNextLines() {
        LineGenerator generator =
                new LineGenerator(lineLength, defaultInterpolationPointsAlongNoiseSpace,
                        defaultInterpolationPointsAlongLine,
                        1.0, randomSeed, true);
        Double[][] lines = generator.getNext(requestedLines);
        Double[][] appended = new Double[requestedLines][lineLength*2];
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
                Double[] newValues = generator.getNext(1)[0];
                Double[] appendedNewValues = new Double[lineLength * 2];
                for (int i = 0; i < newValues.length; i++) {
                    appendedNewValues[i] = newValues[i];
                    appendedNewValues[i+lineLength] = newValues[i];
                }
                appended[lines.length - 1] = appendedNewValues;
                image.updateImage(appended);
            }
        }
    }

    @Ignore
    @Test
    public void testAppendCircularLines(){
        LineGenerator layer2D = new LineGenerator(lineLength, 100, defaultInterpolationPointsAlongNoiseSpace, 1.0,
                System.currentTimeMillis(), true);
        Double[][] lines = layer2D.getNext(1);
        List<Double> values = new ArrayList<>();
        Double[] line = lines[0];
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
        while(true);
    }

    @Ignore
    @Test
    public void testMorphingLine() {
        LineGenerator layer2D = new LineGenerator(lineLength, 100, defaultInterpolationPointsAlongNoiseSpace, 1.0,
                System.currentTimeMillis(), true);
        Double[][] lines = layer2D.getNext(1);
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
                EventQueue.invokeLater(() -> chart.updateDataSeries(dataSeries -> {
                    for (int i = 0; i < lines[0].length; i++) {
                        dataSeries.updateByIndex(i, lines[0][i]);
                    }
                }, label));
            }
        }
    }

    private static void assertExpectedArrayEqualsActual(Double[] expected, Double[] actual, double delta) {
        assertEquals(expected.length, actual.length, delta);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], delta);
        }
    }
}