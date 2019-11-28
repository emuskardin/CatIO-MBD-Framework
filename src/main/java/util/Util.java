package util;

import FmiConnector.Component;
import FmiConnector.TYPE;
import abductive.combinatorial.ModelData;
import abductive.combinatorial.ModelInputData;

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

    public ExtractedData deserialize(String filePath) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filePath);
        List<Object> objectsList = new ArrayList<>();
        boolean cont = true;
        try{
            ObjectInputStream input = new ObjectInputStream(fis);
            while(cont){
                Object obj = input.readObject();
                if(obj != null)
                    objectsList.add(obj);
                else
                    cont = false;
            }
        }catch(Exception e){
            //System.out.println(e.printStackTrace());
        }
        return new ExtractedData(objectsList);
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

    public static ModelData modelDataFromSting(String content) {
        ModelData res = new ModelData();
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] values = line.split(",");
            List<Object> passedVal = new ArrayList<>(Arrays.asList(values).subList(3, values.length));

            switch (values[0]) {
                case "Input":
                    res.getInputs().add(new ModelInputData(values[1], passedVal, getType(values[2])));
                    break;
                case "Parameter":
                    res.getParam().add(new ModelInputData(values[1], passedVal, getType(values[2])));
                    break;
                case "Health State":
                    res.getComponents().add(new ModelInputData(values[1], passedVal, getType(values[2])));
                    break;
            }
        }
        return res;
    }

    private static TYPE getType(String x){
        switch (x){
            case "ENUM":
                return TYPE.ENUM;
            case "STRING":
                return TYPE.STRING;
            case "INTEGER":
                return TYPE.INTEGER;
            case "BOOLEAN":
                return TYPE.BOOLEAN;
            case "DOUBLE":
                return TYPE.DOUBLE;
        }
        return null;
    }

}
