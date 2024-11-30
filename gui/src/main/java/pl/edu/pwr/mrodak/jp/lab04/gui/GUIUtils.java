package pl.edu.pwr.mrodak.jp.lab04.gui;

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
import java.util.List;

public class GUIUtils {
    private ApiClient apiClient;
    private ChartUtils chartUtils;

    public GUIUtils(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.chartUtils = new ChartUtils(apiClient);
    }

    public void createAndShowGUI() {
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
                handleFetchButtonClick(fromField, toField, provinceComboBox, resultArea, frame);
            }
        });

        // Fetch initial data
        List<Province> provinceList = apiClient.getProvinces();
        for (Province province : provinceList) {
            provinceComboBox.addItem(province.getProvinceName());
        }

        frame.setVisible(true);
    }

    private void handleFetchButtonClick(JTextField fromField, JTextField toField, JComboBox<String> provinceComboBox, JTextArea resultArea, JFrame frame) {
        try {
            int yearFrom = Integer.parseInt(fromField.getText());
            int yearTo = Integer.parseInt(toField.getText());
            String selectedProvince = (String) provinceComboBox.getSelectedItem();
            List<AvgLivingSpace> data;
            if (yearTo == yearFrom) {
                displayTextResult(resultArea, selectedProvince, yearFrom);
            } else {
                data = apiClient.getAvgLivingSpace(yearFrom, yearTo);
                chartUtils.displayChart(resultArea, selectedProvince, data);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, "Wprowadz poprawne lata (miedzy 2010 a 2023).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayTextResult(JTextArea resultArea, String selectedProvince, int yearFrom) {
        StringBuilder resultText = new StringBuilder();
        int provinceId = apiClient.getProvinceIdByName(selectedProvince);
        List<AvgLivingSpace> data = apiClient.getAvgLivingSpace(yearFrom);
        for (AvgLivingSpace avgLivingSpace : data) {
            if (avgLivingSpace.getProvinceId() == provinceId) {
                resultText.append(avgLivingSpace.getYear())
                        .append(": ")
                        .append(avgLivingSpace.getValue())
                        .append(" m²\n");
            }
        }
        resultArea.removeAll();
        resultArea.setText(resultText.toString());
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 20)); // Set a larger font
        resultArea.setPreferredSize(new Dimension(800, 400)); // Set preferred size
        resultArea.setFocusable(false); // Make non-focusable
        resultArea.setEditable(false);
        resultArea.revalidate();
        resultArea.repaint();
    }

}