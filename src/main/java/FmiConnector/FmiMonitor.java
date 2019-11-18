package FmiConnector;

import lombok.Data;
import org.javafmi.wrapper.Simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FmiMonitor {
    private Simulation simulation;
    private List<Component> components;
    private String pathToFmi;
    private FmiWriter fmiWriter;

    public FmiMonitor(String pathToFmi){
        this.pathToFmi = pathToFmi;
        simulation = new Simulation(pathToFmi);
        fmiWriter = new FmiWriter(simulation);
    }

    public void resetSimulation(){
        simulation = new Simulation(pathToFmi);
        fmiWriter.setSimulation(simulation);
    }

    public Component read(Component comp){
        switch (comp.getType()){
            case DOUBLE:  {
                comp.setValue(simulation.read(comp.getName()).asDouble());
                break;
            }
            case INTEGER: {
                comp.setValue(simulation.read(comp.getName()).asInteger());
                break;
            }
            case STRING:  {
                comp.setValue(simulation.read(comp.getName()).asString());
                break;
            }
            case BOOLEAN: {
                comp.setValue(simulation.read(comp.getName()).asBoolean());
                break;
            }
            case ENUM: comp.setValue(simulation.read(comp.getName()).asEnumeration());
        }
        return comp;
    }

    public Map<String, Object> readMultiple(List<Component> components){
        Map<String , Object> componentValueMap = new HashMap<>();
        components.forEach(component -> {
            Component comp = read(component);
            componentValueMap.put(comp.getName(), comp.getValue());
        });
        return componentValueMap;
    }
}
