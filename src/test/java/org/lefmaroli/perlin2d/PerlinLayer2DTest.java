package org.lefmaroli.perlin2d;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;

import java.util.Random;

import static org.junit.Assert.*;

public class PerlinLayer2DTest {

    private final int lineLength = 500;
    private final int requestedLines = 700;
    private PerlinLayer2D noiseLayer;

    @Before
    public void setup(){
        noiseLayer = new PerlinLayer2D(lineLength, 200, 1.0, System.currentTimeMillis());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidLineLength(){
        new PerlinLayer2D(-5, 200, 1.0, System.currentTimeMillis());
    }

    @Test
    public void getNextLinesCorrectSize() {
        Double[][] lines = noiseLayer.getNextLines(requestedLines);
        assertEquals(requestedLines, lines.length, 0);
        for (Double[] line : lines) {
            assertEquals(lineLength, line.length, 0);
        }
    }

    @Test
    public void testValuesBounded() {
        Double[][] lines = noiseLayer.getNextLines(requestedLines);
        for (Double[] line : lines) {
            for (Double value : line) {
                assertNotNull(value);
                assertTrue(value > 0.0);
                assertTrue(value < 1.0);
            }
        }
    }

    @Test
    public void testValuesMultipliedByAmplitudeFactor() {
        long randomSeed = System.currentTimeMillis();
        PerlinLayer2D layer = new PerlinLayer2D(lineLength, 50, 1.0, randomSeed);
        Random random = new Random(System.currentTimeMillis());
        double amplitudeFactor = random.nextDouble() * 100;
        PerlinLayer2D amplifiedLayer = new PerlinLayer2D(lineLength, 50, amplitudeFactor, randomSeed);

        Double[][] lines = layer.getNextLines(requestedLines);
        Double[][] amplifiedLines = amplifiedLayer.getNextLines(requestedLines);

        for (Double[] line : lines) {
            for (int j = 0; j < line.length; j++) {
                line[j] = line[j] * amplitudeFactor;
            }
        }

        for (int i = 0; i < lines.length; i++) {
            assertExpectedArrayEqualsActual(lines[i], amplifiedLines[i], 1e-18);
        }
    }

    @Test
    public void testCreateSame() {
        long randomSeed = System.currentTimeMillis();
        PerlinLayer2D layer = new PerlinLayer2D(lineLength, 50, 1.0, randomSeed);
        PerlinLayer2D sameLayer = new PerlinLayer2D(lineLength, 50, 1.0, randomSeed);
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

    //Fake test to visualize data, doesn't assert anything
    @Ignore
    @Test
    public void getNextSlices() {
        Double[][] lines = noiseLayer.getNextLines(requestedLines);
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(lines, 5);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                lines[lines.length - 1] = noiseLayer.getNextLines(1)[0];
                image.updateImage(lines);
            }
        }
    }
}