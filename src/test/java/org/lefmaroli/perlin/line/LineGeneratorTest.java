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
import java.util.Random;

import static org.junit.Assert.*;

public class LineGeneratorTest {

    private final int lineLength = 500;
    private final int requestedLines = 700;
    private LineGenerator defaultLineGenerator;
    private double maxAmplitude = 5.0;
    private long randomSeed = System.currentTimeMillis();
    private int defaultInterpolationPoints = 200;

    @Before
    public void setup() {
        defaultLineGenerator = new LineGenerator(lineLength, defaultInterpolationPoints, maxAmplitude, randomSeed);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidLineLength() {
        new LineGenerator(-5, 200, 1.0, System.currentTimeMillis());
    }

    @Test
    public void getNextLinesCorrectSize() {
        Double[][] lines = defaultLineGenerator.getNextLines(requestedLines);
        assertEquals(requestedLines, lines.length, 0);
        for (Double[] line : lines) {
            assertEquals(lineLength, line.length, 0);
        }
    }

    @Test
    public void testGetLineLength(){
        assertEquals(lineLength, defaultLineGenerator.getLineLength());
    }

    @Test
    public void testGetMaxAmplitude(){
        assertEquals(maxAmplitude, defaultLineGenerator.getMaxAmplitude(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextLinesInvalidCount(){
        defaultLineGenerator.getNextLines(-5);
    }

    @Test
    public void testValuesBounded() {
        Double[][] lines = defaultLineGenerator.getNextLines(requestedLines);
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
        LineGenerator layer = new LineGenerator(lineLength, 50, 1.0, randomSeed);
        Random random = new Random(System.currentTimeMillis());
        double newMaxAmplitude = random.nextDouble() * 100;
        LineGenerator amplifiedLayer = new LineGenerator(lineLength, 50, newMaxAmplitude, randomSeed);

        Double[][] lines = layer.getNextLines(requestedLines);
        Double[][] amplifiedLines = amplifiedLayer.getNextLines(requestedLines);

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
        LineGenerator layer = new LineGenerator(lineLength, 50, 1.0, randomSeed);
        LineGenerator sameLayer = new LineGenerator(lineLength, 50, 1.0, randomSeed);
        Double[][] nextSegment1 = layer.getNextLines(requestedLines);
        Double[][] nextSegment2 = sameLayer.getNextLines(requestedLines);

        assertEquals(nextSegment1.length, nextSegment2.length, 0);
        for (int i = 0; i < nextSegment1.length; i++) {
            assertExpectedArrayEqualsActual(nextSegment1[i], nextSegment2[i], 0.0);
        }
    }

    private void assertExpectedArrayEqualsActual(Double[] expected, Double[] actual, double delta) {
        assertEquals(expected.length, actual.length, delta);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], delta);
        }
    }

    @Test
    public void testEquals() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPoints, maxAmplitude, randomSeed);
        assertEquals(defaultLineGenerator, otherGenerator);
        assertEquals(defaultLineGenerator.hashCode(), otherGenerator.hashCode());
    }

    @Test
    public void testNotEqualsNotSameLineLength() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength + 10, defaultInterpolationPoints, maxAmplitude, randomSeed);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameInterpolationPoints() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPoints + 5, maxAmplitude, randomSeed);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameMaxAmplitude() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPoints, maxAmplitude * 2, randomSeed);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameRandomSeed() {
        LineGenerator otherGenerator =
                new LineGenerator(lineLength, defaultInterpolationPoints, maxAmplitude, randomSeed +1);
        assertNotEquals(defaultLineGenerator, otherGenerator);
    }

    @Test
    public void testToString(){
        ToStringVerifier.forClass(LineGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .withIgnoredFields("segmentLength", "randomGenerator", "generated", "randomBounds", "previousBounds")
                .verify();
    }

    //Fake test to visualize data, doesn't assert anything
    @Ignore
    @Test
    public void getNextLines() {
        Double[][] lines = defaultLineGenerator.getNextLines(requestedLines);
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(lines, 5);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                lines[lines.length - 1] = defaultLineGenerator.getNextLines(1)[0];
                image.updateImage(lines);
            }
        }
    }

    @Ignore
    @Test
    public void testMorphingLine() {
        LineGenerator layer2D = new LineGenerator(lineLength, 100, 1.0, System.currentTimeMillis());
        Double[][] lines = layer2D.getNextLines(1);
        LineChart chart = new LineChart("Morphing line", "length", "values");
        String label = "line";
        chart.addEquidistantDataSeries(lines[0], label);
        chart.setVisible();
        chart.setYAxisRange(0.0, 1.0);

        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 15) {
                previousTime = System.currentTimeMillis();
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                lines[lines.length - 1] = layer2D.getNextLines(1)[0];
                EventQueue.invokeLater(() -> {
                    chart.updateDataSeries(dataSeries -> {
                        for (int i = 0; i < lines[0].length; i++) {
                            dataSeries.updateByIndex(i, lines[0][i]);
                        }
                    }, label);
                });
            }
        }
    }
}