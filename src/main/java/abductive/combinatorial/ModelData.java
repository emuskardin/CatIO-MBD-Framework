package abductive.combinatorial;

import lombok.Data;

import java.util.List;

@Data
public class ModelData {
    private String pathToFmi;
    private List<CombCompData> components;
    private List<CombCompData> inputs;
    private List<CombCompData> param;
}
