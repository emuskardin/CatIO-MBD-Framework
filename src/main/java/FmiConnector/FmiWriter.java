package FmiConnector;

import lombok.Data;
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
        if(value instanceof String){
            if(comp.getType() == TYPE.STRING)
                simulation.write(comp.getName()).with((String) value);
            else{
                if(comp.getType() == TYPE.BOOLEAN)
                    simulation.write(comp.getName()).with(Boolean.parseBoolean((String) value));
                else if(comp.getType() == TYPE.INTEGER)
                    simulation.write(comp.getName()).with(Integer.parseInt((String) value));
                else if(comp.getType() == TYPE.DOUBLE)
                    simulation.write(comp.getName()).with(Double.parseDouble((String) value));
            }
        }else
            writeVar(comp.getName(), value);
    }

    public void writeVar(String name, Object value){
        if(value instanceof Integer)
            simulation.write(name).with((Integer) value);
        else if(value instanceof Double)
            simulation.write(name).with((Double) value);
        else if(value instanceof Boolean)
            simulation.write(name).with((Boolean) value);
        else
            simulation.write(name).with((String) value);
    }

}
