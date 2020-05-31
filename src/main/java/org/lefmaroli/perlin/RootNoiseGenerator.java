package org.lefmaroli.perlin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class RootNoiseGenerator<ContainerDataType, DataType> {

    private final Queue<DataType> generated = new LinkedList<>();

    public abstract int getNoiseInterpolationPointsCount();

    protected abstract List<DataType> generateNextSegment();

    protected abstract ContainerDataType getInContainer(List<DataType> data);

    public ContainerDataType getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        return computeAtLeast(count);
    }

    private ContainerDataType computeAtLeast(int count) {
        List<DataType> results = new ArrayList<>(count);
        if (count < generated.size()) {
            for (int i = 0; i < count; i++) {
                results.add(generated.poll());
            }
        } else {
            for (int i = 0; i < generated.size(); i++) {
                results.add(generated.poll());
            }
            int newCount = count - results.size();
            int noiseInterpolationPointsCount = getNoiseInterpolationPointsCount();
            while (newCount > noiseInterpolationPointsCount) {
                results.addAll(generateNextSegment());
                newCount -= noiseInterpolationPointsCount;
            }
            List<DataType> lastSegment = generateNextSegment();
            results.addAll(lastSegment.subList(0, newCount));
            generated.addAll(lastSegment.subList(newCount, lastSegment.size()));
        }
        return getInContainer(results);
    }
}
