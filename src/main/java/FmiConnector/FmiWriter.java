package FmiConnector;

import lombok.Data;
import model.Component;
import model.Type;
import org.javafmi.wrapper.Simulation;

import java.util.List;

@Data
public class FmiWriter {
    private Simulation simulation;

    public FmiWriter(Simulation simulation){
        this.simulation = simulation;
    }

    public void writeMultipleComp(List<Component> components){
        for(Component comp : components)
            writeComponent(comp);
    }

    public void writeComponent(Component comp){
        Object value = comp.getValue();
        // TODO REFACTOR IF STRING IT INSANCE
        if(comp.getType() != null){
            switch (comp.getType()) {
                case STRING:
                    simulation.write(comp.getName()).with((String) value);
                    break;
                case BOOLEAN:
                    simulation.write(comp.getName()).with(Boolean.parseBoolean((String) value));
                    break;
                case DOUBLE:
                    simulation.write(comp.getName()).with(Double.parseDouble((String) value));
                    break;
                case INTEGER:
                case ENUM:
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
