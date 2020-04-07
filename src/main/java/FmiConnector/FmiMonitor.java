package FmiConnector;

import lombok.Data;
import model.Component;
import org.javafmi.wrapper.Simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for reading modelica component values
 */
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
        simulation.reset();
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

    public void writeMultipleComp(List<Component> components){
        for(Component comp : components)
            writeComponent(comp);
    }

    public void writeComponent(Component comp){
        Object value = comp.getValue();
        if(comp.getType() != null){
            switch (comp.getType()) {
                case STRING:
                    simulation.write(comp.getName()).with((String) value);
                    break;
                case BOOLEAN:
                    if(value instanceof String)
                        simulation.write(comp.getName()).with(Boolean.parseBoolean((String) value));
                    else
                        simulation.write(comp.getName()).with((Boolean) value);
                    break;
                case DOUBLE:
                    if(value instanceof String)
                        simulation.write(comp.getName()).with(Double.parseDouble((String) value));
                    else
                        simulation.write(comp.getName()).with((Double) value);
                    break;
                case INTEGER:
                case ENUM:
                    if(value instanceof String)
                        simulation.write(comp.getName()).with(Integer.parseInt((String) value));
                    else
                        simulation.write(comp.getName()).with((Integer) value);
                    break;
            }
        }
        else
            writeVar(comp.getName(), value);
    }

    public void writeVar(String name, Object value){
        if(value instanceof Double)
            simulation.write(name).with((Double) value);
        else if(value instanceof Integer)
            simulation.write(name).with((Integer) value);
        else if(value instanceof Boolean)
            simulation.write(name).with((Boolean) value);
        else
            simulation.write(name).with((String) value);
    }
}
