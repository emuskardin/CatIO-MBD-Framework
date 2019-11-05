package abductive;

import FmiConnector.Component;
import FmiConnector.FmiMonitor;
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
    AbModelEncoderContract abModelEncoderContract;

    public void runSimulation() {
        abModelEncoderContract.constructModel();
        Simulation simulation = fmiMonitor.getSimulation();
        simulation.init(0);

        while (simulation.getCurrentTime() < simulationRuntime){
            AbductiveModel abModel = abModelEncoderContract.getModel();
            simulation.doStep(stepSize);
            List<String> obs = abModelEncoderContract.encodeObservations(fmiMonitor.readMultiple(comps));
            abModel.addExplain(obs);
            System.out.println(simulation.getCurrentTime() + " " + abModel.getDiagnosis());
        }
    }
}
