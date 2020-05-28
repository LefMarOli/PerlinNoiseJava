package org.lefmaroli.perlin;

import java.util.List;

public abstract class LayeredNoiseGenerator<RawDataType,
        ReturnType extends NoiseData<RawDataType, ReturnType>,
        NoiseLayer extends INoiseGenerator<RawDataType, ReturnType>>
        implements INoiseGenerator<RawDataType, ReturnType> {

    private final double maxAmplitude;
    private final List<NoiseLayer> layers;

    protected LayeredNoiseGenerator(List<NoiseLayer> layers) {
        if (layers.size() < 1) {
            throw new IllegalArgumentException("Number of layers must at least be 1");
        }
        this.layers = layers;
        double sum = 0.0;
        for (NoiseLayer layer : layers) {
            sum += layer.getMaxAmplitude();
        }
        this.maxAmplitude = sum;
    }

    @Override
    public ReturnType getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        return generateResults(count);
    }

    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    public int getNumberOfLayers() {
        return layers.size();
    }

    protected List<NoiseLayer> getLayers() {
        return layers;
    }

    protected abstract ReturnType initializeResults(int count);

    private ReturnType generateResults(int count) {
        ReturnType results = initializeResults(count);
        for (NoiseLayer layer : layers) {
            ReturnType layerData = layer.getNext(count);
            results.add(layerData);
        }
        results.normalizeBy(maxAmplitude);
        return results;
    }
}
