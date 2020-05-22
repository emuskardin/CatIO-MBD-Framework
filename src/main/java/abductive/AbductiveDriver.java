package abductive;

import FmiConnector.FmiConnector;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import model.ModelData;
import model.Scenario;
import org.javafmi.wrapper.Simulation;

import java.util.List;

@Data
@Builder
public class AbductiveDriver {
    String pathToFmi;
    ModelData modelData;
    double simulationStepSize;
    Integer numberOfSteps;
    AbductiveModel abductiveModel;
    Encoder encoder;

    public void runSimulation(){
        runSimulation(null);
    }

    public void runSimulation(Scenario scenario) {
        FmiConnector fmiMonitor = new FmiConnector(pathToFmi);
        Simulation simulation = fmiMonitor.getSimulation();
        simulation.init(0);
        Integer currStep = 0;

        while (currStep < numberOfSteps){
            if(scenario != null)
                scenario.injectFault(currStep, fmiMonitor);
            List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
            abductiveModel.tryToExplain(obs);
            System.out.println(simulation.getCurrentTime() + " " + abductiveModel.getDiagnosis());
            simulation.doStep(simulationStepSize);
            currStep++;
        }
    }
}
