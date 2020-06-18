package interfaces;

import model.SimulationRunData;

import java.util.Set;

public interface Diff {
    /**
     * @param correct SimulationRunData which contains either values or predicates of simulation without fault injection
     * @param faulty SimulationRunData which contains either values or predicates of simulation with fault injection
     * In both lists, each index i corresponds to i-th time step in the simulation and it's corresponding values
     * @return encoding of diff function with respect to the model
     */
    Set<String> encodeDiff(SimulationRunData correct, SimulationRunData faulty);
}
