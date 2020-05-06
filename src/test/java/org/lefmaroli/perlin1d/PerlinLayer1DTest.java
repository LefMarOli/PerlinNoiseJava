package org.lefmaroli.perlin1d;

import org.junit.Test;

import java.util.Random;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PerlinLayer1DTest {

    @Test
    public void testGetNextSegmentCount() {
        PerlinLayer1D layer = new PerlinLayer1D(50, 1.0, 0L);
        int expectedCount = 75;
        Vector<Double> nextSegment = layer.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.size(), 0);
    }

    @Test
    public void testValuesBounded() {
        PerlinLayer1D layer = new PerlinLayer1D(50, 1.0, 0L);
        int count = 10000;
        Vector<Double> nextSegment = layer.getNext(count);
        for (Double value : nextSegment) {
            assertTrue(value < 1.0);
            assertTrue(value > 0.0);
        }
    }

    @Test
    public void testValuesMultipliedByFactor(){
        PerlinLayer1D layer = new PerlinLayer1D(50, 1.0, 0L);
        Random random = new Random(System.currentTimeMillis());
        double amplitudeFactor = random.nextDouble() * 100;
        PerlinLayer1D amplifiedLayer = new PerlinLayer1D(50, amplitudeFactor, 0L);

        int count = 10000;
        Vector<Double> values = layer.getNext(count);
        Vector<Double> actualAmplifiedValues = amplifiedLayer.getNext(count);

        Vector<Double> expectedAmplifiedValues = new Vector<>(values);
        for (int i = 0; i < expectedAmplifiedValues.size(); i++) {
            expectedAmplifiedValues.set(i, expectedAmplifiedValues.get(i) * amplitudeFactor);
        }
        assertExpectedVectorEqualsActual(expectedAmplifiedValues, actualAmplifiedValues, 1e-18);
    }

    @Test
    public void testCreateSame(){
        PerlinLayer1D layer = new PerlinLayer1D(50, 1.0, 0L);
        PerlinLayer1D sameLayer = new PerlinLayer1D(50, 1.0, 0L);
        int expectedCount = 75;
        Vector<Double> nextSegment1 = layer.getNext(expectedCount);
        Vector<Double> nextSegment2 = sameLayer.getNext(expectedCount);
        assertExpectedVectorEqualsActual(nextSegment1, nextSegment2, 0.0);
    }

    public void assertExpectedVectorEqualsActual(Vector<Double> expected, Vector<Double> actual, double delta){
        assertEquals(expected.size(), actual.size(), delta);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i), delta);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        PerlinLayer1D layer = new PerlinLayer1D(-5, 1.0, 0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCount() {
        PerlinLayer1D layer = new PerlinLayer1D(5, 1.0, 0L);
        layer.getNext(-5);
    }
}