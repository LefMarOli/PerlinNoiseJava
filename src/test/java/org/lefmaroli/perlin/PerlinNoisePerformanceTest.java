package org.lefmaroli.perlin;

import static org.awaitility.Awaitility.waitAtMost;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.line.LineNoiseGenerator;
import org.lefmaroli.perlin.line.LineNoiseGeneratorBuilder;
import org.lefmaroli.perlin.point.PointNoiseGenerator;
import org.lefmaroli.perlin.point.PointNoiseGeneratorBuilder;
import org.lefmaroli.perlin.slice.SliceNoiseGenerator;
import org.lefmaroli.perlin.slice.SliceNoiseGeneratorBuilder;

public class PerlinNoisePerformanceTest {

  Logger logger = LogManager.getLogger(PerlinNoisePerformanceTest.class);

  private void testPerformance(
      int numIterations, Consumer<Integer> c, Duration maxTestDuration, String testTitle) {
    ExecutorService service = Executors.newSingleThreadExecutor();
    AtomicBoolean isDone = new AtomicBoolean(false);
    service.submit(
        () -> {
          long start = System.currentTimeMillis();
          for (int i = 0; i < numIterations; i++) {
            c.accept(i);
            if (Thread.interrupted()) {
              return;
            }
          }
          logger.info(testTitle + " done in " + (System.currentTimeMillis() - start) + "ms");
          isDone.set(true);
        });
    try {
      Awaitility.setDefaultPollInterval(Duration.ofMillis(10));
      waitAtMost(maxTestDuration).until(isDone::get);
    } finally {
      service.shutdownNow();
      try {
        boolean areTasksDone = service.awaitTermination(10, TimeUnit.SECONDS);
        if (!areTasksDone) {
          logger.error("Tasks were not completed within the delay");
        } else logger.debug("ExecutorService shutdown complete");
      } catch (InterruptedException e) {
        logger.error("Awaiting task termination was interrupted");
        Thread.currentThread().interrupt();
      }
    }
  }

  @Test
  public void benchmarkCorePerformance() {
    PerlinNoise perlinNoise = new PerlinNoise(1, System.currentTimeMillis());
    testPerformance(
        1000000,
        (i) -> perlinNoise.getFor(i * 0.005),
        Duration.ofMillis(80),
        "PerlinNoise core benchmark");
  }

  @Test
  public void benchmarkPointGeneratorPerformance() throws NoiseBuilderException {
    PointNoiseGenerator noiseGenerator =
        new PointNoiseGeneratorBuilder()
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();
    testPerformance(
        500, (i) -> noiseGenerator.getNext(), Duration.ofMillis(170), "PointGenerator benchmark");
  }

  @Test
  public void benchmarkLineGeneratorPerformance() throws NoiseBuilderException {
    LineNoiseGenerator noiseGenerator =
        new LineNoiseGeneratorBuilder(1000)
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withLineStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();
    testPerformance(
        50000, (i) -> noiseGenerator.getNext(), Duration.ofMillis(700), "LineGenerator benchmark");
  }

  @Test
  public void benchmarkSliceGeneratorPerformance() throws NoiseBuilderException {
    SliceNoiseGenerator noiseGenerator =
        new SliceNoiseGeneratorBuilder(400, 400)
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 1000, 2.0))
            .withWidthInterpolationPointGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withHeightInterpolationPointGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();
    testPerformance(
        5, (i) -> noiseGenerator.getNext(), Duration.ofMillis(2200), "SliceGenerator benchmark");
  }
}
