package org.lefmaroli;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.factorgenerator.FactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;
import org.lefmaroli.perlin1d.Perlin1D;
import org.lefmaroli.perlin1d.PerlinGrid1D;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        FactorGenerator distanceFactorGenerator = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactorGenerator = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);
        PerlinGrid1D grid1D = new PerlinGrid1D.Builder()
                .withNumberOfLayers(16)
                .withDistanceFactorGenerator(distanceFactorGenerator)
                .withAmplitudeFactorGenerator(amplitudeFactorGenerator)
                .build();

        Perlin1D perlin1D = new Perlin1D(grid1D);

//                .Builder()
//                .withDistance(2046)
//                .withLayers(16)
//                .withDistanceFactor(2)
//                .withAmplitudeFactor(1.8)
//                .build();

        XYSeriesCollection dataset = LineChart.createEquidistantDataset(perlin1D.getNext(50), "Perlin1D");
        EventQueue.invokeLater(() -> {
            JFrame framedChart = LineChart.getFramedChart("Perlin1D", "Sequence", "Value", dataset);
            framedChart.setVisible(true);
        });

        boolean activateUpdate = true;
        if (activateUpdate) {
            long start = System.currentTimeMillis();
            while (true) {
                long current = System.currentTimeMillis();
                if ((current - start) > 2) {
                    start = System.currentTimeMillis();
                    EventQueue.invokeLater(() -> {
                        XYSeries dataSeries = dataset.getSeries("Perlin1D");
                        int itemCount = dataSeries.getItemCount();
                        dataSeries.add(dataSeries.getX(itemCount - 1).doubleValue() + 1, perlin1D.getNext());
                        if (itemCount > 5000) {
                            dataSeries.remove(0);
                        }
                    });
                }
            }
        }
    }
}
