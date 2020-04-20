package org.lefmaroli.perlin1d;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
        perlin1D.getNext();
    }

    @Test
    public void TestGetSeveralValues() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        int expectedCount = 58;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());
    }

    @Test
    public void TestGetSeveralValues2() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        int expectedCount = 3000;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());
    }

    @Test
    public void TestGetSeveralValues3() {
        Perlin1D perlin1D = new Perlin1D.Builder().withDistance(10).build();
        int expectedCount = 3567;
        List<Double> nextValues = perlin1D.getNext(expectedCount);
        assertEquals(expectedCount, nextValues.size());
    }
}