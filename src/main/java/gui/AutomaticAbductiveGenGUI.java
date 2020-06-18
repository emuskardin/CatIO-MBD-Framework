package gui;

import abductive.AbductiveModelGenerator;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import interfaces.Diff;
import interfaces.Encoder;
import runningExamples.SimpleRobot.Abductive.RobotDiff;
import runningExamples.SimpleRobot.Abductive.StrongFaultAbEncoder;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class AutomaticAbductiveGenGUI {
    private JTextField pathToFmiForm;
    private JButton fmiButton;
    private JTextField pathToModelDataForm;
    private JButton modelDataButton;
    private JTextField pathToDiffForm;
    private JTextField pathToEncoderForm;
    private JButton diffButton;
    private JButton pathToEncoderButton;
    private JTextField pathToCustomMcaForm;
    private JButton customMCAButton;
    private JTextField numStepsForm;
    private JButton generateModelButton;
    private JTextArea modelArea;
    private JTextField stepSizeForm;
    private JTextField faultInjectionStepForm;
    private JPanel panel;

    private String pathToFMI;
    private String pathToModelData;
    private String pathToDiff;
    private String pathToEncoder;
    private String pathToMCA;
    private int numSteps;
    private int faultInjectionStep;
    private Double stepSize;


    public static void main(String[] args) {
        JFrame frame = new JFrame("Automatic Model Generator");
        frame.setContentPane(new AutomaticAbductiveGenGUI().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public AutomaticAbductiveGenGUI() {
        pathToModelData = "/Users/emuskard/IdeaProjects/CatIO/src/main/java/runningExamples/SimpleRobot/Consistency/simpleRobot.json";
        pathToFMI = "/Users/emuskard/IdeaProjects/CatIO/FMIs/ERobot.SubModel.InputSimpleRobot.fmu";
        pathToEncoder = "/Users/emuskard/IdeaProjects/CatIO/src/main/java/runningExamples/SimpleRobot/Abductive/StrongFaultAbEncoder.java";
        pathToDiff = "/Users/emuskard/IdeaProjects/CatIO/src/main/java/runningExamples/SimpleRobot/Abductive/RobotDiff.java";

        fmiButton.addActionListener(e -> {
            pathToFMI = openFileChooserAndGetPath();
            pathToFmiForm.setText(pathToFMI);
        });
        modelDataButton.addActionListener(e -> {
            pathToModelData = openFileChooserAndGetPath();
            pathToModelDataForm.setText(pathToModelData);
        });
        diffButton.addActionListener(e -> {
            pathToDiff = openFileChooserAndGetPath();
            pathToDiffForm.setText(pathToDiff);
        });
        pathToEncoderButton.addActionListener(e -> {
            pathToEncoder = openFileChooserAndGetPath();
            pathToEncoderForm.setText(pathToEncoder);
        });
        customMCAButton.addActionListener(e -> {
            pathToMCA = openFileChooserAndGetPath();
            pathToCustomMcaForm.setText(pathToMCA);
        });

        generateModelButton.addActionListener(e -> {
            try {
                generateModel();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void generateModel() throws Exception {
        if (!checkFiled())
            return;

        AbductiveModelGenerator abductiveModelGenerator =
                new AbductiveModelGenerator(pathToFMI, Util.modelDataFromJson(pathToModelData), new RobotDiff());

        if (!pathToEncoder.isEmpty())
            abductiveModelGenerator.setEnc(new StrongFaultAbEncoder());

        abductiveModelGenerator.generateModel(numSteps, stepSize, faultInjectionStep);
        abductiveModelGenerator.getAbductiveModel().getRules().forEach(it -> {
            modelArea.append(it + "\n");
        });
    }

    private boolean checkFiled() {
        if (pathToDiff.isEmpty() || pathToFMI.isEmpty() || pathToModelData.isEmpty()) {
            Util.errorMsg("Check provided files", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            numSteps = Integer.parseInt(numStepsForm.getText());
            faultInjectionStep = Integer.parseInt(faultInjectionStepForm.getText());
            stepSize = Double.parseDouble(stepSizeForm.getText());
        } catch (NumberFormatException e) {
            Util.errorMsg("Check provided parameters", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String openFileChooserAndGetPath() {
        JFileChooser j = new JFileChooser(Util.getCurrentDir());
        j.showOpenDialog(null);
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return j.getSelectedFile().getPath();
    }

    private Diff getDiffImpl(String userClass) throws Exception {

        return null;
    }

    private Encoder getEncoderImpl(String userClass) throws Exception {
        // Load the defined class by the user if it implements our interface
        if (Encoder.class.isAssignableFrom(Class.forName(userClass))) {
            return (Encoder) Class.forName(userClass).newInstance();
        }
        throw new Exception("Class " + userClass + " does not implement " + Encoder.class.getName());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Data"));
        final JLabel label1 = new JLabel();
        label1.setText("Path to .fmi");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToFmiForm = new JTextField();
        panel1.add(pathToFmiForm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fmiButton = new JButton();
        fmiButton.setText("Open");
        panel1.add(fmiButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Path to Model Data");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToModelDataForm = new JTextField();
        panel1.add(pathToModelDataForm, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        modelDataButton = new JButton();
        modelDataButton.setText("Open");
        panel1.add(modelDataButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Path to Diff ");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Path to Encoder");
        panel1.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToDiffForm = new JTextField();
        panel1.add(pathToDiffForm, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pathToEncoderForm = new JTextField();
        panel1.add(pathToEncoderForm, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        diffButton = new JButton();
        diffButton.setText("Open");
        panel1.add(diffButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToEncoderButton = new JButton();
        pathToEncoderButton.setText("Open");
        panel1.add(pathToEncoderButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Custom MCA");
        panel1.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToCustomMcaForm = new JTextField();
        panel1.add(pathToCustomMcaForm, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        customMCAButton = new JButton();
        customMCAButton.setText("Open");
        panel1.add(customMCAButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Parameters"));
        final JLabel label6 = new JLabel();
        label6.setText("Number of steps");
        panel2.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Fault Injection step");
        panel2.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Step size");
        panel2.add(label8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numStepsForm = new JTextField();
        panel2.add(numStepsForm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        faultInjectionStepForm = new JTextField();
        panel2.add(faultInjectionStepForm, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        stepSizeForm = new JTextField();
        panel2.add(stepSizeForm, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        generateModelButton = new JButton();
        generateModelButton.setText("Generate Model");
        panel2.add(generateModelButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        modelArea = new JTextArea();
        modelArea.setText("");
        scrollPane1.setViewportView(modelArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
