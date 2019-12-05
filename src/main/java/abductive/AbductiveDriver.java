package abductive;

import model.Component;
import FmiConnector.FmiMonitor;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import org.javafmi.wrapper.Simulation;

import java.util.List;

@Data
@Builder
public class AbductiveDriver {
    FmiMonitor fmiMonitor;
    List<Component> comps;
    double stepSize;
    Integer numberOfSteps;
    AbductiveModel abductiveModel;
    Encoder encoder;

    public void runSimulation() {
        Simulation simulation = fmiMonitor.getSimulation();
        simulation.init(0);
        Integer currStep = 1;

        while (currStep <= numberOfSteps){
            List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(comps));
            abductiveModel.addExplain(obs);
            System.out.println(simulation.getCurrentTime() + " " + abductiveModel.getDiagnosis());
            simulation.doStep(stepSize);
            currStep++;
        }
    }
}
