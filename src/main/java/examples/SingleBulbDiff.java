package examples;

import interfaces.Diff;

import java.util.List;
import java.util.Map;

public class SingleBulbDiff implements Diff {
    @Override
    public String encodeDiff(List<Map<String, Object>> corr, List<Map<String, Object>> faulty) {
        return null;
    }
}
