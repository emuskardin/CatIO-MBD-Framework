package runningExamples;

import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import FmiConnector.Type;
import abductive.AbductiveDriver;
import abductive.AbductiveModel;
import examples.BookAbEncoder;
import consistency.CbModel;
import consistency.ConsistencyDriver;
import examples.BookCarEncoder;

import java.util.Arrays;
import java.util.List;

public class DefaultMain {
    public static void main(String [] args) {

        String[] fmiPath = {"newFmi/ERobot.Experiments.RampInput.fmu", "newFmi/ERobot.Experiments.RampWFault.fmu",
                "newFmi/ERobot.Experiments.constWFault.fmu", "newFmi/ERobot.Experiments.RampIntermittent.fmu",
                "newFmi/ERobot.Experiments.ConstBothBreak.fmu"};
        FmiMonitor fmiMonitor = new FmiMonitor(fmiPath[4]);


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

        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .fmiMonitor(fmiMonitor)
                .model(new CbModel("src/main/java/examples/bookModel.txt"))
                .encoder(new BookCarEncoder())
                .comps(comps)
                .simulationRuntime(20)
                .stepSize(1)
                .build();

        consistencyDriver.stepDiag();
        //consistencyDriver.continuousDiag(true);
    }

}
