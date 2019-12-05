package examples;

import interfaces.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookAbEncoder implements Encoder {

    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        List<String> encodedObservation = new ArrayList<>();
        Double rightWheelInput = (Double) obs.get("robot.rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("robot.rightWheel.o");
        Double leftWheelInput = (Double) obs.get("robot.leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("robot.leftWheel.o");

        int compInputs = Double.compare(rightWheelInput, leftWheelInput);
        if(compInputs == 0) encodedObservation.add("straight");
        else if(compInputs == 1) encodedObservation.add("left_curve");
        else encodedObservation.add("right_curve");

        int compOutputs = Double.compare(rightWheelOutput, leftWheelOutput);
        if (compOutputs == 0){
            encodedObservation.add("nominal(right)");
            encodedObservation.add("nominal(left)");
        }
        else if (compOutputs == 1){
            encodedObservation.add("faster(right)");
            encodedObservation.add("slower(left)");
        }
        else {
            encodedObservation.add("slower(right)");
            encodedObservation.add("faster(left)");
        }

        return encodedObservation;
    }
}
