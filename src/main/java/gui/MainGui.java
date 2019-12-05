package gui;

import javax.swing.*;
import java.awt.*;

public class MainGui {
    private JTabbedPane tabbedPane;

    public static void main(String[] args) {
        JFrame frame = new JFrame("CatIO");
        frame.setContentPane(new MainGui().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel panel;
    private JPanel fmiExtPane;
    private JPanel constModPane;
    private JPanel abModPane;

    private void createUIComponents() {
       fmiExtPane = new FmiDataExtractor().panel;
       constModPane = new ConsistencyModelling().panel;
       abModPane = new AbductiveModelling().panel;
    }

}
