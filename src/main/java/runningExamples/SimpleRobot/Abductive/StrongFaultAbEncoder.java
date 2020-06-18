package runningExamples.SimpleRobot.Abductive;

import interfaces.Encoder;

import java.util.*;

public class StrongFaultAbEncoder implements Encoder {
    @Override
    public Set<String> encodeObservation(Map<String, Object> obs) {
        Set<String> encodedObservation = new HashSet<>();
        Double rightWheelInput = (Double) obs.get("rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("rightWheel.o");
        Double leftWheelInput = (Double) obs.get("leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("leftWheel.o");

        int wantedDir = Double.compare(rightWheelInput, leftWheelInput);
        int actualDir = Double.compare(rightWheelOutput, leftWheelOutput);

        if (wantedDir == 0)
            encodedObservation.add("wantedDirection(straight)");
        else if (wantedDir == 1)
            encodedObservation.add("wantedDirection(right)");
        else
            encodedObservation.add("wantedDirection(left)");

        if (actualDir == 0)
            encodedObservation.add("actualDirection(straight)");
        else if (actualDir == 1)
            encodedObservation.add("actualDirection(right)");
        else
            encodedObservation.add("actualDirection(left)");

        if (!rightWheelInput.equals(rightWheelOutput))
            encodedObservation.add("notEqualInOut(right)");
        else
            encodedObservation.add("EqualInOut(right)");

        if (!leftWheelInput.equals(leftWheelOutput))
            encodedObservation.add("notEqualInOut(left)");
        else
            encodedObservation.add("EqualInOut(left)");

        return encodedObservation;
    }
}
