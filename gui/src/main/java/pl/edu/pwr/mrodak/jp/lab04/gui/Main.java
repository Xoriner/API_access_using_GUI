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

public class Main {
    private static ApiClient apiClient = new ApiClient();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    };

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Przecietna powierzchnia uzytkowa 1 mieszkania");
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
                        StringBuilder resultText = new StringBuilder();
                        int provinceId = getProvinceIdByName(selectedProvince);
                        data = apiClient.getAvgLivingSpace(yearFrom);
                        for (AvgLivingSpace avgLivingSpace : data) {
                            if (avgLivingSpace.getProvinceId() == provinceId) {
                                resultText.append(avgLivingSpace.getYear())
                                        .append(": ")
                                        .append(avgLivingSpace.getValue())
                                        .append("\n");
                            }
                        }
                        resultArea.setText(resultText.toString());
                        resultArea.setEditable(false);
                    } else {
                        data = apiClient.getAvgLivingSpace(yearFrom, yearTo);
                        displayChart(resultArea, selectedProvince, data);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Wprowadz poprawne lata (miedzy 2010 a 2023).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Fetch initial data
        List<Province> provinceList = apiClient.getProvinces();
        for (Province province : provinceList) {
            provinceComboBox.addItem(province.getProvinceName());
        }

        frame.setVisible(true);

    }


    private static void displayChart(JTextArea resultArea, String selectedProvince, List<AvgLivingSpace> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sort the data by year
        data.sort(Comparator.comparingInt(AvgLivingSpace::getYear));

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (AvgLivingSpace avgLivingSpace : data) {
            if (avgLivingSpace.getProvinceId() == getProvinceIdByName(selectedProvince)) {
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