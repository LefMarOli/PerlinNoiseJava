package org.lefmaroli.display;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.function.Consumer;

public class LineChart {

    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private final String title;
    private final String xAxisLabel;
    private final String yAxisLabel;
    private final JFrame framedChart;

    public LineChart(String title, String xAxisLabel, String yAxisLabel){
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.framedChart = getFramedChart();
    }

    public void addEquidistantDataSeries(Collection<Double> dataSeries, String dataSeriesLabel) {
        XYSeries series = new XYSeries(dataSeriesLabel);
        int index = 0;
        for (Double dataPoint : dataSeries) {
            series.add(index, dataPoint);
            index++;
        }
        dataset.addSeries(series);
    }

    public void updateDataSeries(Consumer<XYSeries> updateFunction, String dataSeriesLabel){
        XYSeries series = dataset.getSeries(dataSeriesLabel);
        updateFunction.accept(series);
    }

    public void setVisible(){
        framedChart.setVisible(true);
    }

    private JFrame getFramedChart() {
        JFrame frame = new JFrame(title);
        frame.setContentPane(createChartPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private ChartPanel createChartPanel() {
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
        configureChart(chart);
        return new ChartPanel(chart);
    }

    private void configureChart(JFreeChart chart){
        if(!title.isEmpty()) {
            chart.setTitle(new TextTitle(title,
                            new Font("Serif", java.awt.Font.BOLD, 18)
                    )
            );
        }
        configureXYPlot(chart.getXYPlot());
    }

    private void configureXYPlot(XYPlot plot){
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
        }
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        setGridLines(plot);
    }

    private void setGridLines(XYPlot plot){
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
    }
}