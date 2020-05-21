package org.lefmaroli.perlin.line;

import org.junit.Test;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoiseLineGeneratorBuilderTest {

    private int lineLength = 508;

    @Test
    public void testBuildNoiseLineNotNull() throws NoiseBuilderException {
        NoiseLineGenerator noiseLineGenerator = new NoiseLineGeneratorBuilder(lineLength).build();
        assertNotNull(noiseLineGenerator);
    }

    @Test
    public void testBuildNoiseLineCreateSameFromSameBuilder() throws NoiseBuilderException {
        NoiseLineGeneratorBuilder noiseLineGeneratorBuilder = new NoiseLineGeneratorBuilder(lineLength);
        NoiseLineGenerator noisePointGenerator = noiseLineGeneratorBuilder.build();
        NoiseLineGenerator noisePointGenerator2 = noiseLineGeneratorBuilder.build();
        assertNotNull(noisePointGenerator2);
        assertEquals(noisePointGenerator, noisePointGenerator2);
    }

}