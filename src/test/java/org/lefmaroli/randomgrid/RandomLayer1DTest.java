package org.lefmaroli.randomgrid;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

public class RandomLayer1DTest {

    @Test
    public void testGetNextSegment() {
        RandomLayer1D layer = new RandomLayer1D(50, 1.0, 0L);
        int expectedCount = 75;
        Vector<Double> nextSegment = layer.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.size(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        RandomLayer1D layer = new RandomLayer1D(-5, 1.0, 0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCount() {
        RandomLayer1D layer = new RandomLayer1D(5, 1.0, 0L);
        layer.getNext(-5);
    }
}