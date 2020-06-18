package runningExamples.SimpleRobot.Consistency;

import interfaces.Encoder;
import lombok.Data;

import java.util.*;

@Data
public class SimpleCarEncoder implements Encoder {

    @Override
    public Set<String> encodeObservation(Map<String, Object> obs) {
        Set<String> encodedObservation = new HashSet<>();
        Double rightWheelInput = (Double) obs.get("rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("rightWheel.o");
        Double leftWheelInput = (Double) obs.get("leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("leftWheel.o");


        // check if behaviour is ok
        if(!(Double.compare(leftWheelInput, leftWheelOutput) == 0))
            encodedObservation.add("!lEqInOut");
        else
            encodedObservation.add("lEqInOut");
        if(!(Double.compare(rightWheelInput, rightWheelOutput) == 0))
            encodedObservation.add("!rEqInOut");
        else
            encodedObservation.add("rEqInOut");

        return encodedObservation;
    }
}