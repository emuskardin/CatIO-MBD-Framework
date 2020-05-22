package runningExamples.SimpleRobot.Abductive;

import abductive.AbductiveModel;
import abductive.AbductiveModelGenerator;
import model.ModelData;
import util.Util;

import java.io.IOException;

public class CircuitsMain {
    public static void main(String[] args) throws IOException {
        String pathToCircuitFmi = "FMIs/SC_Example1.fmu";
        String pathToModelData = "singleCircuit.json";
        ModelData circuitData = Util.modelDataFromJson(pathToModelData);

        AbductiveModelGenerator abductiveModelGenerator = new AbductiveModelGenerator(pathToCircuitFmi, circuitData);
        abductiveModelGenerator.setEncoderAndDiff(new CircuitEncoder(), new CircuitDiff());

        // mixed level covering array will be automatically generated without any constraints
        // params for generate model are number of steps, step size and fault injection step
        abductiveModelGenerator.generateModel(50, 0.5, 20);
        AbductiveModel learnedModel = abductiveModelGenerator.getAbductiveModel();
        System.out.println(learnedModel.getRules());
        learnedModel.modelToFile("singleCircuit.txt");
    }
}
