package org.lefmaroli.perlin.line;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.point.PointNoiseGenerator;
import org.lefmaroli.perlin.point.PointNoiseGeneratorBuilder;
import org.lefmaroli.perlin.point.PointNoiseGeneratorBuilderTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LineNoiseGeneratorBuilderTest {

    Logger logger = LogManager.getLogger(LineNoiseGeneratorBuilderTest.class);


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
    public void testBuilderPatternForCircularity() {
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
        Double[][] lines = generator.getNext(requestedLines).getAsRawData();
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(lines, 5);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                System.arraycopy(lines, 1, lines, 0, lines.length - 1);
                lines[lines.length - 1] = generator.getNext(1).getAsRawData()[0];
                image.updateImage(lines);
            }
        }
    }

    @Ignore
    @Test
    public void benchmarkPerformance() throws NoiseBuilderException {
        LineNoiseGenerator noiseGenerator = new LineNoiseGeneratorBuilder(1000)
                .withNumberOfLayers(10)
                .withRandomSeed(0L)
                .withNoiseInterpolationPointCountGenerator(new IntegerGenerator(50, 2.0))
                .withLineInterpolationPointCountGenerator(new IntegerGenerator(50, 2.0))
                .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
                .build();

        double duration = 0.0;
        int numberOfIterations = 5;
        int count = 5000;
        for (int i = 0; i < numberOfIterations; i++) {
            long start = System.currentTimeMillis();
            noiseGenerator.getNext(count);
            long end = System.currentTimeMillis();
            duration += end - start;
            logger.info("Finished iteration " + i);
        }
        duration /= numberOfIterations;

        logger.info("Mean duration: " + duration);
        //No optimisation = 10seconds
    }

}