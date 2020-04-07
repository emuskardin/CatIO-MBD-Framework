package runningExamples.SimpleRobot.Consistency;

import FmiConnector.FmiConnector;
import interfaces.Controller;

import java.util.List;
import java.util.function.Function;

public class RepairRobot implements Controller {
    private int step = 0;
    private FmiConnector fmiConnector;

    @Override
    public void performAction(FmiConnector fmiConnector, List<String> diagnosis) {
        this.fmiConnector = fmiConnector;
        for(String diag : diagnosis) {
            if (diag.equals("AbLeftWheel"))
                repairLeftWheel();
            if (diag.equals("AbRightWheel"))
                repairRightWheel();
        }
        //compensateLeftFaster();
    }

    private void repairLeftWheel(){
        fmiConnector.writeVar("leftFaultType", 1);
    }

    private void repairRightWheel(){
        fmiConnector.writeVar("rightFaultType", 1);
    }

    private void compensateLeftFaster(){
        if(step == 0) {
            fmiConnector.writeVar("leftFaultType", 1);
            fmiConnector.writeVar("rightFaultType", 2);
//        }else if(step == 1) {
//            fmiWriter.writeVar("leftFaultType", 1);
//            fmiWriter.writeVar("rightFaultType", 1);
//        }else if(step == 2){
//            fmiWriter.writeVar("leftFaultType", 3);
//            fmiWriter.writeVar("rightFaultType", 1);
        } else{
            repairRightWheel();
            repairLeftWheel();
        }
        step++;
    }
}