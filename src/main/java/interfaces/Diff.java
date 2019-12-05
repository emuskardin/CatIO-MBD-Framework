package interfaces;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface Diff {
    /**
     * @param corr List of maps containing map of component names and read values for each time step
     * @param faulty List of maps containing map of component names and read values from fault injected simulation
     * In both lists, each index i corresponds to i-th time step in the simulation and it's corresponding values
     * @return encoding of diff function with respect to the model
     */
    public String encodeDiff(List<Map<String, Object>> corr, List<Map<String, Object>> faulty);
}
