package org.lefmaroli.perlin.line;

import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoiseLineGeneratorBuilderTest {

    private int lineLength = 800;

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

    @Test
    public void testBuilderPatternForSubclass() {
        new NoiseLineGeneratorBuilder(lineLength)
                .withLineInterpolationPointCountGenerator(new IntegerGenerator(5, 0.5));
    }

    //Fake test to visualize data, doesn't assert anything
    @Ignore
    @Test
    public void getNextLines() throws NoiseBuilderException {
        IntegerGenerator lineDistance = new IntegerGenerator(128, 0.9);
        IntegerGenerator noiseDistance = new IntegerGenerator(128, 0.5);
        NoiseLineGenerator generator = new NoiseLineGeneratorBuilder(lineLength)
                .withLineInterpolationPointCountGenerator(lineDistance)
                .withNoiseDistanceGenerator(noiseDistance)
                .withNumberOfLayers(4)
                .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.95))
                .build();
        int requestedLines = 800;
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