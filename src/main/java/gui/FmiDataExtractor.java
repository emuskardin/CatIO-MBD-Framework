package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import model.*;
import model.Component;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.wrapper.Simulation;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FmiDataExtractor {

    public JPanel panel;
    private JTextField filePath;
    private JButton openFmi;
    private JTable dataTable;
    private JButton exportModelData;
    private JButton generateMLCAButton;
    private JPanel exportPanel;
    private JTable simScenariosTable;
    private JButton addScenarioButton;
    private JButton importScenariosButton;
    private JButton exportScenariosButton;
    private String pathToFile;
    private DefaultTableModel tableModel;
    private MlcaCreator mlcaCreator;
    private DefaultTableModel scenarioTableModel;
    private List<String> simulationTableHeader;
    private List<Type> simulationFieldsTypes;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FmiDataExtractor");
        frame.setContentPane(new FmiDataExtractor().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    FmiDataExtractor() {
        $$$setupUI$$$();
        openFmi.addActionListener(e -> {
            JFileChooser j = new JFileChooser(Util.getCurrentDir());
            j.showOpenDialog(null);
            j.setFileSelectionMode(JFileChooser.FILES_ONLY);
            pathToFile = j.getSelectedFile().getPath();
            filePath.setText(pathToFile);

            Simulation simulation = new Simulation(pathToFile);

            int rowCount = tableModel.getRowCount();
            for (int i = rowCount - 1; i >= 0; i--)
                tableModel.removeRow(i);

            for (ScalarVariable var : simulation.getModelDescription().getModelVariables()) {
                ArrayList<Object> varData = new ArrayList<>();
                varData.add(var.getName());
                varData.add(var.getTypeName());
                varData.add(Boolean.FALSE);
                varData.add("");
                tableModel.addRow(varData.toArray());
            }
            dataTable.setModel(tableModel);

        });


        exportModelData.addActionListener(e -> {
            ModelData data = getTableData();
            Util.writeToJson(data);
        });

        generateMLCAButton.addActionListener(e -> {
            ModelData data = getTableData();

            if (data.eachTypeHasValue())
                Util.errorMsg("No data to create MLCA is provided", JOptionPane.ERROR_MESSAGE);
            else {
                mlcaCreator = new MlcaCreator(data);
                mlcaCreator.createPopup();
            }
        });

        importScenariosButton.addActionListener(e -> {
            ((DefaultCellEditor) simScenariosTable.getDefaultEditor(Object.class)).setClickCountToStart(1);
            ModelData md = getTableData();
            simulationTableHeader = new ArrayList<>();
            simulationFieldsTypes = new ArrayList<>();
            simulationTableHeader.add("Scenario ID");
            simulationFieldsTypes.add(Type.STRING);
            simulationTableHeader.add("Time Step");
            simulationFieldsTypes.add(Type.INTEGER);
            for (ModelInput hs : md.getHealthStates()) {
                simulationTableHeader.add(hs.getName());
                simulationFieldsTypes.add(hs.getType());
            }
            for (ModelInput in : md.getInputs()) {
                simulationTableHeader.add(in.getName());
                simulationFieldsTypes.add(in.getType());
            }
            for (ModelInput par : md.getParam()) {
                simulationTableHeader.add(par.getName());
                simulationFieldsTypes.add(par.getType());
            }

            scenarioTableModel = new DefaultTableModel(simulationTableHeader.toArray(), 0);
            simScenariosTable.setModel(scenarioTableModel);
        });

        addScenarioButton.addActionListener(e -> {
            Vector<String> row = new Vector<>();
            for (int i = 0; i < simScenariosTable.getColumnCount(); i++)
                row.add("");
            scenarioTableModel.addRow(row);
        });

        exportScenariosButton.addActionListener(e -> {
            List<Scenario> scenarios = new ArrayList<>();
            for (int row = 0; row < simScenariosTable.getRowCount(); row++) {
                String scenarioId = (String) scenarioTableModel.getValueAt(row, 0);
                if (!scenarioId.equals(""))
                    scenarios.add(new Scenario(scenarioId));
                String timeStr = (String) scenarioTableModel.getValueAt(row, 1);
                if (!timeStr.isEmpty()) {
                    Integer time = Integer.parseInt(timeStr);
                    List<Component> components = new ArrayList<>();
                    for (int i = 2; i < simulationTableHeader.size(); i++) {
                        if (!((String) scenarioTableModel.getValueAt(row, i)).isEmpty()) {
                            Component comp = new Component(simulationTableHeader.get(i),
                                    simulationFieldsTypes.get(i), scenarioTableModel.getValueAt(row, i));
                            components.add(comp);
                        }
                    }
                    scenarios.get(scenarios.size() - 1).addToMap(time, components);
                }
            }
            Util.writeToJson(scenarios);
        });
    }

    private Type getType(String x) {
        if (x.equals("Real"))
            return Type.DOUBLE;
        if (x.equals("Boolean"))
            return Type.BOOLEAN;
        if (x.equals("Integer"))
            return Type.INTEGER;
        if (x.equals("Enumeration"))
            return Type.ENUM;
        return Type.STRING;
    }

    private void createUIComponents() {
        dataTable = new JTable();
        String[] header = {"Variable name", "Type", "Read", "Values", "MLCA Type"};
        tableModel = new DefaultTableModel(header, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2)
                    return Boolean.class;
                return String.class;
            }
        };
        String[] mlcaTypes = {"Input", "Parameter", "Health State", ""};
        JComboBox mlcaTypeComboBox = new JComboBox<>(mlcaTypes);
        dataTable.setModel(tableModel);
        dataTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(mlcaTypeComboBox));
        ((DefaultCellEditor) dataTable.getDefaultEditor(Object.class)).setClickCountToStart(1);
    }

    private ModelData getTableData() {
        List<Component> toRead = new ArrayList<>();
        ModelData modelData = new ModelData();
        for (int i = 0; i < dataTable.getRowCount(); i++) {
            if ((Boolean) dataTable.getValueAt(i, 2))
                toRead.add(new Component((String) dataTable.getValueAt(i, 0), getType((String) dataTable.getValueAt(i, 1))));

            String type = (String) dataTable.getValueAt(i, 4);
            if (type != null && !type.equals("")) {
                String valuesStr = (String) dataTable.getValueAt(i, 3);
                List<Object> passedVal = new ArrayList<>();
                if (!valuesStr.isEmpty())
                    passedVal = Arrays.asList(valuesStr.replaceAll("\\s+", "").split(","));
                ModelInput mi = new ModelInput((String) dataTable.getValueAt(i, 0),
                        getType((String) dataTable.getValueAt(i, 1)), passedVal);
                switch (type) {
                    case "Input":
                        modelData.getInputs().add(mi);
                        break;
                    case "Parameter":
                        modelData.getParam().add(mi);
                        break;
                    case "Health State":
                        modelData.getHealthStates().add(mi);
                        break;
                }
            }
        }
        modelData.setComponentsToRead(toRead);
        return modelData;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(6, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JLabel label1 = new JLabel();
        label1.setText("Select file te extract data");
        panel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 0, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        dataTable.setAutoCreateRowSorter(true);
        dataTable.setAutoResizeMode(2);
        dataTable.setDropMode(DropMode.ON);
        dataTable.setFillsViewportHeight(false);
        dataTable.setGridColor(new Color(-16777216));
        dataTable.setSelectionBackground(new Color(-16728359));
        dataTable.setShowVerticalLines(true);
        scrollPane1.setViewportView(dataTable);
        exportPanel = new JPanel();
        exportPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(exportPanel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(152, 293), null, 0, false));
        exportModelData = new JButton();
        exportModelData.setText("Export Model Data");
        exportPanel.add(exportModelData, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateMLCAButton = new JButton();
        generateMLCAButton.setText("Generate MLCA");
        exportPanel.add(generateMLCAButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        exportPanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        filePath = new JTextField();
        panel.add(filePath, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(400, -1), new Dimension(150, -1), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(4, 0, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        simScenariosTable = new JTable();
        simScenariosTable.setGridColor(new Color(-16777216));
        simScenariosTable.setPreferredScrollableViewportSize(new Dimension(450, 400));
        simScenariosTable.setSelectionBackground(new Color(-16728359));
        scrollPane2.setViewportView(simScenariosTable);
        addScenarioButton = new JButton();
        addScenarioButton.setText("Add Scenario");
        panel1.add(addScenarioButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        importScenariosButton = new JButton();
        importScenariosButton.setText("Initialize Scenario Table");
        panel1.add(importScenariosButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Simulation Scenarios");
        panel.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 25), null, 0, false));
        openFmi = new JButton();
        openFmi.setText("Open");
        panel.add(openFmi, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportScenariosButton = new JButton();
        exportScenariosButton.setText("Export Scenarios");
        panel.add(exportScenariosButton, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel.add(spacer3, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
