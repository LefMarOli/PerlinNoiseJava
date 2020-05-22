package org.lefmaroli.perlin.line;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultiLayerLineGeneratorTest {

    private MultiLayerLineGenerator defaultGenerator;
    private List<NoiseLineGenerator> layers;
    private static final double maxAmplitude = 1.75;
    private static final int defaultLineLength = 125;

    @Before
    public void setup() {
        layers = new ArrayList<>(3);
        layers.add(new LineGenerator(defaultLineLength, 2048, 2048, 1.0, System.currentTimeMillis()));
        layers.add(new LineGenerator(defaultLineLength, 1024, 1024, 0.5, System.currentTimeMillis()));
        layers.add(new LineGenerator(defaultLineLength, 512, 512, 0.25, System.currentTimeMillis()));
        defaultGenerator = new MultiLayerLineGenerator(layers);
    }

    @Test
    public void testDimension() {
        assertEquals(2, defaultGenerator.getDimensions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNoLayers() {
        new MultiLayerLineGenerator(new ArrayList<>(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithDifferentLineLengthLayers() {
        List<NoiseLineGenerator> newLayerSet = layers;
        newLayerSet.add(new LineGenerator(defaultLineLength + 5, 256, 256, 0.1225, System.currentTimeMillis()));
        new MultiLayerLineGenerator(newLayerSet);
    }


    @Test
    public void testGetNextCount() {
        int expectedCount = 75;
        Double[][] nextLines = defaultGenerator.getNextLines(expectedCount);
        assertEquals(expectedCount, nextLines.length, 0);
        for (Double[] line : nextLines) {
            assertEquals(defaultLineLength, line.length, 0);
        }
    }

    @Test
    public void testGetNextBoundedValues() {
        Double[][] lines = defaultGenerator.getNextLines(100);
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
        defaultGenerator.getNextLines(-6);
    }

    @Test
    public void testEquals() {
        MultiLayerLineGenerator sameGenerator = new MultiLayerLineGenerator(layers);
        assertEquals(defaultGenerator, sameGenerator);
        assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
    }

    @Test
    public void testNotEquals() {
        List<NoiseLineGenerator> otherLayers = layers;
        otherLayers.add(new LineGenerator(defaultLineLength, 8, 8, 0.1, 5L));
        MultiLayerLineGenerator otherGenerator = new MultiLayerLineGenerator(otherLayers);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testLineLength() {
        assertEquals(defaultLineLength, defaultGenerator.getLineLength());
    }

    @Test
    public void testToString() {
        ToStringVerifier.forClass(MultiLayerLineGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .verify();
    }

}