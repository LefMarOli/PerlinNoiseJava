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
import org.junit.jupiter.api.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.line.LineNoiseGenerator;
import org.lefmaroli.perlin.line.LineNoiseGeneratorBuilder;
import org.lefmaroli.perlin.point.PointNoiseGenerator;
import org.lefmaroli.perlin.point.PointNoiseGeneratorBuilder;
import org.lefmaroli.perlin.slice.SliceNoiseGenerator;
import org.lefmaroli.perlin.slice.SliceNoiseGeneratorBuilder;

class PerlinNoisePerformanceTest {

  private final Logger logger = LogManager.getLogger(PerlinNoisePerformanceTest.class);

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
  void benchmarkCorePerformance() {
    PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
    testPerformance(
        100000,
        (i) -> perlinNoise.getFor(i * 0.005),
        Duration.ofMillis(80),
        "PerlinNoise core benchmark");
  }

  @Test
  void benchmarkCorePerformance5D() {
    PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
    testPerformance(
        100000,
        (i) -> perlinNoise.getFor(i * 0.005, i * 0.1, i, i * 50.7, i / 3.0),
        Duration.ofMillis(500),
        "PerlinNoise core benchmark");
  }

  @Test
  void benchmarkPointGeneratorPerformance() throws NoiseBuilderException {
    PointNoiseGenerator noiseGenerator =
        new PointNoiseGeneratorBuilder()
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();
    testPerformance(
        100000,
        (i) -> noiseGenerator.getNext(),
        Duration.ofMillis(300),
        "PointGenerator benchmark");
  }

  @Test
  void benchmarkLineGeneratorPerformance() throws NoiseBuilderException {
    LineNoiseGenerator noiseGenerator =
        new LineNoiseGeneratorBuilder(1000)
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withLineStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();
    testPerformance(
        500000,
        (i) -> noiseGenerator.getNext(),
        Duration.ofSeconds(500),
        "LineGenerator benchmark");
  }

  @Test
  void benchmarkSliceGeneratorPerformance() throws NoiseBuilderException {
    SliceNoiseGenerator noiseGenerator =
        new SliceNoiseGeneratorBuilder(100, 100)
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 1000, 2.0))
            .withWidthInterpolationPointGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withHeightInterpolationPointGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();
    testPerformance(
        50, (i) -> noiseGenerator.getNext(), Duration.ofMillis(1000), "SliceGenerator benchmark");
  }
}
