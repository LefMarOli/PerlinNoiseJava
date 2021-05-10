package org.lefmaroli.perlin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class AbstractNoiseGeneratorBuilderTest {

  @Test
  public void testBuilderPattern() {
    MockNoiseBuilder noisePointBuilder = new MockNoiseBuilder();
    assertNotNull(noisePointBuilder.withRandomSeed(0L));
    assertNotNull(noisePointBuilder.withNumberOfLayers(5));
    assertNotNull(noisePointBuilder.withAmplitudeGenerator(new DoubleGenerator(1, 1.0)));
    assertNotNull(
        noisePointBuilder.withNoiseStepSizeGenerator(new DoubleGenerator(1, 1.0)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNoLayers() {
    new MockNoiseBuilder().withNumberOfLayers(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNegativeNumberOfLayers() {
    new MockNoiseBuilder().withNumberOfLayers(-8);
  }

  @Test
  public void testCreateSingleLayer() throws NoiseBuilderException {
    INoiseGenerator<Double> built = new MockNoiseBuilder().withNumberOfLayers(1).build();
    assertTrue(built instanceof MockNoiseGeneratorLayer);
  }

  @Test(expected = NoiseBuilderException.class)
  public void testCreateSingleLayerWithNoStepSize() throws NoiseBuilderException {
    new MockNoiseBuilder()
        .withNumberOfLayers(1)
        .withNoiseStepSizeGenerator(new DoubleGenerator(0, 500))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongImplementationOfBuilderClass() {
    int dimensions = 5;
    new WrongSubClassImplementationMock(dimensions)
        .setStepSizeGeneratorForDimension(
            dimensions + 1, new DoubleGenerator(1, 2.0));
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
