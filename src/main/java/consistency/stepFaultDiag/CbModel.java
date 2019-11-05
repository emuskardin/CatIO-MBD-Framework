package consistency.stepFaultDiag;

import consistency.PredicateList;
import lombok.Data;

import java.util.*;

@Data
public class CbModel {
    List<List<Integer>> model = new ArrayList<>();
    List<List<Integer>> workingModel = new ArrayList<>();
    Set<Integer> abPredicates = new HashSet<>();
    PredicateList predicates = new PredicateList();
    int numOfDistinct = 0;

    public List<int[]> modelToIntArr(){
        List<int[]> res = new ArrayList<>();
        for(List<Integer> line: workingModel)
            res.add(lineToArr(line));
        return res;
    }

    private int[] lineToArr(List<Integer> line){
        return line.stream().mapToInt(Integer::intValue).toArray();
    }

    public void addCNFClause(String... params){
        ArrayList<Integer> paramToInt = new ArrayList<>();
        for(String param : params){
            if(param.charAt(0) == '-')
                paramToInt.add(-predicates.get(param.substring(1)));
            else
                paramToInt.add(predicates.get(param));
        }
        model.add(paramToInt);
    }

    public void addHealthStatePredicate(String ab){
        addCNFClause("-" + ab);
        abPredicates.add(predicates.get(ab));
        abPredicates.add(-predicates.get(ab));
    }

    public List<Integer> observationToInt(List<String> observations){
        List<Integer> res = new ArrayList<>();
        observations.forEach(obs -> {
            if(obs.charAt(0) == '-')
                res.add(-predicates.get(obs.substring(1)));
            else
                res.add(predicates.get(obs));
        });
        return res;
    }

    public CbModel modelWithAb(Set<Integer> abToChange){
        // To enable diagnosis at each time step
        if(workingModel.isEmpty())
            workingModel = model;

        CbModel abModel = new CbModel();
        abModel.predicates = this.predicates;
        abModel.abPredicates = this.abPredicates;
        abModel.numOfDistinct = this.numOfDistinct;

        for(int index = 0; index < workingModel.size(); index++){
            List<Integer> abClause= new ArrayList<>();
            if(abToChange.contains(index))
                abClause.add(- workingModel.get(index).get(0));
            else
                abClause.addAll(workingModel.get(index));
            abModel.workingModel.add(abClause);
        }
        return abModel;
    }

    public void increaseByOffset(Boolean increaseHS, Integer currStep){
        // abPredicates size / 2 as we initially save both ab and -ab values
        int offset = increaseHS ? predicates.getSize() * currStep : (predicates.getSize() - (abPredicates.size()/2)) * currStep;
        // Increase each integer by offset
        for(List<Integer> clause: model){
            List<Integer> incCNF = new ArrayList<>();

            for(Integer lit : clause) {
                if (!increaseHS && abPredicates.contains(lit)) {
                    if(clause.size() == 1 && currStep >= 1)
                        continue;
                    incCNF.add(lit);
                }
                else {
                    Integer updatedLit = lit > 0 ? lit + offset : lit - offset;
                    if(abPredicates.contains(lit))
                        abPredicates.add(updatedLit);
                    incCNF.add(updatedLit);
                }
            }
            if (!incCNF.isEmpty())
                workingModel.add(incCNF);
        }
    }

    public List<String> diagToComp(List<Integer> mhs, Integer offset){
        // TODO FIX
        List<String> res = new ArrayList<>();
        mhs.forEach( it -> {
            int timeStep = it / offset; // TODO possible error due to offset for intermittent and persistent faults
            int index = it % model.size();
            res.add(predicates.getPredicateName( model.get(index).get(0)) + "_" + timeStep + "sec");
        });
        return res;
    }

    void clearModel(){
        model = new ArrayList<>();
        workingModel = new ArrayList<>();
        abPredicates = new HashSet<>();
        predicates = new PredicateList();
        numOfDistinct = 0;
    }
}
