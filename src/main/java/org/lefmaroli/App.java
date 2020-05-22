package org.lefmaroli;

import org.lefmaroli.display.LineChart;
import org.lefmaroli.factorgenerator.SingleFactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.point.NoisePointNavigator;
import org.lefmaroli.perlin.point.NoisePointGenerator;
import org.lefmaroli.perlin.point.NoisePointGeneratorBuilder;

import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws NoiseBuilderException {

        SingleFactorGenerator distanceFactorGenerator = new MultiplierFactorGenerator(2048, 0.5);
        SingleFactorGenerator amplitudeFactorGenerator = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);
        NoisePointGenerator grid1D = new NoisePointGeneratorBuilder()
                .withNumberOfLayers(16)
                .withDistanceFactorGenerator(distanceFactorGenerator)
                .withAmplitudeFactorGenerator(amplitudeFactorGenerator)
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
