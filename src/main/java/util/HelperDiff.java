package util;

import interfaces.Diff;
import model.SimulationRunData;
import java.util.Set;

public class HelperDiff implements Diff {
    @Override
    public Set<String> encodeDiff(SimulationRunData correct, SimulationRunData faulty) {
        return null;
    }
}
