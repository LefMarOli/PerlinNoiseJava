package org.lefmaroli.perlin.line;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LayeredLineGeneratorTest {

    private LayeredLineGenerator defaultGenerator;
    private List<LineNoiseGenerator> layers;
    private static final double maxAmplitude = 1.75;
    private static final int defaultLineLength = 125;
    private boolean isCircularDefault = false;

    @Before
    public void setup() {
        layers = new ArrayList<>(3);
        layers.add(
                new LineGenerator(defaultLineLength, 2048, 2048, 1.0, System.currentTimeMillis(), isCircularDefault));
        layers.add(
                new LineGenerator(defaultLineLength, 1024, 1024, 0.5, System.currentTimeMillis(), isCircularDefault));
        layers.add(new LineGenerator(defaultLineLength, 512, 512, 0.25, System.currentTimeMillis(), isCircularDefault));
        defaultGenerator = new LayeredLineGenerator(layers);
    }

    @Test
    public void testDimension() {
        assertEquals(2, defaultGenerator.getDimensions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNoLayers() {
        new LayeredLineGenerator(new ArrayList<>(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithDifferentLineLengthLayers() {
        List<LineNoiseGenerator> newLayerSet = layers;
        newLayerSet.add(new LineGenerator(defaultLineLength + 5, 256, 256, 0.1225, System.currentTimeMillis(),
                isCircularDefault));
        new LayeredLineGenerator(newLayerSet);
    }


    @Test
    public void testGetNextCount() {
        int expectedCount = 75;
        Double[][] nextLines = defaultGenerator.getNext(expectedCount);
        assertEquals(expectedCount, nextLines.length, 0);
        for (Double[] line : nextLines) {
            assertEquals(defaultLineLength, line.length, 0);
        }
    }

    @Test
    public void testGetNextBoundedValues() {
        Double[][] lines = defaultGenerator.getNext(100);
        for (Double[] line : lines) {
            for (Double value : line) {
                assertNotNull(value);
                assertTrue(value > 0.0);
                assertTrue(value < maxAmplitude);
            }
        }
    }

    @Test
    public void testGetMaxAmplitude() {
        assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
    }

    @Test
    public void testNumLayersGenerated() {
        assertEquals(layers.size(), defaultGenerator.getNumberOfLayers(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextNegativeCount() {
        defaultGenerator.getNext(-6);
    }

    @Test
    public void testEquals() {
        LayeredLineGenerator sameGenerator = new LayeredLineGenerator(layers);
        assertEquals(defaultGenerator, sameGenerator);
        assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
    }

    @Test
    public void testNotEquals() {
        List<LineNoiseGenerator> otherLayers = layers;
        otherLayers.add(new LineGenerator(defaultLineLength, 8, 8, 0.1, 5L, isCircularDefault));
        LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testLineLength() {
        assertEquals(defaultLineLength, defaultGenerator.getLineLength());
    }

    @Test
    public void testToString() {
        ToStringVerifier.forClass(LayeredLineGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .verify();
    }

    @Test
    public void testNonCircularity() {
        assertFalse(defaultGenerator.isCircular());
    }

    @Test
    public void testMixCircularity() {
        List<LineNoiseGenerator> otherLayers = layers;
        otherLayers.add(new LineGenerator(defaultLineLength, 8, 8, 0.1, 5L, true));
        LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
        assertFalse(otherGenerator.isCircular());
    }

    @Test
    public void testCircular() {
        List<LineNoiseGenerator> otherLayers = new ArrayList<>(3);
        otherLayers.add(new LineGenerator(defaultLineLength, 8, 8, 0.1, 5L, true));
        otherLayers.add(new LineGenerator(defaultLineLength, 16, 16, 0.05, 2L, true));
        otherLayers.add(new LineGenerator(defaultLineLength, 25, 25, 0.005, 1L, true));
        LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
        assertTrue(otherGenerator.isCircular());
    }
}