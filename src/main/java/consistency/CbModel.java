package consistency;

import lombok.Data;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Data
public class CbModel {
    List<List<Integer>> model = new ArrayList<>();
    List<List<Integer>> workingModel = new ArrayList<>();
    Set<Integer> abPredicates = new HashSet<>();
    PredicateList predicates = new PredicateList();
    int numOfDistinct = 0;

    public CbModel(){ }
    public CbModel(String filepath){
        modelFromFile(filepath);
    }

    public List<int[]> modelToIntArr(){
        List<int[]> res = new ArrayList<>();
        for(List<Integer> line: workingModel)
            res.add(lineToArr(line));
        return res;
    }

    private int[] lineToArr(List<Integer> line){
        return line.stream().mapToInt(Integer::intValue).toArray();
    }

    private void addCnfClause(List<String> params){
        ArrayList<Integer> paramToInt = new ArrayList<>();
        for(String param : params){
            if(param.charAt(0) == '~' || param.charAt(0) == '!')
                paramToInt.add(-predicates.get(param.substring(1)));
            else
                paramToInt.add(predicates.get(param));
        }
        model.add(paramToInt);
    }

    public void modelFromFile(String filename){
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.getLocalizedMessage();
            System.exit(1);
        }
        if(content.contains("->") || content.contains("&") || content.contains("<->") || content.contains("|"))
            modelToCNF(content);
        else {
            String[] clauses = content.split("\\r?\\n");
            List<List<String>> cl = new ArrayList<>();
            for(String clause : clauses){
                String tmpClause = clause.replaceAll("\\s+","");
                List<String> literals = Arrays.asList(tmpClause.split(","));
                cl.add(literals);
            }
            processAndAddClauses(cl);
        }
    }

    public void modelToCNF(String propModel) {
        propModel = propModel.replace("\n", "").replace("\r", "");
        String[] lines = propModel.split("\\.");
        for (int i = 0; i < lines.length; i++)
            lines[i] = "(" + lines[i] + ")";
        String formula = String.join("&", lines);
        formula = formula.replace('-', '=');
        formula = formula.replace('!', '~');
        final FormulaFactory f = new FormulaFactory();
        Formula cnf;
        try {
            final PropositionalParser p = new PropositionalParser(f);
            cnf = p.parse(formula).cnf();
        }catch (ParserException pe){
            System.out.println(pe.getLocalizedMessage());
            return;
        }
        String trimedCnf = cnf.toString().replaceAll("\\s+","");
        String[] clauses= trimedCnf.split("&");
        List<List<String>> clauseLiterals = new ArrayList<>();
        for(String clause : clauses) {
            String working = clause;
            if(working.charAt(0) == '(')
                working = working.substring(1, working.length()-1);
            clauseLiterals.add(Arrays.asList(working.split("\\|")));
        }

        processAndAddClauses(clauseLiterals);
    }

    private void processAndAddClauses(List<List<String>> clauses){
        List<List<String>> hsToFront = new ArrayList<>(clauses);
        for(List<String> clause : clauses) {
            if(clause.size() == 1) {
                String lit = clause.get(0);
                boolean neg = (lit.charAt(0) == '~' || lit.charAt(0) == '!');
                if ((neg && Character.isUpperCase(lit.charAt(1))) || Character.isUpperCase(lit.charAt(0))){
                    addHealthStatePredicate(neg ? lit.substring(1) : lit);
                    addCnfClause(Collections.singletonList(lit));
                    hsToFront.remove(clause);
                }
            }
        }
        hsToFront.forEach(this::addCnfClause);
    }

    private void addHealthStatePredicate(String ab){
        abPredicates.add(predicates.get(ab));
        abPredicates.add(-predicates.get(ab));
    }

    public List<Integer> observationToInt(List<String> observations){
        List<Integer> res = new ArrayList<>();
        observations.forEach(obs -> {
            if(obs.charAt(0) == '!')
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

    public List<String> diagnosisToComponentNames(List<Integer> mhs, Integer offset){
        // TODO FIX
        List<String> res = new ArrayList<>();
        mhs.forEach( it -> {
            int timeStep = it / offset; // TODO possible error due to offset for intermittent and persistent faults
            int index = it % model.size();
            res.add(predicates.getPredicateName( model.get(index).get(0))); //+ "_" + timeStep + "sec"
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

    public List<List<String>> modelToString(){
        List<List<String>> res = new ArrayList<>();
        for(List<Integer> clause : model){
            List<String> tmpClause = new ArrayList<>();
            for(Integer lit : clause) {
                String name = predicates.getPredicateName(lit);
                if(lit < 0)
                    name = "!" + name;
                tmpClause.add(name);
            }
            res.add(tmpClause);
        }
        return res;
    }
    
}
