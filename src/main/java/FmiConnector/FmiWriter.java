package FmiConnector;

import org.javafmi.wrapper.Simulation;

public class FmiWriter {
    private Simulation simulation;

    public FmiWriter(Simulation simulation){
        this.simulation = simulation;
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
