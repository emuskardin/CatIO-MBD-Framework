package model;

import FmiConnector.FmiWriter;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class Scenario {
    private Object scenarioId;
    private Map<Double, List<Component>> timeCompMap = new LinkedHashMap<>();

    public Scenario(Object scenarioId){
        this.scenarioId = scenarioId;
    }

    public void addToMap(Double time, List<Component> comps){
        timeCompMap.put(time, comps);
    }

    public void injectFault(Double currentTime ,FmiWriter fmiWriter, ModelData modelData){
        for(Double key : timeCompMap.keySet()){
            if(key.equals(currentTime)) {
                for(Component comp: timeCompMap.get(key)){
                    if(comp.getValue() instanceof String && modelData.isHS(comp.getName()))
                        comp.setValue(modelData.getFault(comp.getName(), (String) comp.getValue()));
                }
                fmiWriter.writeMultipleComp(timeCompMap.get(key));
            }
        }
    }
}
