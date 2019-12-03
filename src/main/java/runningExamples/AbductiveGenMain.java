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


    }
}



//        String pathToFmi = "newFmi/ExtendedRobot.Experminets.Driver.fmu";
//        List<Object> wheelFaultTypes = Arrays.asList("ok", "faster", "slower", "stuck");
//        List<Object> batterylFaultTypes = Arrays.asList("ok", "empty");
//        List<Object> powerModuleFaultTypes = Arrays.asList("ok", "stuckAtClose", "stuckAtOpen");
//        List<Object> voltageRegFaultTypes = Arrays.asList("ok", "increased", "decreased");
//
//        ModelData modelData = new ModelData();
//        modelData.setHealthStates(
//                Arrays.asList(
//                        new ModelInput("dleftPowerModuleInput", powerModuleFaultTypes, Type.ENUM),
//                        new ModelInput("drightPowerModuleInput", powerModuleFaultTypes, Type.ENUM),
//                        new ModelInput("drightWheelInput", wheelFaultTypes, Type.ENUM),
//                        new ModelInput("dleftWheelInput", wheelFaultTypes, Type.ENUM),
//                        new ModelInput("dbatteryInput", batterylFaultTypes, Type.ENUM),
//                        new ModelInput("drightVoltageRegInput", voltageRegFaultTypes, Type.ENUM),
//                        new ModelInput("dleftVoltageRegInput", voltageRegFaultTypes, Type.ENUM)
//                ));
//
//        MLCA mlca = new MLCA(modelData);
//        mlca.numberOfCorrectComps(Arrays.asList(4));
//        mlca.createTestSuite("test");
//        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("test");
////
////