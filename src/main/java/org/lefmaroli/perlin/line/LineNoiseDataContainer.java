package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.NoiseDataContainer;

import java.util.ArrayList;
import java.util.List;

public class LineNoiseDataContainer implements NoiseDataContainer<Double[][], Double[], LineNoiseDataContainer, LineNoiseData> {

    private final List<LineNoiseData> data;

    public LineNoiseDataContainer(List<LineNoiseData> data) {
        this.data = data;
    }

    public LineNoiseDataContainer(int size, int lineLength) {
        this.data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(new LineNoiseData(lineLength));
        }
    }

    @Override
    public List<LineNoiseData> getAsList() {
        return data;
    }

    @Override
    public Double[][] getAsRawData() {
        Double[][] results = new Double[data.size()][data.get(0).getLineLength()];
        for (int i = 0; i < data.size(); i++) {
            results[i] = data.get(i).getAsRawData();
        }
        return results;
    }

    @Override
    public void add(LineNoiseDataContainer other) {
        List<LineNoiseData> otherAsList = other.getAsList();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).add(otherAsList.get(i));
        }
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (LineNoiseData datum : data) {
            datum.normalizeBy(maxValue);
        }
    }
}
