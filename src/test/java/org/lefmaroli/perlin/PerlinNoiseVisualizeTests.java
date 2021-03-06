package org.lefmaroli.perlin;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.testutils.AssertUtils;
import org.lefmaroli.testutils.ScheduledUpdater;

class PerlinNoiseVisualizeTests {

  private final long randomSeed = System.currentTimeMillis();

  @Test
  void test2D() { // NOSONAR
    PerlinNoise perlinNoise = new PerlinNoise(randomSeed);
    int size = 500;
    double[] values = new double[size];
    double stepSizeX = 0.01;
    double stepSizeY = 0.01;
    for (int i = 0; i < size; i++) {
      values[i] = perlinNoise.getFor(0 * stepSizeX, i * stepSizeY);
    }
    final AtomicInteger currentXIndex = new AtomicInteger(0);

    AtomicReference<LineChart> chart = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    String label = "line";
    if (isDisplaySupported) {
      LineChart c = new LineChart("Test", "length", "values");
      c.addEquidistantDataSeries(values, label);
      c.setVisible();
      c.setYAxisRange(0.0, 1.0);
      chart.set(c);
    }

    int[] placeholder = new int[values.length - 1];

    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              int xIndex = currentXIndex.incrementAndGet();
              for (int i = 0; i < size; i++) {
                double newValue = perlinNoise.getFor(xIndex * stepSizeX, i * stepSizeY);
                values[i] = newValue;
              }
              if (isDisplaySupported) {
                SwingUtilities.invokeLater(
                    () ->
                        chart
                            .get()
                            .updateDataSeries(
                                dataSeries -> {
                                  for (int i = 0; i < values.length; i++) {
                                    dataSeries.updateByIndex(i, values[i]);
                                  }
                                },
                                label));
              }
              AssertUtils.valuesContinuousInArray(values, placeholder);
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

  @Test
  void test3D() { // NOSONAR
    PerlinNoise perlinNoise = new PerlinNoise(randomSeed);
    final int size = 200;
    double[][] values = new double[size][size];
    double stepSize = 0.01;
    double stepSizeY = 0.01;
    double stepSizeZ = 0.05;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        values[i][j] = perlinNoise.getFor(i * stepSize, j * stepSizeY, 0);
      }
    }

    AtomicReference<SimpleGrayScaleImage> im = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    if (isDisplaySupported) {
      im.set(new SimpleGrayScaleImage(values, 5));
      im.get().setVisible();
    }

    int[] placeholder = new int[size - 1];

    AtomicInteger currentZIndex = new AtomicInteger(0);
    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              int zIndex;
              zIndex = currentZIndex.incrementAndGet();
              for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                  values[i][j] =
                      perlinNoise.getFor(i * stepSize, j * stepSizeY, zIndex * stepSizeZ);
                }
              }
              if (Thread.interrupted()) {
                return;
              }
              if (isDisplaySupported) {
                im.get().updateImage(values);
              }
              double[] column = new double[values[0].length];
              for (double[] row : values) {
                AssertUtils.valuesContinuousInArray(row, placeholder);
                System.arraycopy(row, 0, column, 0, row.length);
                AssertUtils.valuesContinuousInArray(column, placeholder);
              }
            },
            30,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) {
            im.get().dispose();
          }
        });
  }
}
