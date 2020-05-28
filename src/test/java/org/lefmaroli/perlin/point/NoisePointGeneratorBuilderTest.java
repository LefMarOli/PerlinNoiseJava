package org.lefmaroli.perlin.point;


import org.junit.Test;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoisePointGeneratorBuilderTest {

    @Test
    public void testBuildNoisePointNotNull() throws NoiseBuilderException {
        PointNoiseGenerator noisePointGenerator = new NoisePointGeneratorBuilder().build();
        assertNotNull(noisePointGenerator);
    }

    @Test
    public void testBuildNoisePointCreateSameFromSameBuilder() throws NoiseBuilderException {
        NoisePointGeneratorBuilder noisePointGeneratorBuilder = new NoisePointGeneratorBuilder();
        PointNoiseGenerator noisePointGenerator = noisePointGeneratorBuilder.build();
        PointNoiseGenerator noisePointGenerator2 = noisePointGeneratorBuilder.build();
        assertNotNull(noisePointGenerator2);
        assertEquals(noisePointGenerator, noisePointGenerator2);
    }

}