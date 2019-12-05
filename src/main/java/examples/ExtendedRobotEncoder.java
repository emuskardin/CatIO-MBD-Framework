package examples;

import interfaces.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtendedRobotEncoder implements Encoder {
    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        List<String> encodedObservation = new ArrayList<>();
        Double rightWheelInput = (Double) obs.get("robot.wheelRight.u");
        Double rightWheelOutput = (Double) obs.get("robot.wheelRight.y");
        Double leftWheelInput = (Double) obs.get("robot.wheelLeft.u");
        Double leftWheelOutput = (Double) obs.get("robot.wheelLeft.y");
        Double rightPowerModuleOutput  = (Double) obs.get("robot.powerModuleRight.y");
        Double leftPowerModuleOutput  = (Double) obs.get("robot.powerModuleLeft.y");
        Double voltageRegLeftDesired = (Double) obs.get("robot.voltageRegulatorLeft.desiredOutput");
        Double voltageRegRightDesired = (Double) obs.get("robot.voltageRegulatorRight.desiredOutput");
        Double voltageRegLeftOutput = (Double) obs.get("robot.voltageRegulatorLeft.y");
        Double voltageRegRightOutput = (Double) obs.get("robot.voltageRegulatorRight.y");

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

        if(!(Double.compare(voltageRegLeftDesired, voltageRegLeftOutput) == 0))
            encodedObservation.add("!desiredLeftVoltage");
        if(!(Double.compare(voltageRegRightDesired, voltageRegRightOutput) == 0))
            encodedObservation.add("!desiredRightVoltage");

        if(!(Double.compare(voltageRegLeftOutput, leftPowerModuleOutput) == 0))
            encodedObservation.add("!eqLeftInAndOut");
        if(!(Double.compare(voltageRegRightOutput, rightPowerModuleOutput) == 0))
            encodedObservation.add("!eqRightInAndOut");

        return encodedObservation;
    }
}
