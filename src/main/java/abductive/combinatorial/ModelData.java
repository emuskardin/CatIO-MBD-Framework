package abductive.combinatorial;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModelData {
    private List<ModelInput> components = new ArrayList<>();
    private List<ModelInput> inputs = new ArrayList<>();
    private List<ModelInput> param = new ArrayList<>();
    private List<Double> faultInjectionTimes = new ArrayList<>();

    public Integer getFault(String compName, String faultName){
        for(ModelInput comp : components){
            if(comp.getOriginalName().equals(compName)){
                for(Object fault : comp.getValues()){
                    if(fault.equals(faultName))
                        return comp.getValues().indexOf(fault) + 1;
                }
                return null;
            }
        }
        return null;
    }
}
