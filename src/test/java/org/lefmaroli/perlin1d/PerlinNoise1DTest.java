package org.lefmaroli.perlin1d;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class PerlinNoise1DTest {

    private PerlinNoise1D noiseLayer;
    private final int interpolationPoints = 50;
    private final int expectedCount = 500;
    private final long randomSeed = System.currentTimeMillis();

    @Before
    public void setup() {
        noiseLayer = new PerlinNoise1D(interpolationPoints, 1.0, randomSeed);
    }

    @Test
    public void testGetNextSegmentCount() {
        Double[] nextSegment = noiseLayer.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.length, 0);
    }

    @Test
    public void testValuesBounded() {
        Double[] nextSegment = noiseLayer.getNext(expectedCount);
        for (Double value : nextSegment) {
            assertNotNull(value);
            assertTrue(value < 1.0);
            assertTrue(value > 0.0);
        }
    }

    @Test
    public void testValuesMultipliedByFactor() {
        Random random = new Random(System.currentTimeMillis());
        double amplitudeFactor = random.nextDouble() * 100;
        PerlinNoise1D amplifiedLayer = new PerlinNoise1D(interpolationPoints, amplitudeFactor, randomSeed);

        Double[] values = noiseLayer.getNext(expectedCount);
        Double[] actualAmplifiedValues = amplifiedLayer.getNext(expectedCount);

        Double[] expectedAmplifiedValues = new Double[values.length];
        for (int i = 0; i < values.length; i++) {
            expectedAmplifiedValues[i] = values[i] * amplitudeFactor;
        }
        assertExpectedArrayEqualsActual(expectedAmplifiedValues, actualAmplifiedValues, 1e-18);
    }

    @Test
    public void testCreateSame() {
        PerlinNoise1D sameLayer = new PerlinNoise1D(50, 1.0, randomSeed);
        Double[] nextSegment1 = noiseLayer.getNext(expectedCount);
        Double[] nextSegment2 = sameLayer.getNext(expectedCount);
        assertExpectedArrayEqualsActual(nextSegment1, nextSegment2, 0.0);
    }

    private void assertExpectedArrayEqualsActual(Double[] expected, Double[] actual, double delta) {
        assertEquals(expected.length, actual.length, delta);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], delta);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        new PerlinNoise1D(-5, 1.0, 0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCount() {
        PerlinNoise1D layer = new PerlinNoise1D(5, 1.0, 0L);
        layer.getNext(-5);
    }
}