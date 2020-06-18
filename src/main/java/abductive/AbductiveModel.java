package abductive;

import atms.ATMSNode;
import atms.ATMSTextInterface;
import atms.ATMSystem;
import atms_gui.Converter4ATMS;
import compiler.LSentence;
import compiler.LogicParser;
import lombok.Data;
import util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Data
public class AbductiveModel {
    private Set<String> rules = new HashSet<>();
    private List<String> observations = new ArrayList<>();
    private ATMSystem atms = null;

    public AbductiveModel(){ }

    public AbductiveModel(String filePath){
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            content = Util.removeComments(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        rules = new HashSet<>(Arrays.asList(content.split("\\.")));
    }

    public void addRule(String rule){
        rules.add(rule);
    }

    public void tryToExplain(Set<String> symptoms){
        observations.add(String.join(",", symptoms) + " -> explain.");
    }

    private String getModelAndObs(){
        StringBuilder sb = new StringBuilder();
        rules.forEach(sb::append);
        observations.forEach(sb::append);
        return sb.toString();
    }

    private Set<Set<String>> getExplanation(){
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
            System.out.println("No explanation");
            expl = "No explanation";
        }else
            expl = explenation.toString();
        observations.clear();
        return expl;
    }

    public void modelToFile(String filename){
        try {
            FileWriter fw = new FileWriter(filename);
            rules.forEach(it -> {
                try {
                    fw.write(it + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
