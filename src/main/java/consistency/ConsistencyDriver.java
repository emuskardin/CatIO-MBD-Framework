package consistency;

import FmiConnector.FmiMonitor;
import model.ModelData;
import consistency.mhsAlgs.RcTree;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import model.Scenario;
import org.javafmi.wrapper.Simulation;
import util.Util;

import java.util.*;

@Data
@Builder
public class ConsistencyDriver {
    String pathToFmi;
    ModelData modelData;
    double simulationStepSize;
    Integer numberOfSteps;
    CbModel model;
    Encoder encoder;

    /**
     * Diagnosis algorithm will be executed after every time step, and diagnosis printed to standard output.
     */
    public void runDiagnosis(ConsistencyType type, Scenario scenario){
        FmiMonitor fmiMonitor = new FmiMonitor(pathToFmi);
        ArrayList<Double> xCoor = new ArrayList<>();
        ArrayList<Double> yCoor = new ArrayList<>();

        Simulation simulation = fmiMonitor.getSimulation();
        Integer currStep = 0;

        simulation.init(0.0);
        if(type == ConsistencyType.STEP){
            model.setNumOfDistinct(model.getPredicates().getSize());
            while (currStep < numberOfSteps){
                if(scenario != null)
                    scenario.injectFault(currStep, fmiMonitor.getFmiWriter(), modelData);

                if(modelData.getPlot() != null){
                    xCoor.add((Double) fmiMonitor.read(modelData.getToReadByName(modelData.getPlot().left)).getValue());
                    yCoor.add((Double) fmiMonitor.read(modelData.getToReadByName(modelData.getPlot().right)).getValue());
                }

                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                RcTree rcTree = new RcTree(model, model.observationToInt(obs));
                String diag = "";
                for(List<Integer> mhs  : rcTree.getDiagnosis())
                    diag += model.diagnosisToComponentNames(mhs);
                System.out.println("Step " + currStep + " : " + diag);
                simulation.doStep(simulationStepSize);
                currStep++;
            }
        }else if(type == ConsistencyType.PERSISTENT || type == ConsistencyType.INTERMITTENT){
            Set<Integer> originalAbPred = new HashSet<>(model.getAbPredicates());
            boolean increaseHs = (type == ConsistencyType.INTERMITTENT);
            int offset = increaseHs ? model.getPredicates().getSize() : (model.getPredicates().getSize() - model.getAbPredicates().size());
            List<Integer> observations = new ArrayList<>();

            while(currStep < numberOfSteps){
                if(scenario != null)
                    scenario.injectFault(currStep , fmiMonitor.getFmiWriter(), modelData);

                if(modelData.getPlot() != null){
                    xCoor.add((Double) fmiMonitor.read(modelData.getToReadByName(modelData.getPlot().left)).getValue());
                    yCoor.add((Double) fmiMonitor.read(modelData.getToReadByName(modelData.getPlot().right)).getValue());
                }

                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                List<Integer> encodedObs = model.observationToInt(obs);
                observations.addAll(increaseObservation(encodedObs, currStep, offset));
                model.increaseByOffset(increaseHs, currStep);
                simulation.doStep(simulationStepSize);
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
        if(modelData.getPlot() != null)
            Util.plot(xCoor, yCoor, "plot");
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
