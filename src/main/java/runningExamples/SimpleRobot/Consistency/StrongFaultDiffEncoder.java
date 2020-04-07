package runningExamples.SimpleRobot.Consistency;

import interfaces.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StrongFaultDiffEncoder implements Encoder {

    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        List<String> encodedObservation = new ArrayList<>();
        Double rightWheelInput = (Double) obs.get("rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("rightWheel.o");
        Double leftWheelInput = (Double) obs.get("leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("leftWheel.o");

        int wantedDir = Double.compare(rightWheelInput, leftWheelInput);
        int actualDir = Double.compare(rightWheelOutput, leftWheelOutput);

        if(wantedDir == 0)
            encodedObservation.add("wantedStraight");
        else if(wantedDir == 1)
            encodedObservation.add("wantedRight");
        else
            encodedObservation.add("wantedLeft");

        if(actualDir == 0)
            encodedObservation.add("actualStraight");
        else if(actualDir == 1)
            encodedObservation.add("actualRight");
        else
            encodedObservation.add("actualLeft");

        return encodedObservation;
    }
}
