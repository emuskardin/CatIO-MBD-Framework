package gui;

import consistency.mhsAlgs.RcTree;
import consistency.CbModel;
import util.Util;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ConsistencyModeling {
    public JPanel panel;
    private JTextArea cnfModelArea;
    private JTextField observationArea;
    private JButton diagnoseObservationButton;
    private JTextArea propLogModelArea;
    private JButton exportCNFModelButton;
    private JButton convertToCNFButton;
    private JTextArea diagnosisArea;
    private JButton exportModelButton;
    private JButton checkSatisfiabilityButton;
    private JTextArea picosatOutput;
    private CbModel cbModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("CBD Modeling");
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
            for(List<Integer> mhs  : rcTree.getDiagnosis()) {
                List<String> diag = cbModel.diagnosisToComponentNames(mhs);
                diagnosisArea.append(String.join(", ", diag) + "\n");
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
            cnfModelArea.append("c DIMACS CNF representation\n");
            cnfModelArea.append("p cnf " + cbModel.getPredicates().getSize() + " " + cbModel.getModel().size()+ "\n");
            for(List<Integer> cnfLIne : cbModel.getModel())
                cnfModelArea.append(cnfLIne.toString().substring(1, cnfLIne.toString().length() - 1) + " 0\n");

            for(List<String> line : cbModel.modelToString())
                cnfModelArea.append("c " + String.join(", ", line) + "\n");

        });

        exportCNFModelButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());
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
            JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());
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

        checkSatisfiabilityButton.addActionListener(e -> {
            Runtime rt = Runtime.getRuntime();
            try {
                File tmpFile = new File("cnfModel.tmp");
                FileWriter writer = new FileWriter(tmpFile);
                writer.write(cnfModelArea.getText());
                writer.close();
                String[] commands = {"lib/picomus", "cnfModel.tmp"};
                Process proc = rt.exec(commands);

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));

                String s;
                while ((s = stdInput.readLine()) != null) {
                    if(s.charAt(0) == 'c')
                        continue;
                    picosatOutput.append(s + "\n");
                }
                tmpFile.deleteOnExit();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        });
    }

}
