package abductive.combinatorial;

import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import consistency.CbModel;
import consistency.mhsAlgs.RcTree;
import interfaces.Encoder;
import org.javafmi.wrapper.Simulation;

import java.util.List;

enum ConsistencyType{
    STEP,
    PERSISTENT,
    INTERMITTENT,
    REPAIR
}

public class SimulationRunner {
    private String pathToFmi;
    private ModelData modelData;
    private FmiMonitor fmiMonitor;

    public SimulationRunner(String pathToFmi, ModelData modelData){
        this.pathToFmi = pathToFmi;
        this.modelData = modelData;
        fmiMonitor = new FmiMonitor(pathToFmi);
    }

    public void runSimulation(Double simulationRuntime, CbModel model, Encoder encoder, List<Component> comps){
        Simulation simulation = fmiMonitor.getSimulation();

        simulation.init(0.0);
        while (simulation.getCurrentTime() <= simulationRuntime){
            List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(comps));
            RcTree rcTree = new RcTree(model, model.observationToInt(obs));
            for(List<Integer> mhs  : rcTree.getDiagnosis())
                System.out.println(fmiMonitor.getSimulation().getCurrentTime() + " " + model.diagnosisToComponentNames(mhs));
            simulation.doStep(1);
        }
        //model.clearModel();
        fmiMonitor.resetSimulation();
    }

}
