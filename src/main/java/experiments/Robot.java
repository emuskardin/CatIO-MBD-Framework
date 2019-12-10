package experiments;

import FmiConnector.FmiMonitor;
import examples.BookCarEncoder;
import interfaces.Encoder;
import model.Component;
import model.Type;

import java.util.ArrayList;
import java.util.List;

public class Robot {
    private static String path = "FMIs/ERobot.SubModel.InputSimpleRobot.fmu";
    private static FmiMonitor fmiMonitor = new FmiMonitor(path);
    private static Encoder encoder = new BookCarEncoder();
    private List<Component> toRead = new ArrayList<>();
    private static boolean insance = false;
    public Robot(){
            toRead.add(new Component("rightWheel.i", Type.DOUBLE));
            toRead.add(new Component("rightWheel.o", Type.DOUBLE));
            toRead.add(new Component("leftWheel.i", Type.DOUBLE));
            toRead.add(new Component("leftWheel.o", Type.DOUBLE));
            if(!insance) {
                fmiMonitor.getFmiWriter().writeVar("rightWheelInput", 3.0);
                fmiMonitor.getFmiWriter().writeVar("leftWheelInput", 3.0);
                leftOk();
                rightOk();
                insance = true;
            }
    }

    public String driveStraight(){
        fmiMonitor.getFmiWriter().writeVar("rightWheelInput", 3.0);
        fmiMonitor.getFmiWriter().writeVar("leftWheelInput", 3.0);
        return doStep();
    }
    public String driveLeft(){
        fmiMonitor.getFmiWriter().writeVar("rightWheelInput", 5.0);
        fmiMonitor.getFmiWriter().writeVar("leftWheelInput", 3.0);
        return doStep();
    }
    public String driveRight(){
        fmiMonitor.getFmiWriter().writeVar("rightWheelInput", 3.0);
        fmiMonitor.getFmiWriter().writeVar("leftWheelInput", 5.0);
        return doStep();
    }
    public String leftOk(){
        fmiMonitor.getFmiWriter().writeVar("leftFaultType", 1);
        return doStep();
    }
    public String rightOk(){
        fmiMonitor.getFmiWriter().writeVar("rightFaultType", 1);
        return doStep();
    }
    public String leftFaster(){
        fmiMonitor.getFmiWriter().writeVar("leftFaultType", 3);
        return doStep();
    }
    public String leftSlower(){
        fmiMonitor.getFmiWriter().writeVar("leftFaultType", 2);
        return doStep();
    }
    public String rightFaster(){
        fmiMonitor.getFmiWriter().writeVar("rightFaultType", 3);
        return doStep();
    }
    public String rightSlower(){
        fmiMonitor.getFmiWriter().writeVar("rightFaultType", 2);
        return doStep();
    }
    public String doStep(){
        fmiMonitor.getSimulation().doStep(1);
        return encoder.encodeObservation(fmiMonitor.readMultiple(toRead)).toString();
    }
}
