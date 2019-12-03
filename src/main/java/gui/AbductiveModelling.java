package gui;

import abductive.AbductiveModel;
import util.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class AbductiveModelling {
    public JPanel panel;
    private JTextArea abductiveModelArea;
    private JTextField observationField;
    private JButton runDiagnosisButton;
    private JTextArea diagnosisArea;
    private JButton exportModelButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("AbductiveModelling");
        frame.setContentPane(new AbductiveModelling().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public AbductiveModelling() {
        runDiagnosisButton.addActionListener(e -> {
            if(abductiveModelArea.getText().isEmpty())
                return;
            AbductiveModel abductiveModel = new AbductiveModel();
            abductiveModel.setRules(Util.removeComments(abductiveModelArea.getText()));
            String obs = observationField.getText().replaceAll("\\s+","");
            abductiveModel.addExplain(Arrays.asList(obs.split(",")));
            diagnosisArea.setText(abductiveModel.getDiagnosis());
        });

        exportModelButton.addActionListener(e -> {
            if(abductiveModelArea.getText().isEmpty())
                return;
            JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(abductiveModelArea.getText());
                    fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


}
