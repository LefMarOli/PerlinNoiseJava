package org.lefmaroli.perlin;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

class AbstractNoiseGeneratorBuilderTest {

  @Test
  void testBuilderPattern() {
    MockNoiseBuilder noisePointBuilder = new MockNoiseBuilder();
    Assertions.assertNotNull(noisePointBuilder.withRandomSeed(0L));
    Assertions.assertNotNull(noisePointBuilder.withNumberOfLayers(5));
    Assertions.assertNotNull(noisePointBuilder.withAmplitudeGenerator(new DoubleGenerator(1, 1.0)));
    Assertions.assertNotNull(noisePointBuilder.withNoiseStepSizeGenerator(new DoubleGenerator(1, 1.0)));
  }

  @Test
  void testCreateWithNoLayers() {
    MockNoiseBuilder mockNoiseBuilder = new MockNoiseBuilder();
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> mockNoiseBuilder.withNumberOfLayers(0));
  }

  @Test
  void testCreateWithNegativeNumberOfLayers() {
    MockNoiseBuilder mockNoiseBuilder = new MockNoiseBuilder();
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> mockNoiseBuilder.withNumberOfLayers(-8));
  }

  @Test
  void testCreateSingleLayer() throws NoiseBuilderException {
    INoiseGenerator<Double> built = new MockNoiseBuilder().withNumberOfLayers(1).build();
    Assertions.assertTrue(built instanceof MockNoiseGeneratorLayer);
  }

  @Test
  void testCreateSingleLayerWithNoStepSize() {
    MockNoiseBuilder mockNoiseBuilder =
        new MockNoiseBuilder()
            .withNumberOfLayers(1)
            .withNoiseStepSizeGenerator(new DoubleGenerator(0, 500));
    Assertions.assertThrows(NoiseBuilderException.class, mockNoiseBuilder::build);
  }

  @Test
  void testWrongImplementationOfBuilderClass() {
    int dimensions = 5;
    DoubleGenerator stepSizeGenerator = new DoubleGenerator(1, 2.0);
    int wrongDimensions = dimensions + 1;
    WrongSubClassImplementationMock wrongSubClassImplementationMock =
        new WrongSubClassImplementationMock(dimensions);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            wrongSubClassImplementationMock.setStepSizeGeneratorForDimension(
                wrongDimensions, stepSizeGenerator));
  }

  private static class MockNoiseGenerator implements INoiseGenerator<Double> {

    @Override
    public boolean equals(Object other) {
      return false;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public int getDimensions() {
      return 0;
    }

    @Override
    public Double getNext() {
      return 0.0;
    }

    @Override
    public double getMaxAmplitude() {
      return 0.0;
    }
  }

  private static class MockNoiseGeneratorLayer extends MockNoiseGenerator {}

  private static class MockNoiseBuilder
      extends NoiseBuilder<Double, MockNoiseGenerator, MockNoiseBuilder> {

    MockNoiseBuilder() {
      super(5);
    }

    @Override
    protected MockNoiseBuilder self() {
      return this;
    }

    @Override
    protected MockNoiseGenerator buildSingleNoiseLayer(
        List<Double> interpolationPoints, double layerAmplitude, long randomSeed) {
      return new MockNoiseGeneratorLayer();
    }

    @Override
    protected MockNoiseGenerator buildMultipleNoiseLayer(List<MockNoiseGenerator> layers) {
      return new MockNoiseGenerator();
    }
  }

  private static class WrongSubClassImplementationMock
      extends NoiseBuilder<Double, MockNoiseGenerator, WrongSubClassImplementationMock> {

    public WrongSubClassImplementationMock(int dimensions) {
      super(dimensions);
    }

    @Override
    protected WrongSubClassImplementationMock self() {
      return this;
    }

    @Override
    protected MockNoiseGenerator buildSingleNoiseLayer(
        List<Double> interpolationPoints, double layerAmplitude, long randomSeed) {
      return null;
    }

    @Override
    protected MockNoiseGenerator buildMultipleNoiseLayer(List<MockNoiseGenerator> layers) {
      return null;
    }
  }
}
