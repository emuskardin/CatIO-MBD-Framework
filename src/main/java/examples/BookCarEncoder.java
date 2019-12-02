package examples;

import interfaces.Encoder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class BookCarEncoder implements Encoder {
    @Override
    public List<String> encodeObservation(Map<String, Object> obs){

        List<String> encodedObservation = new ArrayList<>();
        Double rightWheelInput = (Double) obs.get("rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("rightWheel.o");
        Double leftWheelInput = (Double) obs.get("leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("leftWheel.o");

        int compInputs = Double.compare(rightWheelInput, leftWheelInput);
        if(compInputs == 0) encodedObservation.add("straight");
        else if(compInputs == 1) encodedObservation.add("left");
        else encodedObservation.add("right");

        int compOutputs = Double.compare(rightWheelOutput, leftWheelOutput);
        if (compOutputs == 0){
            encodedObservation.add("rightNominal");
            encodedObservation.add("leftNominal");
        }
        else if (compOutputs == 1){
            encodedObservation.add("rightFaster");
            encodedObservation.add("leftSlower");
        }
        else {
            encodedObservation.add("rightSlower");
            encodedObservation.add("leftFaster");
        }

        // check the behaviour
        if(!(Double.compare(rightWheelInput, rightWheelOutput) == 0))
            encodedObservation.set(1, "!" + encodedObservation.get(1));
        if(!(Double.compare(leftWheelInput, leftWheelOutput) == 0))
            encodedObservation.set(2, "!" +encodedObservation.get(2));

        return encodedObservation;
    }
}