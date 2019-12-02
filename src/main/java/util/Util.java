package util;

import model.*;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Util {
    public static File getCurrentDir(){ return new File(System.getProperty("user.dir"));}

    public static void plot(ArrayList<Double> x, ArrayList<Double> y, String filename){

        double[] x_plot = x.stream().mapToDouble(Double::doubleValue).toArray();
        double[] y_plot = y.stream().mapToDouble(Double::doubleValue).toArray();

        Plot plot = Plot.plot(null)
                .xAxis("x", Plot.axisOpts().range(Collections.min(x), Collections.max(x)))
                .yAxis("y", Plot.axisOpts().range(Collections.min(y), Collections.max(y)))
                .series(null, Plot.data().xy(x_plot, y_plot), null);

        try {
            plot.save(filename, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Component> componentsFromCsv(String filename){
        List<Component> res = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");
            for(String line : lines){
                String[] values = line.split(",");

                res.add(new Component(values[0], getType(values[0])));
            }
        } catch (IOException e) {
            e.getLocalizedMessage();
        }
        return res;
    }

    public static ModelData modelDataFromCsv(String filename){
        ModelData res = new ModelData();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            res = modelDataFromSting(content);
        } catch (IOException e) {
            e.getLocalizedMessage();
        }
        return res;
    }

    public static void errorMsg(String msg){
        JOptionPane.showMessageDialog(new JFrame(), msg, "Dialog",
                JOptionPane.ERROR_MESSAGE);
    }

    public static String writeToCSVFile(List<String> data) {
        JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(file);
                for (String line : data)
                    fw.append(line);
                fw.close();
                return file.getName();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static List<Scenario> simulationScenariosFromCSV(String filename){
        List<Scenario> scenarios = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            List<String> lines = Arrays.asList(content.split("\n"));
            List<String> header = Arrays.asList(lines.get(0).split(",", -1));
            List<String> types = Arrays.asList(lines.get(1).split(","));
            for(String line : lines.subList(2, lines.size())){
               ArrayList<String> values = new ArrayList<>(Arrays.asList(line.split(",")));
               while(values.size() < header.size())
                   values.add("");
               if(!values.get(0).isEmpty())
                   scenarios.add(new Scenario(values.get(0)));
               if(!values.get(1).isEmpty()){
                   Double time = Double.parseDouble(values.get(1));
                   List<Component> comps = new ArrayList<>();
                   for (int i = 2; i < header.size(); i++) {
                       if(!values.get(i).isEmpty()){
                           Component v = new Component(header.get(i), values.get(i));
                           v.setType(getType(types.get(i)));
                           comps.add(v);
                       }
                   }
                   scenarios.get(scenarios.size()-1).addToMap(time, comps);
               }

            }
        } catch (IOException e) {
            e.getLocalizedMessage();
        }
        return scenarios;
    }

    public static ModelData modelDataFromSting(String content) {
        ModelData res = new ModelData();
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] values = line.split(",");
            List<Object> passedVal = Collections.emptyList();
            if(values.length > 2)
                passedVal = new ArrayList<>(Arrays.asList(values).subList(3, values.length));

            switch (values[0]) {
                case "Read":
                    res.getComponentsToRead().add(new Component(values[1], getType(values[2])));
                    break;
                case "Input":
                    res.getInputs().add(new ModelInput(values[1], passedVal, getType(values[2])));
                    break;
                case "Parameter":
                    res.getParam().add(new ModelInput(values[1], passedVal, getType(values[2])));
                    break;
                case "Health State":
                    res.getHealthStates().add(new ModelInput(values[1], passedVal, getType(values[2])));
                    break;
            }
        }
        return res;
    }

    private static Type getType(String x){
        switch (x){
            case "ENUM":
                return Type.ENUM;
            case "STRING":
                return Type.STRING;
            case "INTEGER":
                return Type.INTEGER;
            case "BOOLEAN":
                return Type.BOOLEAN;
            case "DOUBLE":
                return Type.DOUBLE;
        }
        return null;
    }

}
