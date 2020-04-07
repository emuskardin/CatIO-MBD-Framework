package runningExamples.SimpleRobot.Consistency;

import interfaces.Encoder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SimpleCarEncoder implements Encoder {

    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        List<String> encodedObservation = new ArrayList<>();
        Double rightWheelInput = (Double) obs.get("rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("rightWheel.o");
        Double leftWheelInput = (Double) obs.get("leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("leftWheel.o");

        encodedObservation.add("lEqInOut");
        encodedObservation.add("rEqInOut");

        // check if behaviour is ok
        if(!(Double.compare(leftWheelInput, leftWheelOutput) == 0))
            encodedObservation.set(0, "!" + encodedObservation.get(0));
        if(!(Double.compare(rightWheelInput, rightWheelOutput) == 0))
            encodedObservation.set(1, "!" + encodedObservation.get(1));

        return encodedObservation;
    }
}