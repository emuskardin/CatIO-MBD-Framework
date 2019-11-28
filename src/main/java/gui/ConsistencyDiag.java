package gui;

import javax.swing.*;

public class ConsistencyDiag {
    private JPanel mainPanel;
    private JPanel modelingPanel;
    private JPanel fmiDataPanel;
    private JPanel consDiagPanel;
    private JButton diagnoseScenarioButton;
    private JTextArea textArea1;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ConsistencyDiag");
        frame.setContentPane(new ConsistencyDiag().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        modelingPanel = new ConsistencyModeling().panel;
        fmiDataPanel = new FmiDataExtractor().panel;
    }

}
