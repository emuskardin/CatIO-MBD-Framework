package abductive.combinatorial;

import FmiConnector.TYPE;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModelInputData implements Serializable {
    private String name;
    private List<Object> values;
    private TYPE type;
    private String originalName;

    public ModelInputData(String name, List<Object> values, TYPE type){
        this.name = name.replace(".", "_");
        this.originalName = name;
        this.values = values;
        this.type = type;
    }
}
