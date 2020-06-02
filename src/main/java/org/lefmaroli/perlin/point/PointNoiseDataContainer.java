package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.data.NoiseDataContainer;

public class PointNoiseDataContainer
        implements NoiseDataContainer<double[], PointNoiseDataContainer, PointNoiseData> {

    private final PointNoiseData[] data;

    PointNoiseDataContainer(PointNoiseData[] data) {
        this.data = data;
    }

    PointNoiseDataContainer(int size) {
        this.data = new PointNoiseData[size];
        for (int i = 0; i < size; i++) {
            this.data[i] = new PointNoiseData();
        }
    }

    @Override
    public PointNoiseData[] getAsArray() {
        return data;
    }

    @Override
    public double[] getAsRawData() {
        double[] toReturn = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            toReturn[i] = data[i].getAsRawData();
        }
        return toReturn;
    }

    @Override
    public void add(PointNoiseDataContainer other) {
        PointNoiseData[] otherAsArray = other.getAsArray();
        for (int i = 0; i < data.length; i++) {
            data[i].add(otherAsArray[i]);
        }
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (PointNoiseData datum : data) {
            datum.normalizeBy(maxValue);
        }
    }
}
