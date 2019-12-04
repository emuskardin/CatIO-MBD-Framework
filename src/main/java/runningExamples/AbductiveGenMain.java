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
        String pathToFmi = "newFmi/ExtendedRobot.Experminets.Driver.fmu";
        List<Object> wheelFaultTypes = Arrays.asList("ok", "faster", "slower", "stuck");
        List<Object> batterylFaultTypes = Arrays.asList("ok", "empty");
        List<Object> powerModuleFaultTypes = Arrays.asList("ok", "stuckAtClose", "stuckAtOpen");
        List<Object> voltageRegFaultTypes = Arrays.asList("ok", "increased", "decreased");

        ModelData modelData = new ModelData();
        modelData.setHealthStates(
                Arrays.asList(
                        new ModelInput("dleftPowerModule.Input", Type.ENUM, powerModuleFaultTypes),
                        new ModelInput("drightPowerModule.Input", Type.ENUM, powerModuleFaultTypes),
                        new ModelInput("drightWhe.el.Inp.ut", Type.ENUM, wheelFaultTypes),
                        new ModelInput("dleftWhe.el.Input", Type.ENUM, wheelFaultTypes),
                        new ModelInput("dbatteryI.nput", Type.ENUM, batterylFaultTypes),
                        new ModelInput("drightVolt.ageRegInput", Type.ENUM, voltageRegFaultTypes),
                        new ModelInput("dleftVoltag.eRegInput", Type.ENUM, voltageRegFaultTypes)
                ));

        MLCA mlca = new MLCA(modelData);
        mlca.numberOfCorrectComps(Arrays.asList(4));
        mlca.createTestSuite("test");
        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("test");

    }
}
