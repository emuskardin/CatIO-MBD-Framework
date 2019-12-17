package examples;

import interfaces.Encoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SingleBulbEncoder implements Encoder {
    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        if((Boolean) obs.get("b1.on"))
            return Collections.singletonList("light");
        else
            return Collections.singletonList("noLight");

    }
}
