package runningExamples.SimpleRobot.Abductive;

import interfaces.Diff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircuitDiff implements Diff {
    @Override
    public Set<String> encodeDiff(List<List<String>> corr, List<List<String>> faulty) {
        Set<String> diff = new HashSet<>();

        for (int i = 0; i < corr.size(); i++) {
            for (int j = 0; j < corr.get(i).size(); j++) {
                String corrSim = corr.get(i).get(j);
                String faultySim = faulty.get(i).get(j);

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
