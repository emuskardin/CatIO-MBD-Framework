package runningExamples;

import examples.ExtendedRobotEncoder;
import model.Component;
import FmiConnector.FmiMonitor;
import model.Scenario;
import model.Type;
import abductive.AbductiveDriver;
import abductive.AbductiveModel;
import model.ModelData;
import consistency.ConsistencyType;
import examples.BookAbEncoder;
import consistency.CbModel;
import consistency.ConsistencyDriver;
import examples.BookCarEncoder;
import org.apache.commons.lang3.Pair;
import util.Util;

import java.util.Arrays;
import java.util.List;

public class DefaultMain {
    public static void main(String [] args) {

        String[] fmiPath = {"FMIs/ERobot.Experiments.RampInput.fmu", "FMIs/ERobot.Experiments.RampWFault.fmu",
                "FMIs/ERobot.Experiments.constWFault.fmu", "FMIs/ERobot.Experiments.RampIntermittent.fmu",
                "FMIs/ERobot.Experiments.ConstBothBreak.fmu", "FMIs/ERobot.SubModel.InputSimpleRobot.fmu"};
        FmiMonitor fmiMonitor = new FmiMonitor(fmiPath[5]);

        ModelData abModelData = new ModelData();
        abModelData.setComponentsToRead(
                Arrays.asList(
                        new Component("robot.rightWheel.i", Type.DOUBLE),
                        new Component("robot.rightWheel.o", Type.DOUBLE),
                        new Component("robot.leftWheel.i", Type.DOUBLE),
                        new Component("robot.leftWheel.o", Type.DOUBLE)
                )
        );

        FmiMonitor abFmiMonitor = new FmiMonitor(fmiPath[4]);
        AbductiveDriver abductiveDriver = AbductiveDriver.builder()
                .fmiMonitor(abFmiMonitor)
                .abductiveModel(new AbductiveModel("src/main/java/examples/abductiveBookModel.txt"))
                .modelData(abModelData)
                .encoder(new BookAbEncoder())
                .numberOfSteps(20)
                .simulationStepSize(1)
                .build();

        abductiveDriver.runSimulation();

        // Simple robot example
        ModelData md = Util.modelDataFromJson("simpleRobot.json");
        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .fmiMonitor(fmiMonitor)
                .model(new CbModel("src/main/java/examples/bookModel.txt"))
                .encoder(new BookCarEncoder())
                .modelData(md)
                .numberOfSteps(20)
                .simulationStepSize(1)
                .build();

        List<Scenario> scenarios = Util.scenariosFromJson("simpleScen.json");
        //consistencyDriver.runDiagnosis(ConsistencyType.INTERMITTENT, scenarios.get(0));

        FmiMonitor fmiMonitor1 = new FmiMonitor("FMIs/ExtendedRobot.Experminets.Driver.fmu");
        ModelData modelData = Util.modelDataFromJson("extendedRobot.json");
        //modelData.setPlot(new Pair<>("robot.diffDrive.x", "robot.diffDrive.y"));
        ConsistencyDriver extendedDriver = ConsistencyDriver.builder()
                .fmiMonitor(fmiMonitor1)
                .model(new CbModel("src/main/java/examples/extendedRobotModel.txt"))
                .encoder(new ExtendedRobotEncoder())
                .modelData(modelData)
                .numberOfSteps(20)
                .simulationStepSize(1)
                .build();

        List<Scenario> extendedScenarios = Util.scenariosFromJson("extendedScenarios.json");
        extendedDriver.runDiagnosis(ConsistencyType.STEP, extendedScenarios.get(2));
    }

}
