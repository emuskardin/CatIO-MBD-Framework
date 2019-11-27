package gui;

import consistency.mhsAlgs.RcTree;
import consistency.stepFaultDiag.CbModel;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConsistencyModeling {
    private JTextArea cnfModelArea;
    private JTextArea observationArea;
    private JButton diagnoseObservationButton;
    private JTextArea propLogModelArea;
    private JButton exportCNFModelButton;
    private JButton convertToCNFButton;
    private JPanel panel;
    private JTextArea diagnosisArea;
    private JButton exportModelButton;
    private CbModel cbModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FmiDataExtractor");
        frame.setContentPane(new ConsistencyModeling().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public ConsistencyModeling() {
        diagnoseObservationButton.addActionListener(e -> {
            diagnosisArea.setText(null);
            String obsStr = observationArea.getText();
            if(obsStr.isEmpty() || cbModel == null)
                return;
            obsStr = obsStr.replaceAll("\\s+","");
            List<String> obs = Arrays.asList(obsStr.split(","));
            RcTree rcTree = new RcTree(cbModel, cbModel.observationToInt(obs));
            try {
                for(List<Integer> mhs  : rcTree.getDiagnosis()) {
                    List<String> diag = cbModel.diagnosisToComponentNames(mhs, cbModel.getNumOfDistinct());
                    diagnosisArea.append(String.join(", ", diag) + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        convertToCNFButton.addActionListener(e -> {
            cnfModelArea.setText(null);
            String model = propLogModelArea.getText();
            if(model.isEmpty())
                return;
            cbModel = new CbModel();
            cbModel.modelToCNF(model);
            cbModel.setNumOfDistinct(cbModel.getPredicates().getSize());
            for(List<String> line : cbModel.modelToString())
                cnfModelArea.append(String.join(", ", line) + "\n");

        });

        exportCNFModelButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(cnfModelArea.getText());
                    fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exportModelButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(propLogModelArea.getText());
                    fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
