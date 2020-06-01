package org.lefmaroli.perlin.slice;

import org.lefmaroli.perlin.data.NoiseDataContainer;

import java.util.ArrayList;
import java.util.List;

public class SliceNoiseDataContainer
        implements NoiseDataContainer<Double[][][], SliceNoiseDataContainer, SliceNoiseData> {

    private final List<SliceNoiseData> data;

    public SliceNoiseDataContainer(List<SliceNoiseData> data) {
        this.data = data;
    }

    public SliceNoiseDataContainer(int count, int sliceWidth, int sliceHeight){
        this.data = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.data.add(new SliceNoiseData(sliceWidth, sliceHeight));
        }
    }

    @Override
    public List<SliceNoiseData> getAsList() {
        return data;
    }

    @Override
    public void add(SliceNoiseDataContainer other) {
        List<SliceNoiseData> otherAsList = other.getAsList();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).add(otherAsList.get(i));
        }
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (SliceNoiseData datum : data) {
            datum.normalizeBy(maxValue);
        }
    }

    @Override
    public Double[][][] getAsRawData() {
        Double[][][] results = new Double[data.size()][data.get(0).getSliceWidth()][data.get(0).getSliceHeight()];
        for (int i = 0; i < data.size(); i++) {
            results[i] = data.get(i).getAsRawData();
        }
        return results;
    }
}
