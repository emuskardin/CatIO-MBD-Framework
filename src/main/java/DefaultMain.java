import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import FmiConnector.TYPE;
import abductive.AbModelEncoderContract;
import abductive.AbductiveDriver;
import abductive.examples.BookAbExample;
import consistency.ConsistencyDriver;
import consistency.examples.SimplerCarModel;
import consistency.stepFaultDiag.CbModelEncoderContract;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DefaultMain {
    public static void main(String [] args) throws IOException {
        String[] fmiPath = {"newFmi/ERobot.Experiments.RampInput.fmu", "newFmi/ERobot.Experiments.RampWFault.fmu",
                "newFmi/ERobot.Experiments.constWFault.fmu", "newFmi/ERobot.Experiments.RampIntermittent.fmu",
                "newFmi/ERobot.Experiments.ConstBothBreak.fmu"};
        FmiMonitor fmiMonitor = new FmiMonitor(fmiPath[3]);


        List<Component> comps = Arrays.asList(
                new Component("robot.rightWheel.i", TYPE.DOUBLE),
                new Component("robot.rightWheel.o", TYPE.DOUBLE),
                new Component("robot.leftWheel.i", TYPE.DOUBLE),
                new Component("robot.leftWheel.o", TYPE.DOUBLE)
        );

        AbModelEncoderContract abCar = new BookAbExample();
        AbductiveDriver abductiveDriver = AbductiveDriver.builder()
                .fmiMonitor(fmiMonitor)
                .abModelEncoderContract(abCar)
                .comps(comps)
                .stepSize(1)
                .simulationRuntime(20)
                .build();

        abductiveDriver.runSimulation();

        CbModelEncoderContract car = new SimplerCarModel();
        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .fmiMonitor(fmiMonitor)
                .cbModelEncoderContract(car)
                .comps(comps)
                .simulationRuntime(20)
                .stepSize(1)
                .build();

        consistencyDriver.stepDiag();
        consistencyDriver.continuousDiag(true);
    }
}
