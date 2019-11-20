package gui;

import FmiConnector.Component;
import FmiConnector.TYPE;
import abductive.combinatorial.ModelInputData;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.wrapper.Simulation;

import javax.help.HelpSet;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FmiDataExtractor {

    private JPanel panel;
    private JButton extractButton;
    private JTextField filePath;
    private JButton search;
    private JTable dataTable;
    private JButton export;
    private String pathToFile;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FmiDataExtractor");
        frame.setContentPane(new FmiDataExtractor().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public FmiDataExtractor() {
        search.addActionListener(e -> {
            JFileChooser j = new JFileChooser("newFmi");
            j.showOpenDialog(null);
            j.setFileSelectionMode(JFileChooser.FILES_ONLY);
            pathToFile = j.getSelectedFile().getAbsolutePath();
            filePath.setText(pathToFile);
        });

        extractButton.addActionListener(e -> {
            System.out.println(pathToFile);
            Simulation simulation = new Simulation(pathToFile);
            String[] header = {"Variable name","Type","Read", "Parameters"};
            DefaultTableModel tableModel = new DefaultTableModel(header, 0){
                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 2)
                        return Boolean.class;
                    return String.class;
                }
            };
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


        export.addActionListener(e -> {
            List<Component> toBeRead = new ArrayList<>();
            List<ModelInputData> modelInputData = new ArrayList<>();
            for (int i = 0; i < dataTable.getRowCount(); i++) {
                if((Boolean) dataTable.getValueAt(i, 2)){
                    toBeRead.add(new Component((String) dataTable.getValueAt(i,0),
                            getType((String) dataTable.getValueAt(i,1))));
                }

                String inputs = (String) dataTable.getValueAt(i,3);

                if(!inputs.isEmpty()){
                    List<Object> values = Arrays.asList(inputs.split(","));
                    for (int j = 0; j < values.size(); j++)
                        values.set(j, values.get(j).toString().trim());

                    ModelInputData mid = new ModelInputData((String) dataTable.getValueAt(i,0),
                    values, getType((String) dataTable.getValueAt(i,1)));
                    modelInputData.add(mid);
                }
            }

            FileOutputStream fout;
            try {
                fout = new FileOutputStream("extractedData.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(toBeRead);
                oos.writeObject(modelInputData);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
    }

    private TYPE getType(String x){
        if(x.equals("Real"))
            return TYPE.DOUBLE;
        if(x.equals("Boolean"))
            return TYPE.BOOLEAN;
        if(x.equals("Integer"))
            return TYPE.INTEGER;
        // TODO STRING or not
        return TYPE.ENUM;
    }

}
