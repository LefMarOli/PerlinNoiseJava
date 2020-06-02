package org.lefmaroli.perlin.point;

import java.util.Objects;
import org.lefmaroli.perlin.data.NoiseData;

public class PointNoiseData implements NoiseData<Double, PointNoiseData> {

  private double value;

  PointNoiseData(double value) {
    this.value = value;
  }

  PointNoiseData() {
    this.value = 0.0;
  }

  @Override
  public void add(PointNoiseData other) {
    value += other.value;
  }

  @Override
  public void normalizeBy(double maxValue) {
    value /= maxValue;
  }

  @Override
  public Double getAsRawData() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PointNoiseData that = (PointNoiseData) o;
    return Double.compare(that.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "PointNoise{" + "value=" + value + '}';
  }
}
