package org.lefmaroli.perlin1d;

import org.junit.Test;
import org.lefmaroli.factorgenerator.FactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;
import org.lefmaroli.randomgrid.RandomGrid1D;

import java.util.List;

import static org.junit.Assert.*;

public class Perlin1DTest {

    @Test
    public void getNext() {
        FactorGenerator distanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);

        Perlin1D perlin1D =
                new Perlin1D(new RandomGrid1D(5, distanceFactor, amplitudeFactor, System.currentTimeMillis()));

        int expected = 500;
        List<Double> next = perlin1D.getNext(expected);
        assertEquals(expected, perlin1D.getCurrentIndex());
        assertEquals(expected, next.size(), 0);

        for (Double value : next) {
            assertNotNull(value);
            assertTrue(value > 0.0 && value < 1.0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNext2() {
        FactorGenerator distanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);

        Perlin1D perlin1D =
                new Perlin1D(new RandomGrid1D(5, distanceFactor, amplitudeFactor, System.currentTimeMillis()));

        perlin1D.getNext(0);
    }

    @Test
    public void getPrevious() {
        FactorGenerator distanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);

        Perlin1D perlin1D =
                new Perlin1D(new RandomGrid1D(5, distanceFactor, amplitudeFactor, System.currentTimeMillis()));

        int requested = 500;
        List<Double> next = perlin1D.getNext(requested);
        List<Double> previous = perlin1D.getPrevious(requested);
        assertEquals(0, perlin1D.getCurrentIndex());
        assertEquals(requested, next.size(), 0);
        assertEquals(requested, previous.size(), 0);

        //Assert same as requested before in reverse order
        for (int i = 0; i < requested; i++) {
            assertEquals(next.get(i), previous.get(requested - 1 - i), 0.0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrevious() {
        FactorGenerator distanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);

        Perlin1D perlin1D =
                new Perlin1D(new RandomGrid1D(5, distanceFactor, amplitudeFactor, System.currentTimeMillis()));

        perlin1D.getPrevious(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetPrevious2() {
        FactorGenerator distanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);

        Perlin1D perlin1D =
                new Perlin1D(new RandomGrid1D(5, distanceFactor, amplitudeFactor, System.currentTimeMillis()));

        int requested = 500;
        List<Double> next = perlin1D.getNext(requested);
        assertEquals(requested, perlin1D.getCurrentIndex());
        assertEquals(requested, next.size(), 0);
        perlin1D.getPrevious(requested + 100);
    }
}