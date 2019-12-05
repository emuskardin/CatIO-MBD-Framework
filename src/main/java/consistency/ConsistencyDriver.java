package consistency;

import FmiConnector.FmiMonitor;
import model.ModelData;
import consistency.mhsAlgs.RcTree;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import model.Scenario;
import org.javafmi.wrapper.Simulation;

import java.util.*;

@Data
@Builder
public class ConsistencyDriver {
    FmiMonitor fmiMonitor;
    ModelData modelData;
    double stepSize;
    Integer numberOfSteps;
    CbModel model;
    Encoder encoder;

    /**
     * Diagnosis algorithm will be executed after every time step, and diagnosis printed to standard output.
     */
    public void runDiagnosis(ConsistencyType type, Scenario scenario){
        Simulation simulation = fmiMonitor.getSimulation();
        Integer currStep = 1;

        simulation.init(0.0);
        if(type == ConsistencyType.STEP){
            model.setNumOfDistinct(model.getPredicates().getSize());
            while (currStep <= numberOfSteps){
                if(scenario != null)
                    scenario.injectFault(currStep, fmiMonitor.getFmiWriter(), modelData);

                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                RcTree rcTree = new RcTree(model, model.observationToInt(obs));
                String diag = "";
                for(List<Integer> mhs  : rcTree.getDiagnosis())
                    diag += model.diagnosisToComponentNames(mhs);
                System.out.println("Step " + currStep + " : " + diag);
                simulation.doStep(stepSize);
                currStep++;
            }
        }else if(type == ConsistencyType.PERSISTENT || type == ConsistencyType.INTERMITTENT){
            Set<Integer> originalAbPred = new HashSet<>(model.getAbPredicates());
            boolean increaseHs = (type == ConsistencyType.INTERMITTENT);
            int offset = increaseHs ? model.getPredicates().getSize() : (model.getPredicates().getSize() - model.getAbPredicates().size());
            List<Integer> observations = new ArrayList<>();

            while(currStep <= numberOfSteps){
                if(scenario != null)
                    scenario.injectFault(currStep, fmiMonitor.getFmiWriter(), modelData);

                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                List<Integer> encodedObs = model.observationToInt(obs);
                observations.addAll(increaseObservation(encodedObs, currStep, offset));
                model.increaseByOffset(increaseHs, currStep);
                simulation.doStep(stepSize);
                currStep++;
            }

            if(increaseHs)
                model.setNumOfDistinct(offset * currStep);
            else
                model.setNumOfDistinct(model.getAbPredicates().size() + ((offset ) * currStep));

            RcTree rcTree = new RcTree(model, observations);
            for(List<Integer> mhs  : rcTree.getDiagnosis())
                System.out.println(model.getComponentNamesTimed(mhs, increaseHs));

            model.setAbPredicates(originalAbPred);
        }

        model.clearModel();
        fmiMonitor.resetSimulation();
    }

    public void runDiagnosis(ConsistencyType type){
        runDiagnosis(type, null);
    }

    private List<Integer> increaseObservation(List<Integer> obs, int currStep, int offset){
        for (int i = 0; i < obs.size(); i++) {
            Integer increasedOb = obs.get(i) > 0 ? obs.get(i)  + (offset * currStep) : obs.get(i) - (offset * currStep);
            obs.set(i, increasedOb);
        }
        return obs;
    }
}
