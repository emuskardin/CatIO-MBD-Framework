package consistency;

import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import consistency.mhsAlgs.RcTree;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import org.javafmi.wrapper.Simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ConsistencyDriver {
    FmiMonitor fmiMonitor;
    List<Component> comps;
    double stepSize;
    double simulationRuntime;
    CbModel model;
    Encoder encoder;

    /**
     * Diagnosis algorithm will be executed after every time step, and diagnosis printed to standard output.
     * @throws IOException
     */
    public void stepDiag() throws IOException {
        Simulation simulation = fmiMonitor.getSimulation();
        model.setNumOfDistinct(model.getPredicates().getSize());

        simulation.init(0.0);
        while (simulation.getCurrentTime() <= simulationRuntime){
            List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(comps));
            RcTree rcTree = new RcTree(model, model.observationToInt(obs));
            for(List<Integer> mhs  : rcTree.getDiagnosis())
                System.out.println(fmiMonitor.getSimulation().getCurrentTime() + " " + model.diagnosisToComponentNames(mhs));
            simulation.doStep(stepSize);
        }
        model.clearModel();
        fmiMonitor.resetSimulation();
    }

    /**
     * Diagnosis will be printed after simulation has ended. If faults are intermittent, and param is true
     * corresponding time steps in which faults have been diagnosed will be printed to standard output.
     * @param intermittentFaults it true, health states will be increased alongside other param
     * @throws IOException
     */
    public void continuousDiag(Boolean intermittentFaults) throws IOException {
        Simulation simulation = fmiMonitor.getSimulation();
        int offset = intermittentFaults ? model.getPredicates().getSize() : (model.getPredicates().getSize() - (model.getAbPredicates().size()/2));

        List<Integer> observations = new ArrayList<>();
        int currStep = 0;

        simulation.init(0.0);
        while(simulation.getCurrentTime() <= simulationRuntime){
            List<Integer> encodedObs = model.observationToInt(encoder.encodeObservation(fmiMonitor.readMultiple(comps)));
            observations.addAll(increaseObservation(encodedObs, currStep, offset));
            model.increaseByOffset(intermittentFaults, currStep);
            simulation.doStep(stepSize);
            currStep++;
        }

        if(intermittentFaults)
            model.setNumOfDistinct(offset * currStep);
        else
            model.setNumOfDistinct(model.getAbPredicates().size() / 2 + ((offset ) * currStep));
        
        RcTree rcTree = new RcTree(model, observations);
        for(List<Integer> mhs  : rcTree.getDiagnosis())
            System.out.println(model.getComponentNamesTimed(mhs, intermittentFaults));

        model.clearModel();
        fmiMonitor.resetSimulation();
    }

    private List<Integer> increaseObservation(List<Integer> obs, int currStep, int offset){
        for (int i = 0; i < obs.size(); i++) {
            Integer increasedOb = obs.get(i) > 0 ? obs.get(i)  + (offset * currStep) : obs.get(i) - (offset * currStep);
            obs.set(i, increasedOb);
        }
        return obs;
    }

}
