package consistency;

import FmiConnector.FmiMonitor;
import model.Component;
import model.ModelData;
import consistency.mhsAlgs.RcTree;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import model.Scenario;
import model.Type;
import org.javafmi.wrapper.Simulation;

import java.util.*;

@Data
@Builder
public class ConsistencyDriver {
    FmiMonitor fmiMonitor;
    ModelData modelData;
    double stepSize;
    double simulationRuntime;
    CbModel model;
    Encoder encoder;

    /**
     * Diagnosis algorithm will be executed after every time step, and diagnosis printed to standard output.
     */
    public void runDiagnosis(ConsistencyType type, Scenario scenario){
        Simulation simulation = fmiMonitor.getSimulation();
        List<Component> db = new ArrayList<>();
        db.add(new Component("leftFaultType", Type.INTEGER));
        db.add(new Component("rightFaultType", Type.INTEGER));

        simulation.init(0.0);
        if(type == ConsistencyType.STEP){
            model.setNumOfDistinct(model.getPredicates().getSize());
            while (simulation.getCurrentTime() <= simulationRuntime){
                scenario.injectFault(simulation.getCurrentTime(), fmiMonitor.getFmiWriter(), modelData);
                //System.out.println(fmiMonitor.readMultiple(db));

                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                RcTree rcTree = new RcTree(model, model.observationToInt(obs));
                for(List<Integer> mhs  : rcTree.getDiagnosis())
                    System.out.println(fmiMonitor.getSimulation().getCurrentTime() + " " + model.diagnosisToComponentNames(mhs));
                simulation.doStep(stepSize);
            }
        }else if(type == ConsistencyType.PERSISTENT || type == ConsistencyType.INTERMITTENT){
            Set<Integer> abPredTmp = new HashSet<>(model.getAbPredicates());
            boolean increaseHs = (type == ConsistencyType.INTERMITTENT);
            int offset = increaseHs ? model.getPredicates().getSize() : (model.getPredicates().getSize() - (model.getAbPredicates().size()/2));
            List<Integer> observations = new ArrayList<>();
            int currStep = 0;

            while(simulation.getCurrentTime() <= simulationRuntime){
                scenario.injectFault(simulation.getCurrentTime(), fmiMonitor.getFmiWriter(), modelData);

                List<Integer> encodedObs = model.observationToInt(encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead())));
                observations.addAll(increaseObservation(encodedObs, currStep, offset));
                model.increaseByOffset(increaseHs, currStep);
                simulation.doStep(stepSize);
                currStep++;
            }

            if(increaseHs)
                model.setNumOfDistinct(offset * currStep);
            else
                model.setNumOfDistinct(model.getAbPredicates().size() / 2 + ((offset ) * currStep));

            RcTree rcTree = new RcTree(model, observations);
            for(List<Integer> mhs  : rcTree.getDiagnosis())
                System.out.println(model.getComponentNamesTimed(mhs, increaseHs));

            model.setAbPredicates(abPredTmp);
        }

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
