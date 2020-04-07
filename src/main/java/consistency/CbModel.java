package consistency;

import lombok.Data;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import util.Util;

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
            e.printStackTrace();
            System.exit(1);
        }
        if(content.contains("->") || content.contains("&") || content.contains("<->") || content.contains("|")) {
            try {
                modelToCNF(content);
            } catch (ParserException e) {
                e.printStackTrace();
            }
        }
        else {
            content = Util.removeComments(content);
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

    public void modelToCNF(String propModel) throws ParserException {
        propModel = Util.removeComments(propModel);
        propModel = propModel.replace("\n", "").replace("\r", "");
        String[] lines = propModel.split("\\.");
        for (int i = 0; i < lines.length; i++)
            lines[i] = "(" + lines[i] + ")";
        String formula = String.join("&", lines);
        formula = formula.replace('-', '=');
        formula = formula.replace('!', '~');
        final FormulaFactory f = new FormulaFactory();
        final PropositionalParser p = new PropositionalParser(f);
        final Formula cnf = p.parse(formula).cnf();
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
        abPredicates.add(Math.abs(predicates.get(ab)));
    }

    public boolean isHealthStatePredicate(Integer ab){
        return abPredicates.contains(Math.abs(ab));
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
        //abModel.predicates = this.predicates;
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
        int offset = increaseHS ? predicates.getSize() * currStep : (predicates.getSize() - abPredicates.size()) * currStep;
        // Increase each integer by offset
        for(List<Integer> clause: model){
            List<Integer> incCNF = new ArrayList<>();
            for(Integer lit : clause) {
                if (!increaseHS && isHealthStatePredicate(lit)) {
                    if(clause.size() == 1 && currStep >= 1)
                        continue;
                    incCNF.add(lit);
                }
                else {
                    Integer updatedLit = lit > 0 ? lit + offset : lit - offset;
                    if(isHealthStatePredicate(lit))
                        abPredicates.add(updatedLit);
                    incCNF.add(updatedLit);
                }
            }
            if (!incCNF.isEmpty())
                workingModel.add(incCNF);
        }
    }

    public List<String> diagnosisToComponentNames(List<Integer> mhs){
        List<String> res = new ArrayList<>();
        mhs.forEach( it -> {
            int index = it % model.size();
            res.add(predicates.getPredicateName(model.get(index).get(0)));
        });
        return res;
    }

    public List<String> getComponentNamesTimed(List<Integer> mhs , ConsistencyType type, int obsSize){
        List<String> res = new ArrayList<>();
        int offset = predicates.getSize() + obsSize;

        mhs.forEach( it -> {
            int timeStep = (it / offset);
            int index = it % model.size();
            if(type == ConsistencyType.INTERMITTENT)
                res.add(predicates.getPredicateName(model.get(index).get(0)) + "_" + timeStep);
            else
                res.add(predicates.getPredicateName(model.get(index).get(0)));
        });
        return res;
    }

    void clearModel(){
        workingModel = new ArrayList<>();
        abPredicates = new HashSet<>(abPredicates);
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
