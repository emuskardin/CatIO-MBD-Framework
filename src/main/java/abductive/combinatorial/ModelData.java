package abductive.combinatorial;

import lombok.Data;

import java.util.List;

@Data
public class ModelData {
    private String pathToFmi;
    private List<ModelInputData> components;
    private List<ModelInputData> inputs;
    private List<ModelInputData> param;
}
