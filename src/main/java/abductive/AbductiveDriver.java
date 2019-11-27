package abductive;

import FmiConnector.Component;
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
    double simulationRuntime;
    AbductiveModel abductiveModel;
    Encoder encoder;

    public void runSimulation() {
        Simulation simulation = fmiMonitor.getSimulation();
        simulation.init(0);

        while (simulation.getCurrentTime() < simulationRuntime){
            simulation.doStep(stepSize);
            List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(comps));
            abductiveModel.addExplain(obs);
            System.out.println(simulation.getCurrentTime() + " " + abductiveModel.getDiagnosis());
        }
    }
}
