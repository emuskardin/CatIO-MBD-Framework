package gui;

import FmiConnector.Component;
import abductive.combinatorial.MLCA;
import abductive.combinatorial.ModelData;
import aima.core.agent.Model;
import util.Util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MlcaCreator {
    private JPanel panel;
    private JTextField numCorrCompsTextField;
    private JTextArea constraintTextArea;
    private JButton generateMLCAButton;
    private JTextField inputsTextField;
    private JTextField paramsTextField;
    private JTextField hsTextField;
    private MLCA mlca;
    public List<List<Component>> scenarios;

    public void createPopup(){
        JFrame frame = new JFrame("MlcaCreator");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MlcaCreator(ModelData md) {
        mlca = new MLCA(md);
        generateMLCAButton.addActionListener(e -> {
            if(!inputsTextField.getText().isEmpty())
                mlca.addRelationToGroup(mlca.getInputs(), Integer.parseInt(inputsTextField.getText()));
            if(!paramsTextField.getText().isEmpty())
                mlca.addRelationToGroup(mlca.getParams(), Integer.parseInt(paramsTextField.getText()));
            if(!hsTextField.getText().isEmpty())
                mlca.addRelationToGroup(mlca.getComponents(), Integer.parseInt(hsTextField.getText()));

            if(!numCorrCompsTextField.getText().isEmpty()){
                String req = numCorrCompsTextField.getText();
                if (req.contains(",")){
                    String[] nums = req.replaceAll("\\s+","").split(",");
                    List<Integer> reqs= new ArrayList<>();
                    for (String num : nums) reqs.add(Integer.parseInt(num));
                    mlca.numberOfCorrectComps(reqs);
                }else
                    mlca.numberOfCorrectComps(Integer.parseInt(req));
            }

            if(!constraintTextArea.getText().isEmpty())
                mlca.addConstraint(constraintTextArea.getText());

            JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                mlca.createTestSuite(file.getName());
                try {
                    scenarios = mlca.suitToSimulationInput(file.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("MlcaCreator");
//        frame.setContentPane(new MlcaCreator().panel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }
}
