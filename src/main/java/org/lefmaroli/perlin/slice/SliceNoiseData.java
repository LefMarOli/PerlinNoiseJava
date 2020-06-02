package org.lefmaroli.perlin.slice;

import org.lefmaroli.perlin.data.NoiseData;
import org.lefmaroli.perlin.line.LineNoiseData;

import java.util.ArrayList;
import java.util.List;

public class SliceNoiseData implements NoiseData<double[][], SliceNoiseData> {

    private final List<LineNoiseData> data;

    public SliceNoiseData(List<LineNoiseData> data) {
        this.data = data;
    }

    public SliceNoiseData(int sliceWidth, int sliceHeight) {
        this.data = new ArrayList<>(sliceWidth);
        for (int i = 0; i < sliceWidth; i++) {
            this.data.add(new LineNoiseData(sliceHeight));
        }
    }

    public int getSliceWidth() {
        return data.size();
    }

    public int getSliceHeight() {
        return data.get(0).getLineLength();
    }

    @Override
    public void add(SliceNoiseData other) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).add(other.data.get(i));
        }
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (LineNoiseData datum : data) {
            datum.normalizeBy(maxValue);
        }
    }

    @Override
    public double[][] getAsRawData() {
        double[][] results = new double[getSliceWidth()][getSliceHeight()];
        for (int i = 0; i < data.size(); i++) {
            results[i] = data.get(i).getAsRawData();
        }
        return results;
    }
}
