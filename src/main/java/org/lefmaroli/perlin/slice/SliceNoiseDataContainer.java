package org.lefmaroli.perlin.slice;

import org.lefmaroli.perlin.data.NoiseDataContainer;

public class SliceNoiseDataContainer
    implements NoiseDataContainer<double[][][], SliceNoiseDataContainer, SliceNoiseData> {

  private final SliceNoiseData[] data;

  public SliceNoiseDataContainer(SliceNoiseData[] data) {
    this.data = data;
  }

  public SliceNoiseDataContainer(int count, int sliceWidth, int sliceHeight) {
    this.data = new SliceNoiseData[count];
    for (int i = 0; i < count; i++) {
      this.data[i] = new SliceNoiseData(sliceWidth, sliceHeight);
    }
  }

  @Override
  public SliceNoiseData[] getAsArray() {
    return data;
  }

  @Override
  public void add(SliceNoiseDataContainer other) {
    SliceNoiseData[] otherAsList = other.getAsArray();
    for (int i = 0; i < data.length; i++) {
      data[i].add(otherAsList[i]);
    }
  }

  @Override
  public void normalizeBy(double maxValue) {
    for (SliceNoiseData datum : data) {
      datum.normalizeBy(maxValue);
    }
  }

  @Override
  public double[][][] getAsRawData() {
    double[][][] results =
        new double[data.length][data[0].getSliceWidth()][data[0].getSliceHeight()];
    for (int i = 0; i < data.length; i++) {
      results[i] = data[i].getAsRawData();
    }
    return results;
  }
}
