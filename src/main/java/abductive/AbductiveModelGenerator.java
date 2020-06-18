package abductive;

import FmiConnector.FmiConnector;
import interfaces.Diff;
import interfaces.Encoder;
import model.Component;
import model.ModelData;
import lombok.Data;
import model.ModelInput;
import model.Scenario;
import org.javafmi.wrapper.Simulation;
import model.SimulationRunData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class AbductiveModelGenerator {
    private ModelData modelData;
    private FmiConnector fmiConnector;
    private MCA MCA;
    private AbductiveModel abductiveModel;
    private Diff diff;
    private Encoder enc;
    private String pathToMCA;

    public AbductiveModelGenerator(String pathToFmi , ModelData modelData, Diff diff){
        abductiveModel = new AbductiveModel();
        this.modelData = modelData;
        MCA = new MCA(modelData);
        fmiConnector = new FmiConnector(pathToFmi);
        this.diff = diff;

    }

    public void setEncoder(Encoder enc){
        this.enc = enc;
    }

    public void setMCASuite(String pathToMCA){
        this.pathToMCA = pathToMCA;
    }

    public AbductiveModel generateModel(Integer numberOfSteps, Double stepSize, Integer faultInjectionStep) throws IOException {
        MCA.createTestSuite("automaticModelGen.csv");
        List<Scenario> simulationInputs = MCA.suitToSimulationInput("automaticModelGen.csv", faultInjectionStep);

        for(Scenario scenario : simulationInputs){
            fmiConnector.resetSimulation();
            Simulation sim = fmiConnector.getSimulation();

            SimulationRunData corrSimulationRunData = new SimulationRunData();
            SimulationRunData faultySimulationRunData = new SimulationRunData();
            // Setup params and inputs as well as all health states to ok
            fmiConnector.writeMultipleComp(scenario.getTimeCompMap().values().iterator().next());
            fmiConnector.writeMultipleComp(modelData.getAllOkStates());
            sim.init(0.0);
            int stepCounter = 0;
            while(stepCounter <= numberOfSteps){
                if(enc == null)
                    corrSimulationRunData.addValues(fmiConnector.readMultiple(modelData.getComponentsToRead()));
                else
                    corrSimulationRunData.addPredicates(new ArrayList<>((enc.encodeObservation(fmiConnector.readMultiple(modelData.getComponentsToRead())))));

                sim.doStep(stepSize);
                stepCounter++;
            }

            fmiConnector.resetSimulation();
            sim = fmiConnector.getSimulation();
            fmiConnector.writeMultipleComp(scenario.getTimeCompMap().values().iterator().next());
            fmiConnector.writeMultipleComp(modelData.getAllOkStates());

            sim.init(0.0);
            stepCounter = 0;
            while (stepCounter <= numberOfSteps){
                scenario.injectFault(stepCounter, fmiConnector);
                if(enc == null)
                    faultySimulationRunData.addValues(fmiConnector.readMultiple(modelData.getComponentsToRead()));
                else
                    faultySimulationRunData.addPredicates(new ArrayList<>((enc.encodeObservation(fmiConnector.readMultiple(modelData.getComponentsToRead())))));
                sim.doStep(stepSize);
                stepCounter++;
            }

            Set<String> difference = diff.encodeDiff(corrSimulationRunData, faultySimulationRunData);
            if(difference != null && !difference.isEmpty()){
                List<Component> comps = scenario.getTimeCompMap().values().iterator().next();
                for(String rule : formRule(comps, difference))
                    abductiveModel.addRule(rule);
            }
        }

        return abductiveModel;
    }

    private List<String> formRule(List<Component> test, Set<String> diff){
        List<String> rules = new ArrayList<>();

        List<String> faultModes = new ArrayList<>();
        for (Component comp : test) {
            if (modelData.isModeAssigmentVar(comp.getName())) {
                String compAss = comp.getName();
                String faultState = getFaultMode(comp);
                if (faultState.equals("ok"))
                    continue;
                if (!faultState.isEmpty()) {
                    compAss = compAss.substring(0, 1).toUpperCase() + compAss.substring(1);
                    faultModes.add(compAss + "(" + faultState + ")");
                }
            }
        }

        for(String difference : diff) {
            StringBuilder sb = new StringBuilder();
            if (faultModes.isEmpty())
                continue;
            sb.append(String.join(",", faultModes));
            sb.append(" -> ").append(difference).append(".");
            rules.add(sb.toString());
        }

        return rules;
    }

    private String getFaultMode(Component component){
        String res = "";
        for(ModelInput mi : modelData.getModeAssigmentVars())
            if(mi.getName().equals(component.getName()))
                return (String) mi.getValues().get(((Integer)component.getValue()) - 1);
        return res;
    }
}
