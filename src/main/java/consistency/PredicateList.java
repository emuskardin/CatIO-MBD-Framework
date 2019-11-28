package consistency;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class PredicateList {
    private List<String> pmU = new ArrayList<>();

    /**
     * If predicate is not present in list, it will be added to it.
     * Indexes start with 1, as 0 is reserved in DIMACS CNF file format.
     * @param predicate string of predicate name
     * @return unique integer representing predicate
     */
    public Integer get(String predicate){
        if(!pmU.contains(predicate))
            pmU.add(predicate);
        return pmU.indexOf(predicate) + 1;
    }

    public int getSize() { return pmU.size(); }
    String getPredicateName(int n){ return pmU.get(Math.abs(n) - 1); }

}
