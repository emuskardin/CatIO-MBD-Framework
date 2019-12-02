package gui;

import model.Component;
import model.ModelData;
import model.ModelInput;
import model.Type;
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
    private List<String> simulationFieldsTypes;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FmiDataExtractor");
        frame.setContentPane(new FmiDataExtractor().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public FmiDataExtractor() {
        openFmi.addActionListener(e -> {
            JFileChooser j = new JFileChooser(Util.getCurrentDir());
            j.showOpenDialog(null);
            j.setFileSelectionMode(JFileChooser.FILES_ONLY);
            pathToFile = j.getSelectedFile().getPath();
            filePath.setText(pathToFile);

            Simulation simulation = new Simulation(pathToFile);

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
            List<String> data = getTableData();
            Util.writeToCSVFile(data);
        });

        generateMLCAButton.addActionListener(e -> {
            List<String> data = getTableData();

            if(data.isEmpty())
                Util.errorMsg("No data to create MLCA is provided");
            else{
                mlcaCreator = new MlcaCreator(Util.modelDataFromSting(String.join("\n", data)));
                mlcaCreator.createPopup();
            }
        });

        importScenariosButton.addActionListener(e -> {
            ((DefaultCellEditor) simScenariosTable.getDefaultEditor(Object.class)).setClickCountToStart(1);
            ModelData md = Util.modelDataFromSting(String.join("\n", getTableData()));
            simulationTableHeader = new ArrayList<>();
            simulationFieldsTypes = new ArrayList<>();
            simulationTableHeader.add("Scenario ID");
            simulationFieldsTypes.add("STRING");
            simulationTableHeader.add("Time");
            simulationFieldsTypes.add("DOUBLE");
            for(ModelInput hs : md.getHealthStates()) {
                simulationTableHeader.add(hs.getName());
                simulationFieldsTypes.add(hs.getType().name());
            }
            for(ModelInput in : md.getInputs()) {
                simulationTableHeader.add(in.getName());
                simulationFieldsTypes.add(in.getType().name());
            }
            for(ModelInput par : md.getParam()) {
                simulationTableHeader.add(par.getName());
                simulationFieldsTypes.add(par.getType().name());
            }

            scenarioTableModel = new DefaultTableModel(simulationTableHeader.toArray(), 0);
            simScenariosTable.setModel(scenarioTableModel);
        });

        addScenarioButton.addActionListener(e -> {
            Vector row = new Vector();
            for (int i = 0; i < simScenariosTable.getColumnCount(); i++)
                row.add("");
            scenarioTableModel.addRow(row);
        });

        exportScenariosButton.addActionListener(e -> {
            List<String> data = new ArrayList<>();
            data.add(String.join(",", simulationTableHeader) + "\n");
            data.add(String.join(",", simulationFieldsTypes) + "\n");
            for (int row = 0; row < simScenariosTable.getRowCount(); row++) {
                StringBuilder rowData = new StringBuilder();
                rowData.append(scenarioTableModel.getValueAt(row,0)).append(",");
                rowData.append(simScenariosTable.getValueAt(row, 1)).append(",");
                List<String> values = new ArrayList<>();
                for (int val = 2; val < simulationTableHeader.size(); val++)
                    values.add((String) simScenariosTable.getValueAt(row, val));

                rowData.append(String.join(",", values));
                //remove all whitespace .replaceAll("\\s+","")
                data.add(rowData.toString().replaceAll("\\s+","") + "\n");
            }

            Util.writeToCSVFile(data);
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

    private List<String> getTableData(){
        List<String> data = new ArrayList<>();
        for (int i = 0; i < dataTable.getRowCount(); i++) {
            if((Boolean) dataTable.getValueAt(i, 2)){
                data.add("Read," + dataTable.getValueAt(i,0) + "," +
                        getType((String) dataTable.getValueAt(i,1)) + "\n");
            }
            String valuesStr = (String) dataTable.getValueAt(i, 3);
            if (!valuesStr.isEmpty()) {
                valuesStr  = valuesStr.replaceAll("\\s+","");
                List<Object> values = Arrays.asList(valuesStr.split(","));
                for (int j = 0; j < values.size(); j++)
                    values.set(j, values.get(j).toString());

                String type = (String) dataTable.getValueAt(i, 4);
                if (type != null && !type.equals(""))
                    data.add(type + "," + dataTable.getValueAt(i, 0)  + "," + getType((String) dataTable.getValueAt(i,1))
                            + "," + valuesStr + '\n');
            }
        }
        return data;
    }
}
