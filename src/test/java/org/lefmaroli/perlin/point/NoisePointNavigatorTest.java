package org.lefmaroli.perlin.point;

import org.junit.Before;
import org.junit.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NoisePointNavigatorTest {

    private NoisePointNavigator defaultNoisePointNavigator;

    @Before
    public void setup() throws NoiseBuilderException {
        IntegerGenerator defaultNoiseInterpolationPointCountGenerator = new IntegerGenerator(2048, 0.5);
        DoubleGenerator defaultAmplitudeFactorGenerator = new DoubleGenerator(1.0, 1.0 / 1.8);
        PointNoiseGenerator defaultNoisePointGenerator = new PointNoiseGeneratorBuilder()
                .withNumberOfLayers(5)
                .withNoiseInterpolationPointCountGenerator(defaultNoiseInterpolationPointCountGenerator)
                .withAmplitudeGenerator(defaultAmplitudeFactorGenerator)
                .build();
        defaultNoisePointNavigator = new NoisePointNavigator(defaultNoisePointGenerator);
    }

    @Test
    public void getNextTestCount() {
        int expected = 500;
        List<PointNoiseData> next = defaultNoisePointNavigator.getNext(expected);
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
        List<PointNoiseData> next = defaultNoisePointNavigator.getNext(requested);
        List<PointNoiseData> previous = defaultNoisePointNavigator.getPrevious(requested);
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
        List<PointNoiseData> next = defaultNoisePointNavigator.getNext(requested);
        List<PointNoiseData> previous = defaultNoisePointNavigator.getPrevious(requested);
        for (int i = 0; i < requested; i++) {
            assertEquals(next.get(i), previous.get(requested - 1 - i));
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