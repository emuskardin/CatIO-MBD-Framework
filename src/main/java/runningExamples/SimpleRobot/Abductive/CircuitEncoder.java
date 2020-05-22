package runningExamples.SimpleRobot.Abductive;

import interfaces.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CircuitEncoder implements Encoder {
    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        List<String> observations = new ArrayList<>();
        for (int i = 0; i < obs.values().size(); i++) {
            String bulbId = "b" + (i + 1) + ".on";
            if ((Boolean) obs.get(bulbId)) {
                observations.add("bulb" + (i + 1) + "_ON");
            } else {
                observations.add("bulb" + (i + 1) + "_OFF");
            }
        }
        return observations;
    }
}
