package org.lefmaroli.perlin.point;

import org.junit.Before;
import org.junit.Test;
import org.lefmaroli.factorgenerator.FactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

import static org.junit.Assert.*;

public class NoisePointNavigatorTest {

    private NoisePointNavigator defaultNoisePointNavigator;

    @Before
    public void setup() throws NoiseBuilderException {
        FactorGenerator defaultDistanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator defaultAmplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);
        NoisePointGenerator defaultNoisePointGenerator = new NoisePointGeneratorBuilder()
                .withNumberOfLayers(5)
                .withDistanceFactorGenerator(defaultDistanceFactor)
                .withAmplitudeFactorGenerator(defaultAmplitudeFactor)
                .build();
        defaultNoisePointNavigator = new NoisePointNavigator(defaultNoisePointGenerator);
    }

    @Test
    public void getNextTestCount() {
        int expected = 500;
        List<Double> next = defaultNoisePointNavigator.getNext(expected);
        assertEquals(expected, defaultNoisePointNavigator.getCurrentIndex());
        assertEquals(expected, next.size(), 0);
    }

    @Test
    public void getNextSingle(){
        defaultNoisePointNavigator.getNext();
        assertEquals(1, defaultNoisePointNavigator.getCurrentIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNextIllegalArgument() {
        defaultNoisePointNavigator.getNext(0);
    }

    @Test
    public void getPreviousTestCount() {
        int requested = 500;
        List<Double> next = defaultNoisePointNavigator.getNext(requested);
        List<Double> previous = defaultNoisePointNavigator.getPrevious(requested);
        assertEquals(0, defaultNoisePointNavigator.getCurrentIndex());
        assertEquals(requested, next.size(), 0);
        assertEquals(requested, previous.size(), 0);
    }

    @Test
    public void getPreviousSingleTest(){
        defaultNoisePointNavigator.getNext();
        defaultNoisePointNavigator.getPrevious();
        assertEquals(0, defaultNoisePointNavigator.getCurrentIndex());
    }

    @Test
    public void getPreviousReverseOrderTest(){
        int requested = 500;
        List<Double> next = defaultNoisePointNavigator.getNext(requested);
        List<Double> previous = defaultNoisePointNavigator.getPrevious(requested);
        for (int i = 0; i < requested; i++) {
            assertEquals(next.get(i), previous.get(requested - 1 - i), 0.0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPreviousIllegalCount() {
        defaultNoisePointNavigator.getPrevious(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetPreviousOutOfBounds() {
        int requested = 500;
        defaultNoisePointNavigator.getNext(requested);
        defaultNoisePointNavigator.getPrevious(requested + 100);
    }
}