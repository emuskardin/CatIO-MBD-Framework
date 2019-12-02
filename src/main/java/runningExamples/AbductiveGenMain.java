package runningExamples;

import model.Component;
import model.Type;
import abductive.MLCA;
import model.ModelData;
import model.ModelInput;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AbductiveGenMain {
    public static void main(String[] args) throws IOException {

//        Util util = new Util();
//        ExtractedData re = util.deserialize("extractedData.ser");
//
//        String pathToFmi = "newFmi/SC_Example1.fmu";
//
//
//        List<Object> bateryFaultTypes = Arrays.asList("ok", "empty");
//        List<Object> componentFaultTypes = Arrays.asList("ok", "short", "broken");
//        ModelData modelData = new ModelData();
//        modelData.setPathToFmi(pathToFmi);
//        modelData.setComponents(
//                Arrays.asList(
//                    new ModelInputData("r1State", bateryFaultTypes, TYPE.ENUM),
//                    new ModelInputData("bat1State", componentFaultTypes, TYPE.ENUM),
//                    new ModelInputData("b1State", componentFaultTypes, TYPE.ENUM),
//                    new ModelInputData("rLoadState", componentFaultTypes, TYPE.ENUM),
//                    new ModelInputData("rIntState", componentFaultTypes, TYPE.ENUM)
//                ));
//
//        AutomaticModelGen automaticModelGen = new AutomaticModelGen(pathToFmi, modelData);
//        AbductiveModel ab = automaticModelGen.generateModel();
//        ab.addExplain(Collections.singletonList("bulbOff"));
//        System.out.println(ab.getDiagnosis());
//
//    }
//}


        String pathToFmi = "newFmi/ExtendedRobot.Experminets.Driver.fmu";
        List<Object> wheelFaultTypes = Arrays.asList("ok", "faster", "slower", "stuck");
        List<Object> batterylFaultTypes = Arrays.asList("ok", "empty");
        List<Object> powerModuleFaultTypes = Arrays.asList("ok", "stuckAtClose", "stuckAtOpen");
        List<Object> voltageRegFaultTypes = Arrays.asList("ok", "increased", "decreased");

        ModelData modelData = new ModelData();
        modelData.setHealthStates(
                Arrays.asList(
                        new ModelInput("dleftPowerModuleInput", powerModuleFaultTypes, Type.ENUM),
                        new ModelInput("drightPowerModuleInput", powerModuleFaultTypes, Type.ENUM),
                        new ModelInput("drightWheelInput", wheelFaultTypes, Type.ENUM),
                        new ModelInput("dleftWheelInput", wheelFaultTypes, Type.ENUM),
                        new ModelInput("dbatteryInput", batterylFaultTypes, Type.ENUM),
                        new ModelInput("drightVoltageRegInput", voltageRegFaultTypes, Type.ENUM),
                        new ModelInput("dleftVoltageRegInput", voltageRegFaultTypes, Type.ENUM)
                ));

        MLCA mlca = new MLCA(modelData);
        mlca.numberOfCorrectComps(Arrays.asList(4));
        mlca.createTestSuite("test");
        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("test");
//
//        FmiMonitor fmiMonitor = new FmiMonitor(pathToFmi);
//        Component cx = new Component("robot.diffDrive.x", TYPE.DOUBLE);
//        Component cy = new Component("robot.diffDrive.y", TYPE.DOUBLE);
//        Component te = new Component("robot.leftPowerModuleInput", TYPE.INTEGER);
//
//        Integer i = 1;
//        for (List<Component> test : simulationInputs) {
//            ArrayList<Double> x = new ArrayList<>();
//            ArrayList<Double> y = new ArrayList<>();
//            fmiMonitor.resetSimulation();
//            Simulation sim = fmiMonitor.getSimulation();
//            fmiMonitor.getFmiWriter().writeMultipleComp(mlca.getAllOkStates());
//            sim.init(0.0);
//            while (sim.getCurrentTime() <= 20) {
//                sim.doStep(1);
//                if (sim.getCurrentTime() == 10)
//                    fmiMonitor.getFmiWriter().writeMultipleComp(test);
//
//                x.add((Double) fmiMonitor.read(cx).getValue());
//                y.add((Double) fmiMonitor.read(cy).getValue());
//            }
//
//            Util.plot(x, y, "imgs/" + i.toString());
//            i++;
//
//        }
    }
}