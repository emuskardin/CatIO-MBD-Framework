package interfaces;

import java.util.Map;
import java.util.Set;

public interface Encoder {
    /**
     * @param obs map containing names and values read from simulation
     * @return set of observations with respect to the model
     */
    Set<String> encodeObservation(Map<String, Object> obs);
}
