package org.lefmaroli.perlin.point;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Test;
import org.lefmaroli.perlin.line.MultiLayerLineGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultiLayerPointGeneratorTest {

    private MultiLayerPointGenerator defaultGenerator;
    private List<PointGenerator> layers;
    private double maxAmplitude = 1.75;

    @Before
    public void setup() {
        layers = new ArrayList<>(3);
        layers.add(new PointGenerator(2048, 1.0, System.currentTimeMillis()));
        layers.add(new PointGenerator(1024, 0.5, System.currentTimeMillis()));
        layers.add(new PointGenerator(512, 0.25, System.currentTimeMillis()));
        defaultGenerator = new MultiLayerPointGenerator(layers);
    }

    @Test
    public void testDimension() {
        assertEquals(1, defaultGenerator.getDimensions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNoLayers(){
        new MultiLayerPointGenerator(new ArrayList<>(5));
    }

    @Test
    public void testGetNextCount() {
        int expectedCount = 75;
        Double[] nextSegment = defaultGenerator.getNextPoints(expectedCount);
        assertEquals(expectedCount, nextSegment.length, 0);
    }

    @Test
    public void testGetNextBoundedValues() {
        Double[] nextSegment = defaultGenerator.getNextPoints(10000);
        for (Double value : nextSegment) {
            assertTrue(value <= 1.0);
            assertTrue(value >= 0.0);
        }
    }

    @Test
    public void testGetMaxAmplitude(){
        assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
    }

    @Test
    public void testNumLayersGenerated() {
        assertEquals(layers.size(), defaultGenerator.getNumberOfLayers(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextNegativeCount() {
        defaultGenerator.getNextPoints(-6);
    }

    @Test
    public void testEquals() {
        MultiLayerPointGenerator sameGenerator = new MultiLayerPointGenerator(layers);
        assertEquals(defaultGenerator, sameGenerator);
        assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
    }

    @Test
    public void testNotEquals() {
        List<PointGenerator> otherLayers = layers;
        otherLayers.add(new PointGenerator(8, 0.1, 5L));
        MultiLayerPointGenerator otherGenerator = new MultiLayerPointGenerator(otherLayers);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testToString(){
        ToStringVerifier.forClass(MultiLayerPointGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .verify();
    }
}