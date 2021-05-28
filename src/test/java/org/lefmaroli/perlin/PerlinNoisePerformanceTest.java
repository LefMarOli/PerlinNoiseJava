package org.lefmaroli.perlin;

import static org.awaitility.Awaitility.waitAtMost;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.generators.LayeredGeneratorBuilderException;
import org.lefmaroli.perlin.generators.LayeredLineGenerator;
import org.lefmaroli.perlin.generators.LayeredLineGeneratorBuilder;
import org.lefmaroli.perlin.generators.LayeredSliceGenerator;
import org.lefmaroli.perlin.generators.LayeredSliceGeneratorBuilder;
import org.lefmaroli.perlin.generators.LayeredPointGenerator;
import org.lefmaroli.perlin.generators.LayeredPointGeneratorBuilder;

class PerlinNoisePerformanceTest {

  private final Logger logger = LogManager.getLogger(PerlinNoisePerformanceTest.class);

  private long testPerformance(
      int numIterations, Consumer<Integer> c, Duration maxTestDuration, String testTitle) {
    ExecutorService service = Executors.newSingleThreadExecutor();
    AtomicBoolean isDone = new AtomicBoolean(false);
    AtomicLong meanDuration = new AtomicLong();
    service.submit(
        () -> {
          long start, duration = 0;
          for (int i = 0; i < numIterations; i++) {
            start = System.nanoTime();
            c.accept(i);
            duration += System.nanoTime() - start;
            if (Thread.interrupted()) {
              return;
            }
          }
          duration /= numIterations;
          meanDuration.set(duration);
          if (duration > 1E6) {
            logger.info(testTitle + " mean duration " + duration / 1E6 + "ms");
          } else {
            logger.info(testTitle + " mean duration " + duration + "ns");
          }
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
    return meanDuration.get();
  }

  @Test
  void benchmarkCorePerformance() {
    PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
    long meanDuration =
        testPerformance(
            100000,
            (i) -> perlinNoise.getFor(i * 0.005),
            Duration.ofMillis(200),
            "PerlinNoise core benchmark");
    Assertions.assertTrue(meanDuration < 2000);
  }

  @Test
  void benchmarkCorePerformance5D() {
    PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
    long meanDuration =
        testPerformance(
            100000,
            (i) -> perlinNoise.getFor(i * 0.005, i * 0.1, i, i * 50.7, i / 3.0),
            Duration.ofMillis(500),
            "PerlinNoise core benchmark");
    Assertions.assertTrue(meanDuration < 5000);
  }

  @Test
  void benchmarkPointGeneratorPerformance() throws LayeredGeneratorBuilderException {
    LayeredPointGenerator noiseGenerator =
        new LayeredPointGeneratorBuilder()
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudes(new DoubleGenerator(1.0, 0.85))
            .build();
    long meanDuration =
        testPerformance(
            100000,
            (i) -> noiseGenerator.getNext(),
            Duration.ofMillis(300),
            "PointGenerator benchmark");
    Assertions.assertTrue(meanDuration < 2000);
  }

  @Test
  void benchmarkLineGeneratorPerformance() throws LayeredGeneratorBuilderException {
    LayeredLineGenerator noiseGenerator =
        new LayeredLineGeneratorBuilder(1000)
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withLineStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudes(new DoubleGenerator(1.0, 0.85))
            .build();
    long meanDuration =
        testPerformance(
            500,
            (i) -> noiseGenerator.getNext(),
            Duration.ofMillis(500),
            "LineGenerator benchmark");
    Assertions.assertTrue(meanDuration < 500000);
  }

  @Test
  void benchmarkSliceGeneratorPerformance() throws LayeredGeneratorBuilderException {
    LayeredSliceGenerator noiseGenerator =
        new LayeredSliceGeneratorBuilder(100, 100)
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizes(new DoubleGenerator(1.0 / 1000, 2.0))
            .withWidthStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withHeightStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudes(new DoubleGenerator(1.0, 0.85))
            .build();
    long meanDuration =
        testPerformance(
            50,
            (i) -> noiseGenerator.getNext(),
            Duration.ofMillis(1000),
            "SliceGenerator benchmark");
    meanDuration /= 1E6;
    Assertions.assertTrue(meanDuration < 7);
  }

  @Test
  void benchmarkSliceGeneratorPerformanceWithExecutor() throws LayeredGeneratorBuilderException {
    ExecutorService service = Executors.newFixedThreadPool(3);
    long optimized;
    int numIterations = 50;
    LayeredSliceGeneratorBuilder builder =
        new LayeredSliceGeneratorBuilder(200, 200)
            .withNumberOfLayers(3)
            .withRandomSeed(0L)
            .withNoiseStepSizes(new DoubleGenerator(1.0 / 1000, 2.0))
            .withWidthStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withHeightStepSizes(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudes(new DoubleGenerator(1.0, 0.85))
            .withForkJoinPool(null)
            .withLayerExecutorService(service);
    try {
      LayeredSliceGenerator noiseGenerator = builder.build();
      optimized =
          testPerformance(
              numIterations,
              (i) -> noiseGenerator.getNext(),
              Duration.ofMillis(10000),
              "Optimized sliceGenerator benchmark (with ExecutorService)");
    } finally {
      service.shutdown();
    }
    builder.withLayerExecutorService(null);
    LayeredSliceGenerator noiseGenerator = builder.build();
    long unoptimized =
        testPerformance(
            numIterations,
            (i) -> noiseGenerator.getNext(),
            Duration.ofMillis(40000),
            "Unoptimized sliceGenerator benchmark (without ExecutorService)");

    long diff = unoptimized - optimized;
    Assertions.assertTrue(diff > 0, "Unoptimized code ran faster than optimized code");
    Assertions.assertTrue(
        diff < unoptimized * 2 / 3,
        "Optimized code ran slower than " + "two-thirds of the unoptimized process");
  }
}
