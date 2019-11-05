package abductive.examples;

import abductive.AbModelEncoderContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookAbExample implements AbModelEncoderContract {
    @Override
    public void constructModel() {

        // Stating that model has 2 wheel
        model.addRule("wheel(left)");
        model.addRule("wheel(right)");

        // Wheel behaviour
        model.addRule("wheel(left), Expected(left) -> nominal(left)");
        model.addRule("wheel(left), Reduced(left) -> slower(left)");
        model.addRule("wheel(left), Increased(left) -> faster(left)");
        model.addRule("wheel(right), Expected(right) -> nominal(right)");
        model.addRule("wheel(right), Reduced(right) -> slower(right)");
        model.addRule("wheel(right), Increased(right) -> faster(right)");

        // Direction
        model.addRule("nominal(left), nominal(right) -> straight");
        model.addRule("nominal(left), faster(right) -> left_curve");
        model.addRule("slower(left), nominal(right) -> left_curve");
        model.addRule("slower(left), faster(right) -> left_curve");
        model.addRule("faster(left), nominal(right) -> right_curve");
        model.addRule("nominal(left), slower(right) -> right_curve");
        model.addRule("faster(left), slower(right) -> right_curve");

        // Only one speed for each wheel at the time
        model.addRule("nominal(left), faster(left) -> false");
        model.addRule("nominal(left), slower(left) -> false");
        model.addRule("faster(left), slower(left) -> false");
        model.addRule("nominal(right), faster(right) -> false");
        model.addRule("nominal(right), slower(right) -> false");
        model.addRule("faster(right), slower(right) -> false");

        // Car can move in only one direction at the time(not in paper)
        model.addRule("right_curve, left_curve -> false");
        model.addRule("right_curve, straight -> false");
        model.addRule("straight, left_curve -> false");

    }

    @Override
    public List<String> encodeObservations(Map<String, Object> obs) {
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
