package abductive;

import atms.ATMSNode;
import atms.ATMSTextInterface;
import atms.ATMSystem;
import atms_gui.Converter4ATMS;
import compiler.LSentence;
import compiler.LogicParser;
import consistency.mhsAlgs.GDE;
import lombok.Data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Data
public class AbductiveModel {
    private String rules;
    private List<String> observations = new ArrayList<>();
    private ATMSystem atms = null;

    public AbductiveModel(){ };
    public AbductiveModel(String filePath){
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        rules = content;
    }

    public void addExplain(List<String> symptoms){
        observations.add(String.join(",", symptoms) + " -> explain.");
    }

    private String getModelAndObs(){
        StringBuilder sb = new StringBuilder(rules);
        observations.forEach(sb::append);
        return sb.toString();
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
        if (!parser.parse(this.getModelAndObs())) {
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
