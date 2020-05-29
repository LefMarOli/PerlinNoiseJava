package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.data.NoiseDataContainer;

import java.util.ArrayList;
import java.util.List;

public class PointNoiseDataContainer
        implements NoiseDataContainer<Double[], PointNoiseDataContainer, PointNoiseData> {

    private final List<PointNoiseData> data;

    PointNoiseDataContainer(List<PointNoiseData> data) {
        this.data = data;
    }

    PointNoiseDataContainer(int size) {
        this.data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.data.add(new PointNoiseData());
        }
    }

    @Override
    public List<PointNoiseData> getAsList() {
        return data;
    }

    @Override
    public Double[] getAsRawData() {
        Double[] toReturn = new Double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            toReturn[i] = data.get(i).getAsRawData();
        }
        return toReturn;
    }

    @Override
    public void add(PointNoiseDataContainer other) {
        List<PointNoiseData> otherAsList = other.getAsList();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).add(otherAsList.get(i));
        }
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (PointNoiseData datum : data) {
            datum.normalizeBy(maxValue);
        }
    }
}
