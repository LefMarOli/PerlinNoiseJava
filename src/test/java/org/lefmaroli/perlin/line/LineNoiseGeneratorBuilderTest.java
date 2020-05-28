package org.lefmaroli.perlin.line;

import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LineNoiseGeneratorBuilderTest {

    private int lineLength = 800;

    @Test
    public void testBuildNoiseLineNotNull() throws NoiseBuilderException {
        LineNoiseGenerator noiseLineGenerator = new LineNoiseGeneratorBuilder(lineLength).build();
        assertNotNull(noiseLineGenerator);
    }

    @Test
    public void testBuildNoiseLineCreateSameFromSameBuilder() throws NoiseBuilderException {
        LineNoiseGeneratorBuilder lineNoiseGeneratorBuilder = new LineNoiseGeneratorBuilder(lineLength);
        LineNoiseGenerator noisePointGenerator = lineNoiseGeneratorBuilder.build();
        LineNoiseGenerator noisePointGenerator2 = lineNoiseGeneratorBuilder.build();
        assertNotNull(noisePointGenerator2);
        assertEquals(noisePointGenerator, noisePointGenerator2);
    }

    @Test
    public void testBuilderPatternForSubclass() {
        new LineNoiseGeneratorBuilder(lineLength)
                .withLineInterpolationPointCountGenerator(new IntegerGenerator(5, 0.5));
    }

    @Test
    public void testBuilderPatternForCircularity(){
        new LineNoiseGeneratorBuilder(lineLength)
                .withCircularBounds();
    }

    //Fake test to visualize data, doesn't assert anything
    @Ignore
    @Test
    public void getNextLines() throws NoiseBuilderException {
        IntegerGenerator lineInterpolationPointCountGenerator = new IntegerGenerator(128, 0.9);
        IntegerGenerator noiseInterpolationPointCountGenerator = new IntegerGenerator(128, 0.5);
        LineNoiseGenerator generator = new LineNoiseGeneratorBuilder(lineLength)
                .withLineInterpolationPointCountGenerator(lineInterpolationPointCountGenerator)
                .withNoiseInterpolationPointCountGenerator(noiseInterpolationPointCountGenerator)
                .withNumberOfLayers(4)
                .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.95))
                .build();
        int requestedLines = 800;
        Double[][] lines = generator.getNext(requestedLines);
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(lines, 5);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                lines[lines.length - 1] = generator.getNext(1)[0];
                image.updateImage(lines);
            }
        }
    }

}