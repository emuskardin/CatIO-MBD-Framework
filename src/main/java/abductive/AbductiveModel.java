package abductive;

import atms.ATMSNode;
import atms.ATMSTextInterface;
import atms.ATMSystem;
import atms_gui.Converter4ATMS;
import compiler.LSentence;
import compiler.LogicParser;
import consistency.mhsAlgs.GDE;

import java.util.*;

public class AbductiveModel {
    private List<String> rules = new ArrayList<>();
    private List<String> observations = new ArrayList<>();
    private ATMSystem atms = null;

    public void addRule(String rule){ rules.add(rule + ". "); }

    private String getRules(){
        StringBuilder sb = new StringBuilder();
        rules.forEach(sb::append);
        observations.forEach(sb::append);
        return sb.toString();
    }

    public void addObservation(String symptoms){
        observations.add(symptoms + ". ");
    }

    public void addExplain(List<String> symptoms){
        observations.add(String.join(",", symptoms) + " -> explain.");
    }

    public Set<Set<String>> getExplanation(){
        Set<Set<String>> res = Collections.emptySet();
        for(ATMSNode node : atms.nodes){
            if(node.identifier.equals("explain")){
                res = node.label.toSet();
                break;
            }
        }
        return res;
    }

    public String getDiagnosis() {
        LogicParser parser = new LogicParser();
        String expl;
        if (!parser.parse(this.getRules())) {
            return "Parsing Error";
        }
        LSentence result = (LSentence)parser.result();
        LinkedList<LinkedList<String>> list = Converter4ATMS.convert(result);

        atms = ATMSTextInterface.create(list).atms;
        Set<Set<String>> explenation = getExplanation();
        if(explenation.isEmpty()){
            System.out.println("No explenation");
            GDE gde = new GDE(atms.nogood.label.toSet());
            expl = "Diag " + gde.getMUS();
        }else
            expl = explenation.toString();
        observations.clear();
        return expl;
    }
}
