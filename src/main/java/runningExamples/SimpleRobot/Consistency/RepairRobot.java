package runningExamples.SimpleRobot.Consistency;

import FmiConnector.FmiConnector;
import interfaces.Controller;

import java.util.List;

public class RepairRobot implements Controller {
    private FmiConnector fmiConnector;
    private int leftFasterStep = 4;

    @Override
    public int performAction(FmiConnector fmiConnector, List<List<String>> diagnosis) {
        this.fmiConnector = fmiConnector;
        List<String> singleDiag = diagnosis.get(0);

        for(String diag : singleDiag) {
            if (diag.equals("AbLeftWheel"))
                return compensateLeftFaster();
            if (diag.equals("AbRightWheel"))
                return repairLeftWheel();
            if (diag.equals("faster(leftWheel)")){
                return compensateLeftFaster();
            }
            /// ...
        }
        return compensateLeftFaster();
    }

    private int repairLeftWheel(){
        // to repair a component we simply put it state to "ok", which always corresponds to 1
        fmiConnector.writeVar("leftFaultType", 1);
        return 0;
    }

    private int repairRightWheel(){
        fmiConnector.writeVar("rightFaultType", 1);
        return 0;
    }

    private int compensateLeftFaster(){
        // notice that in this example instead of writing values which correspond to wheel speed
        // we can use wheel fault types to achive same effect, of one wheel spinning faster, other slower etc.
        // note that 1 always corresponds to ok state, 2 is faster, and 3 is slower
        if(leftFasterStep == 4) {
            fmiConnector.writeVar("leftFaultType", 1);
            fmiConnector.writeVar("rightFaultType", 2);
            return --leftFasterStep;
        }
        else if(leftFasterStep == 3){
            fmiConnector.writeVar("leftFaultType", 1);
            fmiConnector.writeVar("rightFaultType", 1);
            return --leftFasterStep;
        }
        else if(leftFasterStep == 2){
            fmiConnector.writeVar("leftFaultType", 1);
            fmiConnector.writeVar("rightFaultType", 2);
            return --leftFasterStep;
        }else if(leftFasterStep == 1){
            fmiConnector.writeVar("leftFaultType", 2);
            fmiConnector.writeVar("rightFaultType", 1);
            return --leftFasterStep;
        }
        else{
            // finnaly set wheels to correct state
            repairRightWheel();
            repairLeftWheel();
            return --leftFasterStep;
        }
    }
}