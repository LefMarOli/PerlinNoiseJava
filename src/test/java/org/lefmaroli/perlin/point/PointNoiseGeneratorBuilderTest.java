package org.lefmaroli.perlin.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.utils.AssertUtils;
import org.lefmaroli.utils.ScheduledUpdater;

public class PointNoiseGeneratorBuilderTest {

  @Test
  public void testBuildNoisePointNotNull() throws NoiseBuilderException {
    PointNoiseGenerator noisePointGenerator = new PointNoiseGeneratorBuilder().build();
    assertNotNull(noisePointGenerator);
  }

  @Test
  public void testBuildNoisePointCreateSameFromSameBuilder() throws NoiseBuilderException {
    PointNoiseGeneratorBuilder pointNoiseGeneratorBuilder = new PointNoiseGeneratorBuilder();
    PointNoiseGenerator noisePointGenerator = pointNoiseGeneratorBuilder.build();
    PointNoiseGenerator noisePointGenerator2 = pointNoiseGeneratorBuilder.build();
    assertNotNull(noisePointGenerator2);
    assertEquals(noisePointGenerator, noisePointGenerator2);
  }

  @Test
  public void testSmoothVisuals() throws NoiseBuilderException { // NOSONAR
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(1.0 / 200, 2.0);
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    PointNoiseGenerator generator =
        new PointNoiseGeneratorBuilder()
            .withNoiseStepSizeGenerator(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudeGenerator(amplitudeGenerator)
            .build();
    int requestedPoints = 200;
    final double[] line = new double[requestedPoints];
    for (int i = 0; i < requestedPoints; i++) {
      line[i] = generator.getNext();
    }

    AtomicReference<LineChart> chart = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    String label = "line";
    if (isDisplaySupported) {
      LineChart c = new LineChart("Test", "length", "values");
      c.addEquidistantDataSeries(line, label);
      c.setVisible();
      c.setYAxisRange(0.0, 1.0);
      chart.set(c);
    }

    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              System.arraycopy(line, 1, line, 0, requestedPoints - 1);
              if (Thread.interrupted()) {
                return;
              }
              line[requestedPoints - 1] = generator.getNext();
              if (isDisplaySupported) {
                SwingUtilities.invokeLater(
                    () ->
                        chart
                            .get()
                            .updateDataSeries(
                                dataSeries -> {
                                  for (int i = 0; i < line.length; i++) {
                                    dataSeries.updateByIndex(i, line[i]);
                                  }
                                },
                                label));
              }
              try {
                AssertUtils.valuesContinuousInArray(line);
              } catch (AssertionError e) {
                LogManager.getLogger(this.getClass())
                    .error("Error with line smoothness for point generator " + generator, e);
                throw e;
              }
            },
            30,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) chart.get().dispose();
        });
  }
}
