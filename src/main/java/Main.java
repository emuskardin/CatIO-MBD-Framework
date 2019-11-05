import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import FmiConnector.TYPE;
import abductive.AbModelEncoderContract;
import abductive.AbductiveDriver;
import abductive.combinatorial.CombCompData;
import abductive.combinatorial.MLCA;
import abductive.combinatorial.ModelData;
import abductive.examples.BookAbExample;
import consistency.examples.BookCarModel;
import consistency.stepFaultDiag.CbModelEncoderContract;
import consistency.ConsistencyDriver;
import consistency.examples.SimplerCarModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String [] args) throws IOException {
//
//        ModelData modelData = new ModelData();
//        modelData.setPathToFmi("test");
//        modelData.setComponents(
//                Arrays.asList(
//                        new CombCompData("bat", Arrays.asList("ok", "empty"), TYPE.STRING),
//                        new CombCompData("resistor", Arrays.asList("ok", "short", "broken"), TYPE.STRING),
//                        new CombCompData("bulb", Arrays.asList("ok", "broken"), TYPE.STRING))
//        );
//        modelData.setParam(
//                Arrays.asList(
//                        new CombCompData("p1", Arrays.asList(1,2,3,4), TYPE.INTEGER),
//                        new CombCompData("p1", Arrays.asList(5,6,7), TYPE.INTEGER)
//                )
//        );
//
//        MLCA mlca = new MLCA(modelData);
//        mlca.numberOfCorrectComps(Arrays.asList(2));
//        System.out.println(mlca.getSut().toString());
//        mlca.createTestSuite("test");

        String[] fmiPath = {"newFmi/ERobot.Experiments.RampInput.fmu", "newFmi/ERobot.Experiments.RampWFault.fmu",
                "newFmi/ERobot.Experiments.constWFault.fmu", "newFmi/ERobot.Experiments.RampIntermittent.fmu",
                "newFmi/ERobot.Experiments.ConstBothBreak.fmu"};
        FmiMonitor fmiMonitor = new FmiMonitor(fmiPath[4]);
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