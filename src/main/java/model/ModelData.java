package model;

import lombok.Data;
import org.apache.commons.lang3.Pair;

import java.util.*;

@Data
public class ModelData {
    private List<Component> componentsToRead = new ArrayList<>();
    private List<ModelInput> modeAssigmentVars = new ArrayList<>();
    private List<ModelInput> inputs = new ArrayList<>();
    private List<ModelInput> param = new ArrayList<>();
    private Pair<String , String> plot;

    public Integer getEnumValue(String compName, String faultName){
        for(ModelInput comp : modeAssigmentVars){
            if(comp.getName().equals(compName)){
                for(Object fault : comp.getValues()){
                    if(fault.equals(faultName))
                        return comp.getValues().indexOf(fault) + 1;
                }
            }
        }
        for(ModelInput comp : inputs){
            if(comp.getName().equals(compName)){
                for(Object fault : comp.getValues()){
                    if(fault.equals(faultName))
                        return comp.getValues().indexOf(fault) + 1;
                }
            }
        }
        for(ModelInput comp : param){
            if(comp.getName().equals(compName)){
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
        for(ModelInput mid : modeAssigmentVars)
            res.add(new Component(mid.getName(), mid.getType(), mid.getValues().indexOf("ok") + 1));
        return res;
    }
    public Type getType(String name){
        for (ModelInput mid : modeAssigmentVars) {
            if (mid.getName().equals(name))
                return mid.getType();
        }

        for (ModelInput mid : param) {
            if (mid.getName().equals(name))
                return mid.getType();
        }

        for (ModelInput mid : inputs) {
            if (mid.getName().equals(name))
                return mid.getType();
        }
        System.err.println("Type not found in provided model data");
        System.exit(1);
        return null;
    }
    public boolean isModeAssigmentVar(String name){
        for(ModelInput comp : modeAssigmentVars) {
            if (comp.getName().equals(name))
                return true;
        }
        return false;
    }
    public boolean eachTypeHasValue(){
        for(ModelInput mi : modeAssigmentVars)
            if(mi.getValues() == null || !mi.getValues().isEmpty())
                return false;
        for(ModelInput mi : inputs)
            if(mi.getValues() == null || !mi.getValues().isEmpty())
                return false;
        for(ModelInput mi : param)
            if(mi.getValues() == null || !mi.getValues().isEmpty())
                return false;
        return true;
    }
    public Integer getFaultIndex(String valueName) {
        for (ModelInput mid : modeAssigmentVars) {
            if (mid.getValues().contains(valueName))
                return mid.getValues().indexOf(valueName) + 1;
        }
        return null;
    }
    public Component getToReadByName(String name){
        for(Component comp: componentsToRead){
            if(comp.getName().equals(name))
                return comp;
        }
        return null;
    }
}
