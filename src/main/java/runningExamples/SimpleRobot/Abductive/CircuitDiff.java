package runningExamples.SimpleRobot.Abductive;

import interfaces.Diff;

import java.util.HashSet;
import java.util.Set;

public class CircuitDiff implements Diff {

    @Override
    public Set<String> encodeDiff(SimulationRunData correct, SimulationRunData faulty) {
        Set<String> diff = new HashSet<>();

        for (int i = 0; i < correct.getNumberOfSteps(); i++) {
            for (int j = 0; j < correct.getPredicatesFromStep(i).size(); j++) {
                String corrSim = correct.getPredicatesFromStep(i).get(j);
                String faultySim = faulty.getPredicatesFromStep(j).get(j);

                if(!corrSim.equals(faultySim)){
                    if(corrSim.contains("_ON"))
                        diff.add("bulb" + (j + 1) + "_OFF_INSTEAD_ON");
                    else
                        diff.add("bulb" + (j + 1) + "_ON_INSTEAD_OFF");
                }
            }
        }
        return diff;
    }
}
