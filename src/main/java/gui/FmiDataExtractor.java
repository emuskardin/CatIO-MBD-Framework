package gui;

import FmiConnector.Component;
import FmiConnector.Type;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.wrapper.Simulation;
import util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FmiDataExtractor {

    public JPanel panel;
    private JTextField filePath;
    private JButton openFmi;
    private JTable dataTable;
    private JButton exportRead;
    private JButton generateMLCAButton;
    private JPanel exportPanel;
    private JTable simScenariosTable;
    private JButton addScenarioButton;
    private JButton importScenariosButton;
    private String pathToFile;
    private DefaultTableModel tableModel;
    private MlcaCreator mlcaCreator;

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


        exportRead.addActionListener(e -> {
            List<String> data = new ArrayList<>();
            for (int i = 0; i < dataTable.getRowCount(); i++) {
                if((Boolean) dataTable.getValueAt(i, 2)){
                    data.add(dataTable.getValueAt(i,0) + "," +
                            getType((String) dataTable.getValueAt(i,1)));
                }
            }
            writeToCSVFile(data);
        });

        generateMLCAButton.addActionListener(e -> {
            List<String> exportData = new ArrayList<>();
            for (int i = 0; i < dataTable.getRowCount(); i++) {
                String valuesStr = (String) dataTable.getValueAt(i, 3);
                if (!valuesStr.isEmpty()) {
                    List<Object> values = Arrays.asList(valuesStr.split(","));
                    for (int j = 0; j < values.size(); j++)
                        values.set(j, values.get(j).toString().trim());

                    String type = (String) dataTable.getValueAt(i, 4);
                    if (type != null && !type.equals(""))
                        exportData.add(type + "," + dataTable.getValueAt(i, 0)  + "," + getType((String) dataTable.getValueAt(i,1))
                                + "," + valuesStr);
                }
            }

            if(exportData.isEmpty())
                Util.errorMsg("No data to create MLCA is provided");
            else{
                mlcaCreator = new MlcaCreator(Util.modelDataFromSting(String.join("\n", exportData)));
                mlcaCreator.createPopup();
            }

        });

        importScenariosButton.addActionListener(e -> {
            List<List<Component>> scenarios = mlcaCreator.scenarios;
            List<String> tableHeader = new ArrayList<>();
            tableHeader.add("Fault Injection Time");
            for(Component comp : scenarios.get(0))
                tableHeader.add(comp.getName());

            tableModel = new DefaultTableModel(tableHeader.toArray(), 0);
            for(List<Component> scenario : scenarios){
                List<Object> rowData = new ArrayList<>();
                rowData.add("");
                scenario.forEach(it -> rowData.add(it.getValue()));
                tableModel.addRow(rowData.toArray());
            }

            simScenariosTable.setModel(tableModel);
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
    }

    private String writeToCSVFile(List<String> data) {
        JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(file);
                for (String datum : data) {
                    fw.append(datum).append("\n");
                }
                fw.close();
                return file.getName();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
