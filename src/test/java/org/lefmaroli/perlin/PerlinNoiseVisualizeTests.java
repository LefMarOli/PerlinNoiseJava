package org.lefmaroli.perlin;

import java.awt.EventQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;
import org.lefmaroli.utils.ScheduledUpdater;

@Ignore("Tests skipped, visual assessment only")
public class PerlinNoiseVisualizeTests {

  private final long randomSeed = System.currentTimeMillis();

  @Test
  public void test2D() {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    int size = 500;
    double[] values = new double[size];
    double stepSizeX = 0.01;
    double stepSizeY = 0.055;
    for (int i = 0; i < size; i++) {
      values[i] = perlinNoise.getFor(0 * stepSizeX, i * stepSizeY);
    }
    final AtomicInteger currentXIndex = new AtomicInteger(0);
    LineChart chart = new LineChart("Test", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(values, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);

    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              int xIndex = currentXIndex.incrementAndGet();
              for (int i = 0; i < size; i++) {
                double newValue = perlinNoise.getFor(xIndex * stepSizeX, i * stepSizeY);
                values[i] = newValue;
              }
              EventQueue.invokeLater(
                  () ->
                      chart.updateDataSeries(
                          dataSeries -> {
                            for (int i = 0; i < values.length; i++) {
                              dataSeries.updateByIndex(i, values[i]);
                            }
                          },
                          label));
            },
            30,
            TimeUnit.MILLISECONDS,
            15,
            TimeUnit.SECONDS);
    completed.thenRun(chart::dispose);
  }

  @Test
  public void test3D() {
    PerlinNoise perlinNoise = new PerlinNoise(3, randomSeed);
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
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(values, 5);
    image.setVisible();
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
              image.updateImage(values);
            },
            30,
            TimeUnit.MILLISECONDS,
            15,
            TimeUnit.SECONDS);
    completed.thenRun(image::dispose);
  }
}
