package org.lefmaroli.perlin1d;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class Perlin1DTest {

    @Test
    public void TestCreate() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(6).build();
        assertEquals(8, perlin1D.getDistance(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestFailCreate() {
        new Perlin1D.Builder().withDistance(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestFailCreate2() {
        new Perlin1D.Builder().withDistance(1).build();
    }

    @Test
    public void TestGet1Value() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        Double next = perlin1D.getNext();
        assertNotNull(next);
        assertTrue(next > 0.0 && next < 1.0);
    }

    @Test
    public void TestGetSeveralValues() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        int expectedCount = 58;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());

        for (Double value : nextValues) {
            assertNotNull(value);
            assertTrue(value > 0.0 && value < 1.0);
        }
    }

    @Test
    public void TestGetSeveralValues2() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        int expectedCount = 3000;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());

        for (Double value : nextValues) {
            assertNotNull(value);
            assertTrue(value > 0.0 && value < 1.0);
        }
    }

    @Test
    public void TestGetSeveralValues3() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        int expectedCount = 3567;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());

        for (Double value : nextValues) {
            assertNotNull(value);
            assertTrue(value > 0.0 && value < 1.0);
        }
    }



}