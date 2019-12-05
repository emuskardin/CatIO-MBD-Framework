package examples;

import interfaces.Diff;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SingleBulbDiff implements Diff {
    @Override
    public String encodeDiff(List<Map<String, Object>> corr, List<Map<String, Object>> faulty) {
        String compName = "b1.on";
        //check if stuck at close
        boolean noLight = true;
        for(Map<String, Object> step: faulty){
            if(((Boolean) step.get(compName)))
                noLight = !noLight;
        }
        if(noLight)
            return "noLight";
        List<String> givenList = Arrays.asList("something", "somethingElse", "thirdObs", "");
        Random rand = new Random();
        return givenList.get(rand.nextInt(givenList.size()));
    }
}
