package runningExamples.SimpleRobot.Abductive;

import interfaces.Diff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RobotDiff implements Diff {
    @Override
    public Set<String> encodeDiff(List<List<String>> corr, List<List<String>> faulty) {
        Set<String> diff = new HashSet<>();
        for (int i = 0; i < corr.size(); i++) {
            // as seen in encoder, predicates are
            // wanted direction
            // actual direction
            // is left wheel input equal to output
            // is right wheel input equal to output
            String corrWantedDir = corr.get(i).get(0);
            String corrActualDir = corr.get(i).get(1);
            String corrRightWheel = corr.get(i).get(2);
            String corrLeftWheel = corr.get(i).get(3);

            String faultyWantedDir = faulty.get(i).get(0);
            String faultyActualDir = faulty.get(i).get(1);
            String faultyRightWheel = faulty.get(i).get(2);
            String faultyLeftWheel = faulty.get(i).get(3);

            // check if actual directions of correct and faulty simulations are same
            if(!corrActualDir.equals(faultyActualDir)) {
                diff.add(corrWantedDir);
                diff.add(faultyActualDir);
            }
            // add a wheel which exerts different behaviour
            if(!corrRightWheel.equals(faultyRightWheel))
                diff.add(faultyRightWheel);
            if(!corrLeftWheel.equals(faultyLeftWheel))
                diff.add(faultyLeftWheel);
        }
        return diff;
    }
}
