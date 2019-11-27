package interfaces;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface Encoder {
    List<String> encodeObservation(Map<String, Object> obs);
}
