package org.lefmaroli;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.perlin1d.Perlin1D;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        Perlin1D perlin1D = new Perlin1D(2000, 4);

        XYSeriesCollection dataset = LineChart.createEquidistantDataset(perlin1D.getNext(50), "Perlin1D");
        EventQueue.invokeLater(() -> {
            JFrame framedChart = LineChart.getFramedChart("Perlin1D", "Sequence", "Value", dataset);
            framedChart.setVisible(true);
        });

        boolean activateUpdate = true;
        if(activateUpdate) {
            long start = System.currentTimeMillis();
            while (true) {
                long current = System.currentTimeMillis();
                if ((current - start) > 5) {
                    start = System.currentTimeMillis();
                    EventQueue.invokeLater(() -> {
                        XYSeries dataSeries = dataset.getSeries("Perlin1D");
                        int itemCount = dataSeries.getItemCount();
                        dataSeries.add(dataSeries.getX(itemCount - 1).doubleValue() + 1, perlin1D.getNext());
                        if (itemCount > 25 * perlin1D.getDistance() || itemCount > 5000) {
                            dataSeries.remove(0);
                        }
                    });
                }
            }
        }
    }
}
