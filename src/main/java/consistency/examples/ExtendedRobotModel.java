package consistency.examples;

import consistency.stepFaultDiag.CbModelEncoderContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtendedRobotModel implements CbModelEncoderContract {
    @Override
    public void constructModel() {
        model.addHealthStatePredicate("ab(leftWheel)");
        model.addHealthStatePredicate("ab(rightWheel)");
        model.addHealthStatePredicate("ab(leftPowerModule)");
        model.addHealthStatePredicate("ab(rightPowerModule)");
        model.addHealthStatePredicate("ab(leftVoltageReg)");
        model.addHealthStatePredicate("ab(rightVoltageReg)");
        model.addHealthStatePredicate("ab(battery)");

        model.addCNFClause("leftWheel");
        model.addCNFClause("rightWheel");
        model.addCNFClause("leftPowerModule");
        model.addCNFClause("rightPowerModule");
        model.addCNFClause("leftVoltageReg");
        model.addCNFClause("rightVoltageReg");
        model.addCNFClause("battery");

        model.addCNFClause("-battery", "ab(battery), battery(outNotNull)");
        model.addCNFClause("-leftPowerModule", "ab(leftPowerModule)", "leftPowerModule(outBatMax)");
        model.addCNFClause("-leftPowerModule", "ab(leftPowerModule)", "leftPowerModule(desiredOutput)");
        model.addCNFClause("-rightPowerModule", "ab(rightPowerModule)", "rightPowerModule(outBatMax)");
        model.addCNFClause("-rightPowerModule", "ab(rightPowerModule)", "rightPowerModule(desiredOutput)");
        model.addCNFClause("-leftVoltageReg", "ab(leftVoltageReg)", "leftVoltageReg(outEqIn)");
        model.addCNFClause("-rightVoltageReg", "ab(rightVoltageReg)", "rightVoltageReg(outEqIn)");

        model.addCNFClause("-battery(outNotNull)","-leftPowerModule(desiredOutput)", "-rightPowerModule(desiredOutput)", "nominal(leftWheel)");
        model.addCNFClause("-battery(outNotNull)","-leftPowerModule(desiredOutput)", "-rightPowerModule(desiredOutput)", "plus(leftWheel)");
        model.addCNFClause("-battery(outNotNull)","-leftPowerModule(desiredOutput)", "-rightPowerModule(desiredOutput)", "nominal(rightWheel)");
        model.addCNFClause("-battery(outNotNull)","-leftPowerModule(desiredOutput)", "-rightPowerModule(desiredOutput)", "plus(rightWheel)");

        model.addCNFClause("-battery(outNotNull)", "-leftPowerModule(outBatMax)", "-rightPowerModule(outBatMax)", "nominal(leftWheel)");
        model.addCNFClause("-battery(outNotNull)", "-leftPowerModule(outBatMax)", "-rightPowerModule(outBatMax)", "plus(leftWheel)");
        model.addCNFClause("-battery(outNotNull)", "-leftPowerModule(outBatMax)", "-rightPowerModule(outBatMax)", "nominal(rightWheel)");
        model.addCNFClause("-battery(outNotNull)", "-leftPowerModule(outBatMax)", "-rightPowerModule(outBatMax)", "plus(rightWheel)");

        // Wheels and directions
        model.addCNFClause("-leftWheel", "ab(leftWheel)", "nominal(leftWheel)");
        model.addCNFClause("-leftWheel", "ab(leftWheel)", "plus(leftWheel)");
        model.addCNFClause("-rightWheel", "ab(rightWheel)", "nominal(rightWheel)");
        model.addCNFClause("-rightWheel", "ab(rightWheel)", "plus(rightWheel)");
        model.addCNFClause("-nominal(leftWheel)", "-nominal(rightWheel)", "straight");
        model.addCNFClause("-plus(leftWheel)", "-nominal(rightWheel)", "left");
        model.addCNFClause("-nominal(leftWheel)", "-plus(rightWheel)", "right");

    }

    @Override
    public List<String> encodeObservation(Map<String, Object> obs) {
        List<String> njok = new ArrayList<>();
        njok.add("-straight");
        njok.add("nominal(leftWheel)");
        njok.add("plus(rightWheel)");
        njok.add("-leftVoltageReg(outEqIn)");

        return njok;
    }
}
