package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.NoiseData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineNoiseData implements NoiseData<Double[], LineNoiseData> {

    private final List<Double> values;

    public LineNoiseData(List<Double> values) {
        this.values = values;
    }

    public LineNoiseData(int size) {
        this.values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            values.add(0.0);
        }
    }

    @Override
    public void add(LineNoiseData other) {
        if (other.getLineLength() != getLineLength()) {
            throw new IllegalArgumentException("Line lengths have to be of equal size");
        } else {
            int index = 0;
            Iterator<Double> iterator = other.getIterator();
            while (iterator.hasNext()) {
                Double otherValueAtIndex = iterator.next();
                values.set(index, values.get(index) + otherValueAtIndex);
                index++;
            }
        }
    }

    public Iterator<Double> getIterator() {
        return values.iterator();
    }

    public int getLineLength() {
        return values.size();
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (int i = 0; i < getLineLength(); i++) {
            values.set(i, values.get(i) / maxValue);
        }
    }

    @Override
    public Double[] getAsRawData() {
        Double[] results = new Double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            results[i] = values.get(i);
        }
        return results;
    }
}
