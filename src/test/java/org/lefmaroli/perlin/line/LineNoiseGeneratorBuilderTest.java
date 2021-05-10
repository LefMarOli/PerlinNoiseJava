package org.lefmaroli.perlin.line;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class LineNoiseGeneratorBuilderTest {

  Logger logger = LogManager.getLogger(LineNoiseGeneratorBuilderTest.class);

  private static final int lineLength = 800;

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

  @Ignore("Skipped, only used to visualize results")
  @Test
  public void getNextLines() throws NoiseBuilderException {
    DoubleGenerator lineInterpolationPointCountGenerator =
        new DoubleGenerator(1.0 / 500, 1.0 / 0.9);
    DoubleGenerator noiseInterpolationPointCountGenerator = new DoubleGenerator(1.0 / 80, 2.0);
    int lineLength = 200;
    LineNoiseGenerator generator =
        new LineNoiseGeneratorBuilder(lineLength)
            .withLineStepSizeGenerator(lineInterpolationPointCountGenerator)
            .withNoiseStepSizeGenerator(noiseInterpolationPointCountGenerator)
            .withNumberOfLayers(4)
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.95))
            .build();
    int requestedLines = 200;
    final double[][] image = new double[requestedLines][lineLength];
    for (int i = 0; i < requestedLines; i++) {
      double[] nextLine = generator.getNext();
      System.arraycopy(nextLine, 0, image[i], 0, lineLength);
    }
    SimpleGrayScaleImage im = new SimpleGrayScaleImage(image, 5);
    im.setVisible();

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> scheduledFuture =
        ses.scheduleAtFixedRate(
            () -> {
              for (int i = 0; i < requestedLines - 1; i++) {
                for (int j = 0; j < lineLength; j++) {
                  image[i][j] = image[i + 1][j];
                }
              }
              double[] nextLine = generator.getNext();
              System.arraycopy(nextLine, 0, image[requestedLines - 1], 0, lineLength);
              im.updateImage(image);
            },
            5,
            30,
            TimeUnit.MILLISECONDS);

    int testDurationInMs = 15;
    ses.schedule(
        () -> {
          scheduledFuture.cancel(true);
          ses.shutdown();
        },
        testDurationInMs,
        TimeUnit.SECONDS);

    waitAtMost(testDurationInMs + 1, TimeUnit.SECONDS).until(ses::isShutdown);
  }

  @Ignore
  @Test
  public void benchmarkPerformance() throws NoiseBuilderException {
    LineNoiseGenerator noiseGenerator =
        new LineNoiseGeneratorBuilder(1000)
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withLineStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();

    double duration = 0.0;
    int numberOfIterations = 5 * 5000;
    for (int i = 0; i < numberOfIterations; i++) {
      long start = System.currentTimeMillis();
      noiseGenerator.getNext();
      long end = System.currentTimeMillis();
      duration += end - start;
      logger.info("Finished iteration " + i);
    }
    duration /= numberOfIterations;

    logger.info("Mean duration: " + duration);
    // Current performance is 482.6
  }
}
