package interfaces;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface Encoder {
    /**
     * @param obs map containing names and values read from simulation
     * @return list of observations with respect to the model
     */
    List<String> encodeObservation(Map<String, Object> obs);
}
