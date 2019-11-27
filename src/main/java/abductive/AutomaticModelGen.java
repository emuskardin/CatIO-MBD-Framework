package abductive;

import FmiConnector.Component;
import FmiConnector.FmiMonitor;
import FmiConnector.TYPE;
import abductive.combinatorial.MLCA;
import abductive.combinatorial.ModelData;
import lombok.Data;
import org.javafmi.wrapper.Simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class AutomaticModelGen {
    private AbductiveModel abductiveModel;
    private MLCA mlca;
    private FmiMonitor fmiMonitor;
    private List<Component> toRead;

    public AutomaticModelGen(String pathToFmi ,ModelData modelData){
        abductiveModel = new AbductiveModel();
        mlca = new MLCA(modelData);
        fmiMonitor = new FmiMonitor(pathToFmi);
    }

    public AbductiveModel generateModel() throws IOException {
        AbductiveModel abductiveModel = new AbductiveModel();
        Component bulb = new Component("b1.on", TYPE.BOOLEAN);
        Simulation sim = fmiMonitor.getSimulation();
        List<Boolean> correctBulbObs = new ArrayList<>();
        fmiMonitor.getFmiWriter().writeMultipleComp(mlca.getAllOkStates());
        sim.init(0.0);

        while(sim.getCurrentTime() < 10){
            correctBulbObs.add((Boolean) fmiMonitor.read(bulb).getValue());
            sim.doStep(0.25);
        }

        mlca.numberOfCorrectComps(4);
        mlca.addRelationToGroup(mlca.getComponents(), 3);
        mlca.createTestSuite("test");
        List<List<Component>> simulationInputs = mlca.suitToSimulationInput("test");

        for(List<Component> test : simulationInputs){
            fmiMonitor.resetSimulation();
            sim = fmiMonitor.getSimulation();
            fmiMonitor.getFmiWriter().writeMultipleComp(test);
            sim.init(0.0);
            List<Boolean> observations = new ArrayList<>();
            while (sim.getCurrentTime() < 10){
                observations.add((Boolean) fmiMonitor.read(bulb).getValue());
                sim.doStep(0.25);
            }

            List<String> diff = diff(correctBulbObs, observations);
            //if(!diff.isEmpty())
            //    abductiveModel.addRule(formRule(test, diff));
        }
        return abductiveModel;
    }

    private String formRule(List<Component> test, List<String> diff){
        StringBuilder sb = new StringBuilder();
        List<String> faultModes = new ArrayList<>();
        for (int i = 0; i < test.size(); i++) {
            String name = test.get(i).getName();
            test.get(i).setName(name.substring(0, 1).toUpperCase() + name.substring(1));
        }
        test.forEach(it -> faultModes.add(getFaultMode(it)));
        sb.append(String.join(",", faultModes));
        sb.append(" -> ");
        sb.append(String.join("", diff));

        System.out.println(sb.toString());
        return sb.toString();
    }

    private String getFaultMode(Component component){
        String res;
        res = component.getName();
        res += component.getValue().toString();
        return res;
    }

    public List<String> diff(List<Boolean> corr, List<Boolean> faulty){
        List<String> res = new ArrayList<>();
        for (int i = 0; i < corr.size(); i++) {
            if(corr.get(i) != faulty.get(i)){
                if(faulty.get(i))
                    res.add("bulbOn");
                else
                    res.add("bulbOFF");
            }
        }

        return res;
    }


}
