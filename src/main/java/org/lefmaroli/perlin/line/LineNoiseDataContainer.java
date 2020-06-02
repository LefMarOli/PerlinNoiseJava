package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.data.NoiseDataContainer;

public class LineNoiseDataContainer
        implements NoiseDataContainer<double[][], LineNoiseDataContainer, LineNoiseData> {

    private final LineNoiseData[] data;

    public LineNoiseDataContainer(LineNoiseData[] data) {
        this.data = data;
    }

    public LineNoiseDataContainer(int size, int lineLength) {
        this.data = new LineNoiseData[size];
        for (int i = 0; i < size; i++) {
            data[i] = new LineNoiseData(lineLength);
        }
    }

    @Override
    public LineNoiseData[] getAsArray() {
        return data;
    }

    @Override
    public double[][] getAsRawData() {
        double[][] results = new double[data.length][data[0].getLineLength()];
        for (int i = 0; i < data.length; i++) {
            results[i] = data[i].getAsRawData();
        }
        return results;
    }

    @Override
    public void add(LineNoiseDataContainer other) {
        LineNoiseData[] otherAsArray = other.getAsArray();
        for (int i = 0; i < data.length; i++) {
            data[i].add(otherAsArray[i]);
        }
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (LineNoiseData datum : data) {
            datum.normalizeBy(maxValue);
        }
    }
}
