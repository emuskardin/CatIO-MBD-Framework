package examples;

import interfaces.Diff;

import java.util.*;

public class SingleBulbDiff implements Diff {
    @Override
    public Set<String> encodeDiff(List<List<String>> corr, List<List<String>> faulty) {
        Set<String> ret = new HashSet<>();
        //check if stuck at close
        boolean noLight = true;
        for(List<String> faultyStep : faulty){
            if(faultyStep.get(0).equals("light")) {
                noLight = false;
                break;
            }
        }
        if(noLight) {
            ret.add("noLight");
            return ret;
        }

        for (int i = 0; i < corr.size(); i++) {
            if(!corr.get(i).get(0).equals(faulty.get(i).get(0)))
                ret.add("inverted");
        }
        return ret;
    }
}
