package experiments;

import lombok.Data;
import util.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
class Node{
    Integer value;
    String name;
    List<Integer> parentVals;
    Set<Integer> children = new HashSet<>();
    Set<Integer> parents = new HashSet<>();

    Node(Integer value, String name, List<Integer> parents){
        this.value = value;
        this.name = name;
        this.parentVals = parents;
    }
}

public class DAG {
    private String fileContent;
    private List<String> lines = new ArrayList<>();
    private List<Integer> inputs = new ArrayList<>();
    private List<Integer> outputs = new ArrayList<>();
    private Map<Integer, Node> nodes = new HashMap<>();
    private Node rootNode = new Node(null, "Root", null);
    private Map<Integer, Integer> benchToCnfMap = new HashMap<>();

    public DAG(String filename){
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.getLocalizedMessage();
            System.exit(1);
        }
    }

    public void getLabels(){
        parseFileContent();
        Map<Integer, List<String>> outputLabels = new HashMap<>();
        for(Integer output : outputs)
            outputLabels.put(output, computeLabel(nodes.get(output)));

        Util.writeToJson(outputLabels);
    }
    private void constructTree(){
        List<Node> leftToExpand = new ArrayList<>();
        for(Node node : nodes.values()) {
            if (node.parentVals.isEmpty()) {
                rootNode.children.add(node.value);
                leftToExpand.add(node);
            }
        }

        while(!leftToExpand.isEmpty()){
            Node inProcess = leftToExpand.remove(0);
            for(Node node : nodes.values()){
                if(node.parentVals.contains(inProcess.value)){
                    inProcess.children.add(node.value);
                    node.parents.add(inProcess.value);
                    leftToExpand.add(node);
                }
            }
        }
    }

    public void parseFileContent(){
        String[] tmpLines = fileContent.split("\n");
        for(String line : tmpLines) {
            if(!line.isEmpty() && line.charAt(0) == '#')
                continue;
            lines.add(line.replaceAll("\\s+", ""));
        }

        getInputs();
        //parseLine();
        //constructTree();
        getOutputs();
    }

    private void parseLine(){
        for(String line : lines) {
            if (line.contains("=")) {
                Integer value = Integer.parseInt(line.substring(0, line.indexOf("=")));
                String name = line.substring(line.indexOf("=") + 1);
                List<Integer> parents = getParenthesesContent(name);
                parents.removeAll(inputs);
                nodes.put(value, new Node(value, name, parents));
            }
        }
    }
    private void getInputs() {
        for (String line : lines)
            if (line.contains("INPUT"))
                inputs.addAll((getParenthesesContent(line)));
    }

    private void getOutputs() {
        Set<Integer> keyset = nodes.keySet();
        for (String line : lines) {
            if (line.contains("OUTPUT")) {
                List<Integer> vals = getParenthesesContent(line);
                outputs.addAll(vals);
                for (Integer val : vals) {
                    if (!keyset.contains(val))
                        nodes.put(val, new Node(val, "OUTPUT(" + val + ")", Collections.emptyList()));
                }
            }
        }
    }

    private List<Integer> getParenthesesContent(String line){
        List<Integer> content = new ArrayList<>();
        for(String numberStr : line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(","))
            content.add(Integer.parseInt(numberStr));
        return content;
    }

    private Set<Node> breathFirstTraversal(){
        Set<Node> visited = new LinkedHashSet<>();
        List<Node> descendants = new ArrayList<>();
        descendants.add(rootNode);
        while(!descendants.isEmpty()){
            Node curr = descendants.remove(0);
            for(Integer key : curr.children){
                if(!visited.contains(nodes.get(key))) {
                    visited.add(nodes.get(key));
                    descendants.add(nodes.get(key));
                }
            }
        }
        return visited;
    }

    // all parents plus itself
    private List<String> computeLabel(Node node){
        Set<Node> visited = new LinkedHashSet<>();
        List<Node> parents = new ArrayList<>();
        parents.add(node);
        while(!parents.isEmpty()){
            Node curr = parents.remove(0);
            for(Integer key : curr.parents){
                if(!visited.contains(nodes.get(key))){
                    visited.add(nodes.get(key));
                    parents.add(nodes.get(key));
                }
            }
        }
        List<String> res = new ArrayList<>();
        res.add(node.name);
        for(Node n : visited)
            res.add(n.name);
        return res;
    }

    public void parseInputOutputCNFNumbers(String filename){
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.getLocalizedMessage();
            System.exit(1);
        }
        List<String> content = Arrays.asList(fileContent.split("\n"));
        for(String line : content){
            if(line.charAt(0) == 'c' && line.contains("input")){
                String[] vals = line.split(" ");
                Integer cnf = Integer.parseInt(vals[1]);
                Integer bench = null;
                Matcher m = Pattern.compile("[0-9]+").matcher(line);
                while (m.find())
                    bench = Integer.parseInt(m.group());
                benchToCnfMap.put(bench, cnf);
            }
            else if(line.charAt(0) == 'c' && line.contains("output")){
                String[] vals = line.split(" ");
                Integer cnf = Integer.parseInt(vals[1]);
                Integer bench = null;
                Matcher m = Pattern.compile("[0-9]+").matcher(line);
                while (m.find())
                    bench = Integer.parseInt(m.group());
                benchToCnfMap.put(bench, cnf);
            }
        }
    }

    public void createTC(){
        try (PrintStream out = new PrintStream(new FileOutputStream("randTest.txt"))) {
            out.println(fileContent);
            for(Integer input : inputs)
                out.println( Math.random() < 0.5? "-" + benchToCnfMap.get(input) : benchToCnfMap.get(input));
            for(Integer output : outputs)
                out.println( Math.random() < 0.5? "-" + benchToCnfMap.get(output) : benchToCnfMap.get(output));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
