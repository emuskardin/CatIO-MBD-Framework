package FmiConnector;

import model.Component;

import java.util.HashMap;
import java.util.Map;

public class RepairAction {
    private Map<String, Component> diagPredicateToCompMap = new HashMap<>();

    public void associatePredicateWithComponent(String predicate, Component comp){
        diagPredicateToCompMap.put(predicate, comp);
    }

    public Component getComponentToRepair(String predicate){
        return diagPredicateToCompMap.get(predicate);
    }
}
