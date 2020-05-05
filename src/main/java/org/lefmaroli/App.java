package org.lefmaroli;

import org.lefmaroli.display.LineChart;
import org.lefmaroli.factorgenerator.FactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;
import org.lefmaroli.perlin1d.Perlin1D;
import org.lefmaroli.perlin1d.PerlinGrid1D;

import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        FactorGenerator distanceFactor = new MultiplierFactorGenerator(2048, 0.5);
        FactorGenerator amplitudeFactor = new MultiplierFactorGenerator(1.0, 1.0 / 1.8);
        PerlinGrid1D grid1D = new PerlinGrid1D(16, distanceFactor, amplitudeFactor, System.currentTimeMillis());

        Perlin1D perlin1D = new Perlin1D(grid1D);

        LineChart lineChart = new LineChart("Perlin1D", "Sequence #", "Value");
        String dataLabel = "DataSet";
        lineChart.addEquidistantDataSeries(perlin1D.getNext(50), dataLabel);
        lineChart.setVisible();

        boolean activateUpdate = true;
        if (activateUpdate) {
            long start = System.currentTimeMillis();
            while (true) {
                long current = System.currentTimeMillis();
                if ((current - start) > 2) {
                    start = System.currentTimeMillis();
                    EventQueue.invokeLater(() -> {
                        lineChart.updateEquidistantDataSeries(dataSeries -> {
                            int itemCount = dataSeries.getItemCount();
                            dataSeries.add(dataSeries.getX(itemCount - 1).doubleValue() + 1, perlin1D.getNext());
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
