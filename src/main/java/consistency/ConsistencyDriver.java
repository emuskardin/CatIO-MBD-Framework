package consistency;

import FmiConnector.FmiMonitor;
import interfaces.Controller;
import model.Component;
import model.ModelData;
import consistency.mhsAlgs.RcTree;
import interfaces.Encoder;
import lombok.Builder;
import lombok.Data;
import model.Scenario;
import org.apache.commons.lang3.Pair;
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

        ArrayList<Double> xPlot = new ArrayList<>();
        ArrayList<Double> yPlot = new ArrayList<>();
        Pair<Component, Component> plotData = modelData.getPlot();
        Simulation simulation = fmiMonitor.getSimulation();
        Integer currStep = 0;

        Controller controller = modelData.getController();

        simulation.init(0.0);
        if(type == ConsistencyType.STEP){
            model.setNumOfDistinct(model.getPredicates().getSize());
            while (currStep < numberOfSteps) {
                // If there is scenario and step injection is defined at current step, it will be injected at this point
                if (scenario != null)
                    scenario.injectFault(currStep, fmiMonitor.getFmiWriter(), modelData);

                // Save data which is going to be plotted, if plot variables are defined
                if (plotData != null) {
                    xPlot.add((Double) fmiMonitor.read(plotData.left).getValue());
                    yPlot.add((Double) fmiMonitor.read(plotData.right).getValue());
                }

                // Read data and encode it
                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                // Run diagnosis
                RcTree rcTree = new RcTree(model, model.observationToInt(obs));

                // For each step print diagnosis
                List<List<String>> diag = new ArrayList<>();
                for (List<Integer> mhs : rcTree.getDiagnosis())
                    diag.add(model.diagnosisToComponentNames(mhs));
                System.out.println("Step " + currStep + " : " + diag);

                // do step
                simulation.doStep(simulationStepSize);
                currStep++;

                // If controller is defined, perform action
                if(controller != null && !diag.get(0).isEmpty()){
                    controller.performAction(fmiMonitor.getFmiWriter(), diag.get(0));
                }
            }
            }else if(type == ConsistencyType.PERSISTENT || type == ConsistencyType.INTERMITTENT){
            Set<Integer> originalAbPred = new HashSet<>(model.getAbPredicates());
            int obsCounter = 0;
            boolean increaseHs = (type == ConsistencyType.INTERMITTENT);
            int offset = increaseHs ? model.getPredicates().getSize() : (model.getPredicates().getSize() - model.getAbPredicates().size());
            List<Integer> observations = new ArrayList<>();

            while(currStep < numberOfSteps){
                if(scenario != null)
                    scenario.injectFault(currStep , fmiMonitor.getFmiWriter(), modelData);

                if(modelData.getPlot() != null){
                    xPlot.add((Double) fmiMonitor.read(modelData.getPlot().left).getValue());
                    yPlot.add((Double) fmiMonitor.read(modelData.getPlot().right).getValue());
                }

                List<String> obs = encoder.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                obsCounter = obs.size();
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
            List<List<Integer>> mhs = rcTree.getDiagnosis();
            for (List<Integer> hs : mhs)
                System.out.println(model.getComponentNamesTimed(hs, type, obsCounter));


            // abPredicates were updated, so revert them back to original to enable reuse
            model.setAbPredicates(originalAbPred);
        }

        // if plot values are specified
        if(modelData.getPlot() != null)
            Util.plot(xPlot, yPlot, "plot");

        // for reuse, clear and reset members of this class
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
