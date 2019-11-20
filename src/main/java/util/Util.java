package util;

import FmiConnector.Component;
import abductive.combinatorial.ModelInputData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
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
        ExtractedData extractedData = new ExtractedData(objectsList);
        return extractedData;
    }

}
