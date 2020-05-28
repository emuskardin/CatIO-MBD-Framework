package runningExamples.SimpleRobot.Abductive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationRunData {
    private List<Map<String, Object>> valuesMap;
    private List<List<String>> predicatesMap;


    public SimulationRunData() {
        valuesMap = new ArrayList<>();
        predicatesMap = new ArrayList<>();
    }

    public Map<String, Object> getValuesFromStep(int n) {
        return valuesMap.get(n);
    }

    public List<String> getPredicatesFromStep(int n) {
        return predicatesMap.get(n);
    }

    public void addValues(Map<String, Object> values) {
        valuesMap.add(values);
    }

    public void addPredicates(List<String> predicates) {
        predicatesMap.add(predicates);
    }

    public int getNumberOfSteps() {
        if (predicatesMap.size() != 0)
            return predicatesMap.size();
        return valuesMap.size();
    }
}
