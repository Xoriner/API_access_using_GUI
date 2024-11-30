package pl.edu.pwr.mrodak.jp.lab04.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import pl.edu.pwr.mrodak.jp.lab04.client.ApiClient;
import pl.edu.pwr.mrodak.jp.lab04.client.models.AvgLivingSpace;
import pl.edu.pwr.mrodak.jp.lab04.client.models.Province;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static ApiClient apiClient = new ApiClient();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Living Space Data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 2));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

            JLabel provinceLabel = new JLabel("Region:");
            JComboBox<String> provinceComboBox = new JComboBox<>();
            JLabel fromLabel = new JLabel("Rok od:");
            JTextField fromField = new JTextField();
            JLabel toLabel = new JLabel("Rok do:");
            JTextField toField = new JTextField();
            JButton fetchButton = new JButton("Pobierz");

            fromField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (toField.getText().isEmpty()) {
                        toField.setText(fromField.getText());
                    }
                }
            });

            panel.add(provinceLabel);
            panel.add(provinceComboBox);
            panel.add(fromLabel);
            panel.add(fromField);
            panel.add(toLabel);
            panel.add(toField);
            panel.add(fetchButton);

            JTextArea resultArea = new JTextArea();
            resultArea.setBorder(new EmptyBorder(10, 10, 10, 10));
            resultArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(resultArea);

            frame.add(panel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);

            fetchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int yearFrom = Integer.parseInt(fromField.getText());
                        int yearTo = Integer.parseInt(toField.getText());
                        String selectedProvince = (String) provinceComboBox.getSelectedItem();
                        List<AvgLivingSpace> data;
                        if (yearTo == yearFrom) {
                            data = apiClient.getAvgLivingSpace(yearFrom);
                            // CHANGE THIS
                            resultArea.setText(data.stream()
                                    .filter(avgLivingSpace -> avgLivingSpace.getProvinceId() == getProvinceIdByName(selectedProvince))
                                    .map(avgLivingSpace -> avgLivingSpace.getYear() + ": " + avgLivingSpace.getValue())
                                    .collect(Collectors.joining("\n")));
                        } else {
                            data = apiClient.getAvgLivingSpace(yearFrom, yearTo);
                            displayChart(resultArea, selectedProvince, data, yearFrom, yearTo);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter valid years.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Fetch initial data
            List<Province> provinceList = apiClient.getProvinces();
            for (Province province : provinceList) {
                provinceComboBox.addItem(province.getProvinceName());
            }

            frame.setVisible(true);
        });
    }


    private static void displayChart(JTextArea resultArea, String selectedProvince, List<AvgLivingSpace> data, int yearFrom, int yearTo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sort the data by year
        data.sort(Comparator.comparingInt(AvgLivingSpace::getYear));

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (AvgLivingSpace avgLivingSpace : data) {
            if (avgLivingSpace.getProvinceId() == getProvinceIdByName(selectedProvince)) {
                double value = avgLivingSpace.getValue();
                dataset.addValue(value, "Living Space", String.valueOf(avgLivingSpace.getYear()));
                if (value < minValue) {
                    minValue = value;
                }
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Average Living Space",
                "Rok",
                "Wartość",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        var plot = lineChart.getCategoryPlot();
        var rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(minValue - 10, maxValue + 10);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        // Clear the resultArea and add the chartPanel
        resultArea.removeAll();
        resultArea.setLayout(new BorderLayout());
        resultArea.add(chartPanel, BorderLayout.CENTER);
        resultArea.revalidate();
        resultArea.repaint();
    }

    private static int getProvinceIdByName(String provinceName) {
        List<Province> provinceList = apiClient.getProvinces();
        for (Province province : provinceList) {
            if (province.getProvinceName().equals(provinceName)) {
                return province.getProvinceId();
            }
        }
        throw new IllegalArgumentException("Province not found: " + provinceName);
    }
}