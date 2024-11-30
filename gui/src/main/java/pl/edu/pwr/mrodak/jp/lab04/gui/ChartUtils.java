package pl.edu.pwr.mrodak.jp.lab04.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import pl.edu.pwr.mrodak.jp.lab04.client.ApiClient;
import pl.edu.pwr.mrodak.jp.lab04.client.models.AvgLivingSpace;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ChartUtils {
    private ApiClient apiClient;

    public ChartUtils(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void displayChart(JTextArea resultArea, String selectedProvince, List<AvgLivingSpace> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sort the data by year
        data.sort(Comparator.comparingInt(AvgLivingSpace::getYear));

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (AvgLivingSpace avgLivingSpace : data) {
            if (avgLivingSpace.getProvinceId() == apiClient.getProvinceIdByName(selectedProvince)) {
                double value = avgLivingSpace.getValue();
                dataset.addValue(value, "Powierzchnia użytkowa", String.valueOf(avgLivingSpace.getYear()));
                if (value < minValue) {
                    minValue = value;
                }
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Przecietna powierzchnia uzytkowa 1 mieszkania",
                "Rok",
                "Wartość [m²]",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        var plot = lineChart.getCategoryPlot();
        var rangeAxis = plot.getRangeAxis();
        // Getting nice axis range
        rangeAxis.setRange(minValue - (maxValue - minValue) * 0.1, maxValue + (maxValue - minValue) * 0.1);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        // Clear the resultArea and add the chartPanel
        resultArea.removeAll();
        resultArea.setLayout(new BorderLayout());
        resultArea.add(chartPanel, BorderLayout.CENTER);
        resultArea.revalidate();
        resultArea.repaint();
    }
}