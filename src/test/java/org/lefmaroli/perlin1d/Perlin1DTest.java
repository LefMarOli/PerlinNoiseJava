package org.lefmaroli.perlin1d;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Perlin1DTest {

    @Test
    public void TestCreate() {
        Perlin1D perlin1D = new Perlin1D(6);
        assertEquals(6, perlin1D.getDistance(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestFailCreate() {
        new Perlin1D(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestFailCreate2() {
        new Perlin1D(1);
    }

    @Test
    public void TestGet1Value() {
        Perlin1D perlin1D = new Perlin1D(10);
        perlin1D.getNext();
    }

    @Test
    public void TestGetSeveralValues() {
        Perlin1D perlin1D = new Perlin1D(10);
        int expectedCount = 58;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());
    }

    @Test
    public void TestGetSeveralValues2() {
        Perlin1D perlin1D = new Perlin1D(10);
        int expectedCount = 3000;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());
    }

    @Test
    public void TestGetSeveralValues3() {
        Perlin1D perlin1D = new Perlin1D(10);
        int expectedCount = 3567;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());
    }
}