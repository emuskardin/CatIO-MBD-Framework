package abductive.combinatorial;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModelData {
    private List<ModelInputData> components = new ArrayList<>();
    private List<ModelInputData> inputs = new ArrayList<>();
    private List<ModelInputData> param = new ArrayList<>();
}
