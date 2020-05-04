package org.lefmaroli.perlin1d;

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

public class PerlinLayer1DTest {

    @Test
    public void testGetNextSegment() {
        PerlinLayer1D layer = new PerlinLayer1D(50, 1.0, 0L);
        int expectedCount = 75;
        Vector<Double> nextSegment = layer.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.size(), 0);
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