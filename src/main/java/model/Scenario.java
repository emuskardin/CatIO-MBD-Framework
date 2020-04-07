package model;

import FmiConnector.FmiConnector;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class Scenario {
    private String scenarioId;
    private Map<Integer, List<Component>> timeCompMap = new LinkedHashMap<>();

    public Scenario(String scenarioId){
        this.scenarioId = scenarioId;
    }
    public void addToMap(Integer time, List<Component> comps){
        timeCompMap.put(time, comps);
    }

    public void injectFault(Integer currentTime , FmiConnector fmiConnector, ModelData modelData){
        for(Integer key : timeCompMap.keySet()){
            if(key.equals(currentTime)) {
                for(Component comp: timeCompMap.get(key)){
                    if(comp.getValue() instanceof String && comp.getType() == Type.ENUM)
                        comp.setValue(modelData.getEnumValue(comp.getName(), (String) comp.getValue()));
                }
                fmiConnector.writeMultipleComp(timeCompMap.get(key));
            }
        }
    }
}
