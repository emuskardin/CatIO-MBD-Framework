package gui;

import model.*;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.wrapper.Simulation;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

            for(ScalarVariable var : simulation.getModelDescription().getModelVariables()){
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

            if(data.eachTypeHasValue())
                Util.errorMsg("No data to create MLCA is provided", JOptionPane.ERROR_MESSAGE);
            else{
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
            simulationTableHeader.add("Time");
            simulationFieldsTypes.add(Type.DOUBLE);
            for(ModelInput hs : md.getHealthStates()) {
                simulationTableHeader.add(hs.getName());
                simulationFieldsTypes.add(hs.getType());
            }
            for(ModelInput in : md.getInputs()) {
                simulationTableHeader.add(in.getName());
                simulationFieldsTypes.add(in.getType());
            }
            for(ModelInput par : md.getParam()) {
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
                String scenarioId = (String) scenarioTableModel.getValueAt(row,0);
                if(!scenarioId.equals(""))
                    scenarios.add(new Scenario(scenarioId));
                String timeStr = (String) scenarioTableModel.getValueAt(row,1);
                if(!timeStr.isEmpty()){
                    Double time = Double.parseDouble(timeStr);
                    List<Component> components = new ArrayList<>();
                    for (int i = 2; i < simulationTableHeader.size(); i++) {
                        if(!((String) scenarioTableModel.getValueAt(row,i)).isEmpty()) {
                            Component comp = new Component(simulationTableHeader.get(i),
                                    simulationFieldsTypes.get(i), scenarioTableModel.getValueAt(row, i));
                            components.add(comp);
                        }
                    }
                    scenarios.get(scenarios.size()-1).addToMap(time, components);
                }
            }
            Util.writeToJson(scenarios);
        });
    }

    private Type getType(String x){
        if(x.equals("Real"))
            return Type.DOUBLE;
        if(x.equals("Boolean"))
            return Type.BOOLEAN;
        if(x.equals("Integer"))
            return Type.INTEGER;
        if(x.equals("Enumeration"))
            return Type.ENUM;
        return Type.STRING;
    }

    private void createUIComponents() {
        dataTable = new JTable();
        String[] header = {"Variable name","Type","Read", "Values", "MLCA Type"};
        tableModel = new DefaultTableModel(header, 0){
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

    private ModelData getTableData(){
        List<Component> toRead = new ArrayList<>();
        ModelData modelData = new ModelData();
        for (int i = 0; i < dataTable.getRowCount(); i++) {
            if((Boolean) dataTable.getValueAt(i, 2))
                toRead.add(new Component((String) dataTable.getValueAt(i,0), getType((String) dataTable.getValueAt(i,1))));

            String type = (String) dataTable.getValueAt(i, 4);
            if (type != null && !type.equals("")){
                String valuesStr = (String) dataTable.getValueAt(i, 3);
                List<Object> passedVal = new ArrayList<>();
                if(!valuesStr.isEmpty())
                    passedVal = Arrays.asList(valuesStr.replaceAll("\\s+","").split(","));
                ModelInput mi = new ModelInput((String) dataTable.getValueAt(i,0),
                        getType((String)dataTable.getValueAt(i,1)), passedVal);
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
}
