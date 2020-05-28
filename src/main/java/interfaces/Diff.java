package interfaces;

import runningExamples.SimpleRobot.Abductive.SimulationRunData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Diff {
    /**
     * @param correct List of maps containing map of component names and read values for each time step
     * @param faulty List of maps containing map of component names and read values from fault injected simulation
     * In both lists, each index i corresponds to i-th time step in the simulation and it's corresponding values
     * @return encoding of diff function with respect to the model
     */
    Set<String> encodeDiff(SimulationRunData correct, SimulationRunData faulty);
}
