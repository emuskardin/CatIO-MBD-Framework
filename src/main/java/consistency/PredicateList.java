package consistency;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

// In PicoSAT 0 represent end of the line and it is reserved, so index + 1 is used to represent a predicate
public class PredicateList {
    private List<String> pmU = new ArrayList<>();

    public Integer get(String predicate){
        if(!pmU.contains(predicate))
            pmU.add(predicate);
        return pmU.indexOf(predicate) + 1;
    }

    public int getSize() { return pmU.size(); }
    public String getPredicateName(int n){ return pmU.get(Math.abs(n) - 1); }

}
