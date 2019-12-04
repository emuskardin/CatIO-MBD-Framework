package abductive;

import interfaces.Diff;
import model.Component;
import FmiConnector.FmiMonitor;
import model.ModelData;
import lombok.Data;
import org.javafmi.wrapper.Simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AutomaticModelGen {
    private ModelData modelData;
    private FmiMonitor fmiMonitor;
    private MLCA mlca;
    private AbductiveModel abductiveModel;
    private Diff diff;

    public AutomaticModelGen(String pathToFmi ,ModelData modelData, Diff diff){
        abductiveModel = new AbductiveModel();
        this.modelData = modelData;
        mlca = new MLCA(modelData);
        fmiMonitor = new FmiMonitor(pathToFmi);
        this.diff = diff;
    }

    public AbductiveModel generateModel(Double runtime, Double stepSize) throws IOException {
        AbductiveModel abductiveModel = new AbductiveModel();
        Simulation sim = fmiMonitor.getSimulation();
        List<Map<String, Object>> corrObs = new ArrayList<>();
        fmiMonitor.getFmiWriter().writeMultipleComp(mlca.getMd().getAllOkStates());
        sim.init(0.0);

        // Get observations from simulations without faults
        while(sim.getCurrentTime() <= runtime){
            corrObs.add(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
            sim.doStep(stepSize);
        }

        mlca.numberOfCorrectComps(4);
        mlca.addRelationToGroup(mlca.getComponents(), 3);
        mlca.createTestSuite("automaticModelGen.csv");
        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("automaticModelGen.csv");

        for(List<Component> test : simulationInputs){
            fmiMonitor.resetSimulation();
            sim = fmiMonitor.getSimulation();
            fmiMonitor.getFmiWriter().writeMultipleComp(test);
            sim.init(0.0);
            List<Map<String, Object>> observations = new ArrayList<>();
            while (sim.getCurrentTime() < runtime){
                observations.add(fmiMonitor.readMultiple(modelData.getComponentsToRead()));
                sim.doStep(stepSize);
            }

            String difference = diff.encodeDiff(corrObs, observations);
            if(!difference.isEmpty())
                abductiveModel.addRule(formRule(test, difference));
        }
        return abductiveModel;
    }

    private String formRule(List<Component> test, String diff){
        StringBuilder sb = new StringBuilder();
        List<String> faultModes = new ArrayList<>();
        for (Component component : test) {
            String name = component.getName();
            component.setName(name.substring(0, 1).toUpperCase() + name.substring(1));
        }
        test.forEach(it -> faultModes.add(getFaultMode(it)));
        sb.append(String.join(",", faultModes));
        sb.append(" -> ").append(diff).append(".");

        System.out.println(sb.toString());
        return sb.toString();
    }

    private String getFaultMode(Component component){
        String res;
        res = component.getName();
        res += component.getValue().toString();
        return res;
    }

}
