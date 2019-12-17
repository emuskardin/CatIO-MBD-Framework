package abductive;

import interfaces.Diff;
import interfaces.Encoder;
import model.Component;
import FmiConnector.FmiMonitor;
import model.ModelData;
import lombok.Data;
import model.ModelInput;
import org.javafmi.wrapper.Simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class AbductiveModelGenerator {
    private ModelData modelData;
    private FmiMonitor fmiMonitor;
    private MLCA mlca;
    private AbductiveModel abductiveModel;
    private Diff diff;
    private Encoder enc;

    public AbductiveModelGenerator(String pathToFmi , ModelData modelData){
        abductiveModel = new AbductiveModel();
        this.modelData = modelData;
        mlca = new MLCA(modelData);
        fmiMonitor = new FmiMonitor(pathToFmi);
    }

    public void setEncoderAndDiff(Encoder enc, Diff diff){
        this.enc = enc;
        this.diff = diff;
    }


    public AbductiveModel generateModel(Double runtime, Double stepSize) throws IOException {
        mlca.createTestSuite("automaticModelGen.csv");
        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("automaticModelGen.csv");

        for(List<Component> test : simulationInputs){
            fmiMonitor.resetSimulation();
            Simulation sim = fmiMonitor.getSimulation();
            List<List<String>> corrObs = new ArrayList<>();
            List<List<String>> faultyObs = new ArrayList<>();
            // Setup params and inputs as well as all health states to ok
            fmiMonitor.getFmiWriter().writeMultipleComp(test);
            fmiMonitor.getFmiWriter().writeMultipleComp(mlca.getModelData().getAllOkStates());
            sim.init(0.0);
            while(sim.getCurrentTime() <= runtime){
                corrObs.add(enc.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead())));
                sim.doStep(stepSize);
            }

            fmiMonitor.resetSimulation();
            sim = fmiMonitor.getSimulation();
            fmiMonitor.getFmiWriter().writeMultipleComp(test);
            sim.init(0.0);
            while (sim.getCurrentTime() <= runtime){
                faultyObs.add(enc.encodeObservation(fmiMonitor.readMultiple(modelData.getComponentsToRead())));
                sim.doStep(stepSize);
            }

            Set<String> difference = diff.encodeDiff(corrObs, faultyObs);
            if(difference != null && !difference.isEmpty()){
                for(String rule : formRule(test, difference))
                    abductiveModel.addRule(rule);
            }
        }
        return abductiveModel;
    }

    private List<String> formRule(List<Component> test, Set<String> diff){
        List<String> rules = new ArrayList<>();

        List<String> faultModes = new ArrayList<>();
        for (Component comp : test) {
            if (modelData.isHS(comp.getName())) {
                String compAss = comp.getName();
                String faultState = getFaultMode(comp);
                if (faultState.equals("ok"))
                    continue;
                if (!faultState.isEmpty()) {
                    compAss = compAss.substring(0, 1).toUpperCase() + compAss.substring(1);
                    faultModes.add(compAss + "_" + faultState);
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
        for(ModelInput mi : modelData.getHealthStates())
            if(mi.getName().equals(component.getName()))
                return (String) mi.getValues().get(((Integer)component.getValue()) - 1);
        return res;
    }

    public void writeModeltoFile(String filename){
        String[] rules = abductiveModel.getRules().split("\\.");
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(new File(filename));
            for(String rule: rules)
                fileWriter.write(rule + ".\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
