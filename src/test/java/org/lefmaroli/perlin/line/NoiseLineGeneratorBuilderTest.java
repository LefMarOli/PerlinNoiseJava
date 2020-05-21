package org.lefmaroli.perlin.line;

import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
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

    //Fake test to visualize data, doesn't assert anything
    @Ignore
    @Test
    public void getNextLines() throws NoiseBuilderException {
        NoiseLineGenerator generator = new NoiseLineGeneratorBuilder(lineLength).build();
        int requestedLines = 500;
        Double[][] lines = generator.getNextLines(requestedLines);
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(lines, 5);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                lines[lines.length - 1] = generator.getNextLines(1)[0];
                image.updateImage(lines);
            }
        }
    }

}