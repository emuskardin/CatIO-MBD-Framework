package consistency;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class PredicateList {
    private List<String> predicateList = new ArrayList<>();

    /**
     * If predicate is not present in list, it will be added to it.
     * Indexes start with 1, as 0 is reserved in DIMACS CNF file format.
     * @param predicate string of predicate name
     * @return unique integer representing predicate
     */
    public Integer get(String predicate){
        if(!predicateList.contains(predicate))
            predicateList.add(predicate);
        return predicateList.indexOf(predicate) + 1;
    }

    public int getSize() { return predicateList.size(); }
    String getPredicateName(int n){ return predicateList.get(Math.abs(n) - 1); }

}
