package org.lefmaroli.perlin.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

public class LineNoiseGeneratorBuilderTest {

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

  @Test
  public void testSmoothVisuals() throws NoiseBuilderException {  //NOSONAR
    double lineStepSizeInitialValue = 1.0 / 50;
    DoubleGenerator lineStepSizeGenerator =
        new DoubleGenerator(lineStepSizeInitialValue, 1.0 / 0.9);
    double noiseStepSizeInitialValue = 1.0 / 80;
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(noiseStepSizeInitialValue, 2.0);
    int lineLength = 200;
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    LineNoiseGenerator generator =
        new LineNoiseGeneratorBuilder(lineLength)
            .withLineStepSizeGenerator(lineStepSizeGenerator)
            .withNoiseStepSizeGenerator(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudeGenerator(amplitudeGenerator)
            .build();
    int requestedLines = 200;
    final double[][] image = new double[requestedLines][lineLength];
    for (int i = 0; i < requestedLines; i++) {
      double[] nextLine = generator.getNext();
      System.arraycopy(nextLine, 0, image[i], 0, lineLength);
    }
    SimpleGrayScaleImage im = new SimpleGrayScaleImage(image, 5);
    im.setVisible();

    CompletableFuture<Void> completed = ScheduledUpdater.updateAtRateForDuration(
        () -> {
          for (int i = 0; i < requestedLines - 1; i++) {
            System.arraycopy(image[i + 1], 0, image[i], 0, lineLength);
          }
          if (Thread.interrupted()) {
            return;
          }
          double[] nextLine = generator.getNext();
          System.arraycopy(nextLine, 0, image[requestedLines - 1], 0, lineLength);
          im.updateImage(image);
          try {
            AssertUtils.valuesContinuousInArray(nextLine);
            double[] row = new double[image.length];
            for(int i = 0; i < lineLength; i++) {
              System.arraycopy(image[i], 0, row, 0, image.length);
              AssertUtils.valuesContinuousInArray(row);
            }
          } catch (AssertionError e) {
            LogManager.getLogger(this.getClass())
                .error("Error with line smoothness for line generator " + generator, e);
            throw e;
          }
        },
        30,
        TimeUnit.MILLISECONDS,
        5,
        TimeUnit.SECONDS);
    completed.thenRun(im::dispose);
  }
}
