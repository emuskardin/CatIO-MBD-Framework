import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import FmiConnector.TYPE;
import abductive.AbductiveModel;
import abductive.AutomaticModelGen;
import abductive.combinatorial.MLCA;
import abductive.combinatorial.ModelData;
import abductive.combinatorial.ModelInputData;
import org.javafmi.wrapper.Simulation;
import util.ExtractedData;
import util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        modelData.setComponents(
                Arrays.asList(
                        new ModelInputData("dleftPowerModuleInput", powerModuleFaultTypes, TYPE.ENUM),
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