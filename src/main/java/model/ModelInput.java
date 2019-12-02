package model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModelInput implements Serializable {
    private String name;
    private List<Object> values;
    private Type type;
    private String originalName;

    public ModelInput(String name, List<Object> values, Type type){
        this.name = name.replace(".", "_");
        this.originalName = name;
        this.values = values;
        this.type = type;
    }
}
