import ui.LoginScreen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel for better native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default look and feel
        }

        // Set some global UI properties
        UIManager.put("OptionPane.background", new java.awt.Color(30, 30, 46));
        UIManager.put("Panel.background", new java.awt.Color(30, 30, 46));
        UIManager.put("OptionPane.messageForeground", new java.awt.Color(245, 245, 240));
        UIManager.put("Button.background", new java.awt.Color(13, 148, 136));
        UIManager.put("Button.foreground", new java.awt.Color(245, 245, 240));

        // Launch application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }
}

