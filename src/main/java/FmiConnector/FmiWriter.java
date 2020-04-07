package FmiConnector;

import lombok.Data;
import model.Component;
import org.javafmi.wrapper.Simulation;

import java.util.List;

@Data
public class FmiWriter {
    private Simulation simulation;

    public FmiWriter(Simulation simulation){
        this.simulation = simulation;
    }



}
