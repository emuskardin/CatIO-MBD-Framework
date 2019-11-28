package interfaces;

import FmiConnector.Component;

import java.util.List;

@FunctionalInterface
public interface Diff {
    /**
     * @param corr Components containing names and values of simulation run without any injected faults
     * @param faulty Components containing names and possibly faulty values read from fault injected simulation
     * @return encoding of diff function with respect to the model
     */
    public String encodeDiff(List<Component> corr, List<Component> faulty);
}
