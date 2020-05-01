package org.lefmaroli.randomgrid;

import junit.framework.TestCase;
import org.junit.Test;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomGrid1DTest {

    @Test
    public void testGetNext() {
        RandomGrid1D grid1D = new RandomGrid1D(9, new MultiplierFactorGenerator(2048, 0.5),
                new MultiplierFactorGenerator(1.0, 1.0 / 1.8), System.currentTimeMillis());

        int expectedCount = 75;
        Vector<Double> nextSegment = grid1D.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.size(), 0);

        for (Double value : nextSegment) {
            assertTrue(value <= 1.0);
            assertTrue(value >= 0.0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate(){
        new RandomGrid1D(0, new MultiplierFactorGenerator(2048, 0.5),
                new MultiplierFactorGenerator(1.0, 1.0 / 1.8), System.currentTimeMillis());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate2(){
        new RandomGrid1D(-8, new MultiplierFactorGenerator(2048, 0.5),
                new MultiplierFactorGenerator(1.0, 1.0 / 1.8), System.currentTimeMillis());
    }

    @Test
    public void testNumLayersGenerated(){
        int expectedNumLayers = 9;
        RandomGrid1D grid1D = new RandomGrid1D(expectedNumLayers, new MultiplierFactorGenerator(2048, 0.5),
                new MultiplierFactorGenerator(1.0, 1.0 / 1.8), System.currentTimeMillis());
        assertEquals(expectedNumLayers, grid1D.getNumberOfLayers(), 0);
    }

    @Test
    public void testNumLayersGenerated2(){
        int expectedNumLayers = 9;
        RandomGrid1D grid1D = new RandomGrid1D(expectedNumLayers, new MultiplierFactorGenerator(32, 0.5),
                new MultiplierFactorGenerator(1.0, 1.0 / 1.8), System.currentTimeMillis());
        assertEquals(6, grid1D.getNumberOfLayers(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNext2(){
        RandomGrid1D grid1D = new RandomGrid1D(9, new MultiplierFactorGenerator(2048, 0.5),
                new MultiplierFactorGenerator(1.0, 1.0 / 1.8), System.currentTimeMillis());
        grid1D.getNext(-6);
    }


}