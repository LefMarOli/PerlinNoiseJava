package org.lefmaroli.perlin1d;

import org.junit.Before;
import org.junit.Test;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PerlinGrid1DTest {

    private PerlinGrid1D.Builder defaultBuilder;
    private final int defaultNumberOfLayers = 9;

    @Before
    public void setup() {
        defaultBuilder = new PerlinGrid1D.Builder()
                .withNumberOfLayers(defaultNumberOfLayers)
                .withDistanceFactorGenerator(new MultiplierFactorGenerator(2048, 0.5))
                .withAmplitudeFactorGenerator(new MultiplierFactorGenerator(1.0, 1.0 / 1.8));
    }

    @Test
    public void testGetNextCount() {
        PerlinGrid1D grid1D = defaultBuilder.build();
        int expectedCount = 75;
        Vector<Double> nextSegment = grid1D.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.size(), 0);
    }

    @Test
    public void testGetNextBoundedValues() {
        PerlinGrid1D grid1D = defaultBuilder.build();
        Vector<Double> nextSegment = grid1D.getNext(10000);
        for (Double value : nextSegment) {
            assertTrue(value <= 1.0);
            assertTrue(value >= 0.0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNoLayers() {
        defaultBuilder.withNumberOfLayers(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNegativeNumberOfLayers() {
        defaultBuilder.withNumberOfLayers(-8);
    }

    @Test
    public void testNumLayersGenerated() {
        PerlinGrid1D grid1D = defaultBuilder.build();
        assertEquals(defaultNumberOfLayers, grid1D.getNumberOfLayers(), 0);
    }

    @Test
    public void testNumLayersGenerated2() {
        int numberOfInterpolationPoints = 32;
        double factor = 0.5;
        int expectedNumberOfLayers = 6; //Can't divide 32 more than 5 times by 2.0 and still get an integer
        MultiplierFactorGenerator distanceFactorGenerator =
                new MultiplierFactorGenerator(numberOfInterpolationPoints, factor);
        PerlinGrid1D grid1D = defaultBuilder
                .withDistanceFactorGenerator(distanceFactorGenerator)
                .build();
        assertEquals(expectedNumberOfLayers, grid1D.getNumberOfLayers(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextNegativeCount() {
        PerlinGrid1D grid1D = defaultBuilder.build();
        grid1D.getNext(-6);
    }
}