package runningExamples;

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
import util.Util;

import java.util.Arrays;
import java.util.List;

public class DefaultMain {
    public static void main(String [] args) {


        String[] fmiPath = {"FMIs/ERobot.Experiments.RampInput.fmu", "FMIs/ERobot.Experiments.RampWFault.fmu",
                "FMIs/ERobot.Experiments.constWFault.fmu", "FMIs/ERobot.Experiments.RampIntermittent.fmu",
                "FMIs/ERobot.Experiments.ConstBothBreak.fmu", "FMIs/ERobot.SubModel.InputSimpleRobot.fmu"};
        FmiMonitor fmiMonitor = new FmiMonitor(fmiPath[5]);

        List<Component> comps = Arrays.asList(
                new Component("robot.rightWheel.i", Type.DOUBLE),
                new Component("robot.rightWheel.o", Type.DOUBLE),
                new Component("robot.leftWheel.i", Type.DOUBLE),
                new Component("robot.leftWheel.o", Type.DOUBLE)
        );

        AbductiveDriver abductiveDriver = AbductiveDriver.builder()
                .fmiMonitor(fmiMonitor)
                .abductiveModel(new AbductiveModel("src/main/java/examples/abductiveBookModel.txt"))
                .encoder(new BookAbEncoder())
                .comps(comps)
                .stepSize(1)
                .simulationRuntime(20)
                .build();

        //abductiveDriver.runSimulation();

        ModelData md = Util.modelDataFromJson("simpleRobot.json");
        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .fmiMonitor(fmiMonitor)
                .model(new CbModel("src/main/java/examples/bookModel.txt"))
                .encoder(new BookCarEncoder())
                .modelData(md)
                .simulationRuntime(20)
                .stepSize(1)
                .build();

        List<Scenario> scenarios = Util.scenariosFromJson("simpleScen.json");

        consistencyDriver.runDiagnosis(ConsistencyType.STEP, scenarios.get(0));

        //consistencyDriver.runDiagnosis(ConsistencyType.PERSISTENT);
        //consistencyDriver.runDiagnosis(ConsistencyType.INTERMITTENT);

    }

}
