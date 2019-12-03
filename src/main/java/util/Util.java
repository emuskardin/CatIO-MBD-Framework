package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.ModelData;
import model.Scenario;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public static void errorMsg(String msg, int msgType){
        JOptionPane.showMessageDialog(new JFrame(), msg, "Dialog", msgType);
    }

    public static void writeToJson(Object object){
        JFileChooser fileChooser = new JFileChooser(Util.getCurrentDir());

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(object, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Scenario> scenariosFromJson(String filename){
        return (List<Scenario>) jsonToObject(filename, new TypeToken<List<Scenario>>(){}.getType());
    }

    public static ModelData modelDataFromJson(String filename){
        return (ModelData) jsonToObject(filename, new TypeToken<ModelData>(){}.getType());
    }

    public static Object jsonToObject(String filename, Type type){
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filename)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String removeComments(String content){
        content = content.replaceAll("(?s)/\\*.*?\\*/","");
        content = content.replaceAll("//.*", "");
        return content;
    }

}
