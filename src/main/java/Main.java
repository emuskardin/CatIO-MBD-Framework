import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import FmiConnector.TYPE;
import abductive.combinatorial.ModelInputData;
import abductive.combinatorial.MLCA;
import abductive.combinatorial.ModelData;
import com.github.plot.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String [] args) throws IOException {
        String pathToFmi = "newFmi/ExtendedRobot.Experminets.Driver.fmu";
        List<Object> wheelFaultTypes = Arrays.asList("ok", "faster", "slower", "stuck");
        List<Object> batterylFaultTypes = Arrays.asList("ok", "empty");
        List<Object> powerModuleFaultTypes = Arrays.asList("ok", "stuckAtClose", "stuckAtOpen");
        List<Object> voltageRegFaultTypes = Arrays.asList("ok", "increased", "decreased");

        ModelData modelData = new ModelData();
        modelData.setPathToFmi("test");
        modelData.setComponents(
                Arrays.asList(
                        new ModelInputData("dleftPowerModuleInput",powerModuleFaultTypes, TYPE.ENUM),
                        new ModelInputData("drightPowerModuleInput", powerModuleFaultTypes, TYPE.ENUM),
                        new ModelInputData("drightWheelInput", wheelFaultTypes, TYPE.ENUM),
                        new ModelInputData("dleftWheelInput", wheelFaultTypes, TYPE.ENUM),
                        new ModelInputData("dbatteryInput", batterylFaultTypes, TYPE.ENUM),
                        new ModelInputData("drightVoltageRegInput", voltageRegFaultTypes, TYPE.ENUM),
                        new ModelInputData("dleftVoltageRegInput", voltageRegFaultTypes, TYPE.ENUM)
                ));

        MLCA mlca = new MLCA(modelData);
        mlca.numberOfCorrectComps(Arrays.asList(4));
        mlca.createTestSuite("test");
        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("test");

        FmiMonitor fmiMonitor = new FmiMonitor(pathToFmi);
        Component cx = new Component("robot.diffDrive.x", TYPE.DOUBLE);
        Component cy = new Component("robot.diffDrive.y", TYPE.DOUBLE);
        Component te = new Component("robot.leftPowerModuleInput", TYPE.INTEGER);

        Integer i = 1;
        for(List<Component> test :simulationInputs){
            ArrayList<Double> x = new ArrayList<>();
            ArrayList<Double> y = new ArrayList<>();
            fmiMonitor.resetSimulation();
            fmiMonitor.getSimulation().init(0.0);
            while (fmiMonitor.getSimulation().getCurrentTime() <= 20){
                fmiMonitor.getSimulation().doStep(0.5);
                if(fmiMonitor.getSimulation().getCurrentTime() == 10)
                    fmiMonitor.getFmiWriter().writeMultipleComp(test);

                x.add((Double) fmiMonitor.read(cx).getValue());
                y.add((Double) fmiMonitor.read(cy).getValue());
            }

            Util.plot(x, y, "imgs/" + i.toString());
            i++;

        }

//        String[] fmiPath = {"newFmi/ERobot.Experiments.RampInput.fmu", "newFmi/ERobot.Experiments.RampWFault.fmu",
//                "newFmi/ERobot.Experiments.constWFault.fmu", "newFmi/ERobot.Experiments.RampIntermittent.fmu",
//                "newFmi/ERobot.Experiments.ConstBothBreak.fmu"};
//        FmiMonitor fmiMonitor = new FmiMonitor(fmiPath[4]);
//        List<Component> comps = Arrays.asList(
//                new Component("robot.rightWheel.i", TYPE.DOUBLE),
//                new Component("robot.rightWheel.o", TYPE.DOUBLE),
//                new Component("robot.leftWheel.i", TYPE.DOUBLE),
//                new Component("robot.leftWheel.o", TYPE.DOUBLE)
//        );
//
//        AbModelEncoderContract abCar = new BookAbExample();
//        AbductiveDriver abductiveDriver = AbductiveDriver.builder()
//                .fmiMonitor(fmiMonitor)
//                .abModelEncoderContract(abCar)
//                .comps(comps)
//                .stepSize(1)
//                .simulationRuntime(20)
//                .build();
//
//        abductiveDriver.runSimulation();

//        CbModelEncoderContract car = new SimplerCarModel();
//        ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
//                .fmiMonitor(fmiMonitor)
//                .cbModelEncoderContract(car)
//                .comps(comps)
//                .simulationRuntime(20)
//                .stepSize(1)
//                .build();
//
//        consistencyDriver.stepDiag();
//        consistencyDriver.continuousDiag(true);

    }
}