package pl.edu.pwr.mrodak.jp.lab04.gui;


import pl.edu.pwr.mrodak.jp.lab04.client.ApiClient;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();
        GUIUtils guiUtils = new GUIUtils(apiClient);
        SwingUtilities.invokeLater(guiUtils::createAndShowGUI);
    }
}