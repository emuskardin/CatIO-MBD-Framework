package runningExamples.SimpleRobot.Consistency;

import FmiConnector.FmiWriter;
import interfaces.Controller;

import java.util.List;
import java.util.function.Function;

public class RepairRobot implements Controller {
    private FmiWriter fmiWriter;
    private int step = 0;

    @Override
    public void performAction(FmiWriter fmiWriter, List<String> diagnosis) {
        this.fmiWriter = fmiWriter;
        for(String diag : diagnosis) {
            if (diag.equals("AbLeftWheel"))
                repairLeftWheel();
            if (diag.equals("AbRightWheel"))
                repairRightWheel();
        }
        //compensateLeftFaster();
    }

    private void repairLeftWheel(){
        fmiWriter.writeVar("leftFaultType", 1);
    }

    private void repairRightWheel(){
        fmiWriter.writeVar("rightFaultType", 1);
    }

    private void compensateLeftFaster(){
        if(step == 0) {
            fmiWriter.writeVar("leftFaultType", 1);
            fmiWriter.writeVar("rightFaultType", 2);
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