package model;

import lombok.Data;

import java.util.*;

@Data
public class ModelData {
    private List<Component> componentsToRead = new ArrayList<>();
    private List<ModelInput> healthStates = new ArrayList<>();
    private List<ModelInput> inputs = new ArrayList<>();
    private List<ModelInput> param = new ArrayList<>();
    private List<Scenario> scenarios;

    public Integer getFault(String compName, String faultName){
        for(ModelInput comp : healthStates){
            if(comp.getOriginalName().equals(compName)){
                for(Object fault : comp.getValues()){
                    if(fault.equals(faultName))
                        return comp.getValues().indexOf(fault) + 1;
                }
            }
        }
        System.err.println(compName + " does not contain state " + faultName);
        return null;
    }

    public List<Component> getAllOkStates(){
        List<Component> res = new ArrayList<>();
        for(ModelInput mid : healthStates)
            res.add(new Component(mid.getOriginalName(), mid.getValues().indexOf("ok") + 1));
        return res;
    }

    public Type getType(String name){
        for (ModelInput mid : healthStates) {
            if (mid.getOriginalName().equals(name))
                return mid.getType();
        }

        for (ModelInput mid : param) {
            if (mid.getOriginalName().equals(name))
                return mid.getType();
        }

        for (ModelInput mid : inputs) {
            if (mid.getOriginalName().equals(name))
                return mid.getType();
        }
        System.err.println("Type not found in provided model data");
        System.exit(1);
        return null;
    }

    public boolean isHS(String name){
        for(ModelInput comp : healthStates) {
            if (comp.getOriginalName().equals(name))
                return true;
        }
        return false;
    }
}
