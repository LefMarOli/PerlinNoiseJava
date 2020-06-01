package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.data.NoiseData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineNoiseData implements NoiseData<double[], LineNoiseData> {

    private final double[] values;

    public LineNoiseData(double[] values) {
        this.values = values;
    }

    public LineNoiseData(int size) {
        this.values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = 0.0;
        }
    }

    @Override
    public void add(LineNoiseData other) {
        if (other.getLineLength() != getLineLength()) {
            throw new IllegalArgumentException("Line lengths have to be of equal size");
        } else {
            double[] otherValues = other.getAsRawData();
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i] + otherValues[i];
            }
        }
    }

    public int getLineLength() {
        return values.length;
    }

    @Override
    public void normalizeBy(double maxValue) {
        for (int i = 0; i < getLineLength(); i++) {
            values[i] = values[i] / maxValue;
        }
    }

    @Override
    public double[] getAsRawData() {
        return values;
    }
}
