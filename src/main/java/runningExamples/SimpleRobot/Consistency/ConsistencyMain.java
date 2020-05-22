package runningExamples.SimpleRobot.Consistency;

import consistency.CbModel;
import consistency.ConsistencyDriver;
import consistency.ConsistencyType;
import model.ModelData;
import model.Scenario;
import org.apache.commons.lang3.Pair;
import util.Util;

import java.util.List;

public class ConsistencyMain {
    public static void main(String[] args) {
        String inputModelRobot = "FMIs/ERobot.SubModel.InputSimpleRobot.fmu";
        String pathToSimpleModel = "src/main/java/runningExamples/SimpleRobot/Consistency/simpleModel.txt";
        String pathToScenarios = "src/main/java/runningExamples/SimpleRobot/Consistency/simpleScenarios.json";
        String pathToModelData = "src/main/java/runningExamples/SimpleRobot/Consistency/simpleRobot.json";

        // Robot data extracted from JSON
        ModelData modelData = Util.modelDataFromJson(pathToModelData);
        // Set values to be plotted
        modelData.setPlotVariables("diffDrive.x", "diffDrive.y");
        // Set controller which will perform repair actions
        //modelData.setController(new RepairRobot());
        // Connect everything together
        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .pathToFmi(inputModelRobot)
                .model(new CbModel(pathToSimpleModel))
                .encoder(new SimpleCarEncoder())
                .modelData(modelData)
                .numberOfSteps(5)
                .simulationStepSize(1)
                .build();

        // Load simulations from file
        List<Scenario> scenarios = Util.scenariosFromJson(pathToScenarios, modelData);
        // Run diagnosis and if possible repair from scenario 0
        consistencyDriver.runDiagnosis(ConsistencyType.STEP, scenarios.get(3));
/*
        System.out.println("Strong fault model diagnosis");
        ConsistencyDriver strongFaultDriver = ConsistencyDriver.builder()
                .pathToFmi(inputModelRobot)
                .model(new CbModel(pathToStongFaultModel))
                .encoder(new StrongFaultDiffEncoder())
                .modelData(modelData)
                .numberOfSteps(20)
                .simulationStepSize(1)
                .build();

        //strongFaultDriver.runDiagnosis(ConsistencyType.STEP, scenarios.get(2));
*/
    }
}

