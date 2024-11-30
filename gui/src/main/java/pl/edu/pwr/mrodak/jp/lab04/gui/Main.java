package pl.edu.pwr.mrodak.jp.lab04.gui;

import pl.edu.pwr.mrodak.jp.lab04.client.ApiClient;
import pl.edu.pwr.mrodak.jp.lab04.client.models.AvgLivingSpace;
import pl.edu.pwr.mrodak.jp.lab04.client.models.Province;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.Map;

public class Main {
    private static ApiClient apiClient = new ApiClient();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Living Space Data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 2));

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
                        if (yearTo == yearFrom) {
                            List<AvgLivingSpace> data = apiClient.getAvgLivingSpace(yearFrom);
                        } else {
                            Map<Integer, List<AvgLivingSpace>> data = apiClient.getAvgLivingSpace(yearFrom, yearTo);
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



}