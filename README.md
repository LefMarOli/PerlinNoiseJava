# PerlinNoiseJava

[![SonarCloud Analysis](https://github.com/LefMarOli/PerlinNoiseJava/workflows/SonarCloud%20Analysis/badge.svg)](https://sonarcloud.io/dashboard?id=LefMarOli_PerlinNoiseJava)

Java implementation of Perlin Noise algorithm as described
in [this](https://www.youtube.com/watch?v=MJ3bvCkHJtE) resource.

## About this project

This project is an implementation of the popular Perlin noise algorithm described
by [Ken Perlin](https://en.wikipedia.org/wiki/Perlin_noise).

This projects also features generators to populate grids of noise in 1 and 2 dimensions that can be
updated over time. These generators offer the option of spatial circularity over the grid's borders.
For example, connecting both ends of a line generated this way will preserve noise continuity over
the new-made junction. For a 2D grid, tiling the grid with itself would also preserve noise
continuity over the tile's junction.

## Usage

### Raw Perlin

The perlin noise core algorithm is accessible through the PerlinNoise class. After creating an
instance of the class with a random seed, it can be used as follows:

    PerlinNoise perlinNoise = new PerlinNoise(seed);
    double noiseValue = perlinNoise.getFor(coordinates);

where `coordinates` represents a single dimension array of doubles, of length up to 5.

### PerlinNoise Generators

The noise generators aim to automatize the generation of noise following a single or double
dimensional array. The generators are accessible through their respective builders like such:

    LayeredSliceGeneratorBuilder builder = new LayeredSliceGeneratorBuilder(width, height);
    LayeredSliceGenerator generator = builder.build();
    double[][] noiseValues = generator.getNext();

The number of layers is set using the builder:

    builder.withNumberOfLayers(n);

Step sizes in spatial and time dimensions are set with the builder's methods:

    builder.setTimeStepSizes(timeStepSizes);
    builder.setWidthStepSizes(widthStepSizes);
    builder.setHeightStepSizes(heightStepSizes);

where stepSizes is an `Iterable<Double>` having at least n (number of layers) values.

The amplitude of each layer is set with the builder's method:

    builder.withAmplitudes(amplitudes);

which is also an `Iterable<Double>`, following the same principle as the `setXStepSizes()` methods.

Spatial circularity option is set with the builder:

    builder.withCircularBounds(true);

### Parallelization

Parallelization of the noise generation is possible using two paradigms:

* the ForkJoinPool Java framework, to split line or slice generation into smaller fragments;
* the ExecutorService framework, to launch generation of each layer simultaneously.

These optimizations are accessible using the builder's methods:

    builder.withForkJoinPool(pool);
    builder.withLayerExecutorService(executorService);
