package consistency.examples;

import lombok.Data;
import consistency.stepFaultDiag.CbModelEncoderContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class BookCarModel implements CbModelEncoderContract {

    @Override
    public void constructModel(){
//        model.addHealthStatePredicate("ab(rightWheel)");
//        model.addHealthStatePredicate("ab(leftWheel)");
//        model.addCNFClause("rightWheel");
//        model.addCNFClause("leftWheel");
//        model.addCNFClause("-rightWheel", "ab(rightWheel)", "nominal(rightWheel)");
//        model.addCNFClause("-rightWheel", "ab(rightWheel)", "plus(rightWheel)");
//        model.addCNFClause("-rightWheel", "ab(rightWheel)", "minus(rightWheel)");
//        model.addCNFClause("-leftWheel", "ab(leftWheel)", "nominal(leftWheel)");
//        model.addCNFClause("-leftWheel", "ab(leftWheel)", "plus(leftWheel)");
//        model.addCNFClause("-leftWheel", "ab(leftWheel)", "minus(leftWheel)");
//        model.addCNFClause("-nominal(rightWheel)", "-nominal(leftWheel)", "straight");
//        model.addCNFClause("-plus(rightWheel)", "-minus(leftWheel)", "left");
//        model.addCNFClause("-minus(rightWheel)","-plus(leftWheel)", "right");
          model.modelFromFile("bookModel.txt");
    }

    @Override
    public List<String> encodeObservation(Map<String, Object> obs){

        List<String> encodedObservation = new ArrayList<>();
        Double rightWheelInput = (Double) obs.get("robot.rightWheel.i");
        Double rightWheelOutput = (Double) obs.get("robot.rightWheel.o");
        Double leftWheelInput = (Double) obs.get("robot.leftWheel.i");
        Double leftWheelOutput = (Double) obs.get("robot.leftWheel.o");

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
            encodedObservation.set(1, "-" + encodedObservation.get(1));
        if(!(Double.compare(leftWheelInput, leftWheelOutput) == 0))
            encodedObservation.set(2, "-" +encodedObservation.get(2));

        return encodedObservation;
    }
}