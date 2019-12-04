package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ModelInput implements Serializable {
    private String name;
    private Type type;
    private List<Object> values;

}
