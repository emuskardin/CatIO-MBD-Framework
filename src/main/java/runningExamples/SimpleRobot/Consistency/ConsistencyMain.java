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
        String pathToStongFaultModel = "src/main/java/runningExamples/SimpleRobot/Consistency/strongFaultDiffModel.txt";
        String pathToScenarios = "src/main/java/runningExamples/SimpleRobot/Consistency/simpleScenarios.json";
        String pathToModelData = "src/main/java/runningExamples/SimpleRobot/Consistency/simpleRobot.json";

        // Simple robot example
        ModelData modelData = Util.modelDataFromJson(pathToModelData);
        // Set values to be plotted
        modelData.setPlotVariables("diffDrive.x", "diffDrive.y");
        modelData.setController(new RepairRobot());
        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .pathToFmi(inputModelRobot)
                .model(new CbModel(pathToSimpleModel))
                .encoder(new SimpleCarEncoder())
                .modelData(modelData)
                .numberOfSteps(20)
                .simulationStepSize(1)
                .build();

        List<Scenario> scenarios = Util.scenariosFromJson(pathToScenarios);
        //consistencyDriver.runDiagnosis(ConsistencyType.INTERMITTENT, scenarios.get(0));
        consistencyDriver.runDiagnosis(ConsistencyType.STEP, scenarios.get(0));

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
    }
}
