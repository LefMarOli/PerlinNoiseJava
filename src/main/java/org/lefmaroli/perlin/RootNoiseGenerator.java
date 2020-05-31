package org.lefmaroli.perlin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class RootNoiseGenerator<ContainerDataType, DataType, BoundsType> {

    private final Queue<DataType> generated = new LinkedList<>();
    private BoundsType previousBounds;
    private final int noiseInterpolationPointsCount;

    public RootNoiseGenerator(int noiseInterpolationPointsCount){
        if (noiseInterpolationPointsCount < 0) {
            throw new IllegalArgumentException("Noise interpolation points must be greater than 0");
        }
        this.noiseInterpolationPointsCount = noiseInterpolationPointsCount;
    }

    public int getNoiseInterpolationPointsCount(){
        return noiseInterpolationPointsCount;
    }

    protected abstract List<DataType> generateNextSegment(BoundsType previous, BoundsType current);

    protected abstract ContainerDataType getInContainer(List<DataType> data);

    protected abstract BoundsType getNewBounds();

    public ContainerDataType getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        return computeAtLeast(count);
    }

    private ContainerDataType computeAtLeast(int count) {
        if(previousBounds == null){
            previousBounds = getNewBounds();
        }
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
                BoundsType newBounds = getNewBounds();
                results.addAll(generateNextSegment(previousBounds, newBounds));
                newCount -= noiseInterpolationPointsCount;
                previousBounds = newBounds;
            }
            BoundsType newBounds = getNewBounds();
            List<DataType> lastSegment = generateNextSegment(previousBounds, newBounds);
            previousBounds = newBounds;
            results.addAll(lastSegment.subList(0, newCount));
            generated.addAll(lastSegment.subList(newCount, lastSegment.size()));
        }
        return getInContainer(results);
    }
}
