package org.lefmaroli;

import org.lefmaroli.display.LineChart;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.point.PointNoiseGeneratorBuilder;
import org.lefmaroli.perlin.point.NoisePointNavigator;
import org.lefmaroli.perlin.point.PointNoiseGenerator;

import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws NoiseBuilderException {

        IntegerGenerator interpolationPointCountGenerator = new IntegerGenerator(2048, 0.5);
        DoubleGenerator amplitudeFactorGenerator = new DoubleGenerator(1.0, 1.0 / 1.8);
        PointNoiseGenerator grid1D = new PointNoiseGeneratorBuilder()
                .withNumberOfLayers(16)
                .withNoiseInterpolationPointCountGenerator(interpolationPointCountGenerator)
                .withAmplitudeGenerator(amplitudeFactorGenerator)
                .build();

        NoisePointNavigator noisePointNavigator = new NoisePointNavigator(grid1D);

        LineChart lineChart = new LineChart("Perlin1D", "Sequence #", "Value");
        String dataLabel = "DataSet";
        lineChart.addEquidistantDataSeries(noisePointNavigator.getNext(50), dataLabel);
        lineChart.setVisible();

        boolean activateUpdate = true;
        if (activateUpdate) {
            long start = System.currentTimeMillis();
            while (true) {
                long current = System.currentTimeMillis();
                if ((current - start) > 2) {
                    start = System.currentTimeMillis();
                    EventQueue.invokeLater(() -> {
                        lineChart.updateDataSeries(dataSeries -> {
                            int itemCount = dataSeries.getItemCount();
                            dataSeries.add(dataSeries.getX(itemCount - 1).doubleValue() + 1, noisePointNavigator.getNext());
                            if (itemCount > 5000) {
                                dataSeries.remove(0);
                            }
                        }, dataLabel);
                    });
                }
            }
        }
    }
}
