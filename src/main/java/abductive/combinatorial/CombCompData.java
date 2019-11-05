package abductive.combinatorial;

import FmiConnector.TYPE;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CombCompData {
    private String name;
    private List<Object> values;
    private TYPE type;
}
