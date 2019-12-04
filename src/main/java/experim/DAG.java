package experim;

import lombok.Data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class ValueNodesMap{
    Map<Integer, Set<Node>> map = new LinkedHashMap<>();

    void addToMap(Integer value, Node node){
        map.computeIfAbsent(value, set -> new HashSet<>());
        map.get(value).add(node);
    }

    Set<Integer> getKeyset(){
        return map.keySet();
    }

    Set<Node> getNodes(Integer key){
        return map.get(key);
    }
}

@Data
class Node{
    Integer value;
    String name;
    List<Integer> parentVals;
    ValueNodesMap children = new ValueNodesMap();
    ValueNodesMap parents = new ValueNodesMap();

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
    private List<Node> nodes = new ArrayList<>();
    private Node rootNode = new Node(null, "Root", null);

    public DAG(String filename){
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.getLocalizedMessage();
            System.exit(1);
        }

        parseFileContent();
        System.out.println("Inputs  :" + inputs);
        System.out.println("Outputs :" + outputs);
        for(Node node: breathFirstTraversal()){
            System.out.println(node.name + " children of " + getAllParents(node));
        }
    }

    private void constructTree(){
        List<Node> leftToExpand = new ArrayList<>();
        for(Node node : nodes) {
            if (node.parentVals.isEmpty()) {
                rootNode.children.addToMap(node.value, node);
                leftToExpand.add(node);
            }
        }

        while(!leftToExpand.isEmpty()){
            Node inProcess = leftToExpand.remove(0);
            for(Node node : nodes){
                if(node.parentVals.contains(inProcess.value)){
                    inProcess.children.addToMap(node.value, node);
                    node.parents.addToMap(inProcess.value, inProcess);
                    leftToExpand.add(node);
                }
            }
        }
    }

    private void parseFileContent(){
        String[] tmpLines = fileContent.split("\n");
        for(String line : tmpLines) {
            if(!line.isEmpty() && line.charAt(0) == '#')
                continue;
            lines.add(line.replaceAll("\\s+", ""));
        }
        addInputsAndOutputs(lines);
        parseLine(lines);
        constructTree();

    }

    private void parseLine(List<String> lines){
        for(String line : lines) {
            if (line.contains("=")) {
                Integer value = Integer.parseInt(line.substring(0, line.indexOf("=")));
                String name = line.substring(line.indexOf("=") + 1);
                List<Integer> parents = getParenthesesContent(name);
                parents.removeAll(inputs);
                nodes.add(new Node(value, name, parents));
            }
        }
    }
    private void addInputsAndOutputs(List<String> lines){
        for(String line : lines){
            if(line.contains("INPUT"))
                inputs.addAll(getParenthesesContent(line));
            else if(line.contains("OUTPUT"))
                outputs.addAll(getParenthesesContent(line));
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
            for(Integer key : curr.children.getKeyset()){
                if(!visited.contains(curr.children.getNodes(key))){
                    visited.addAll(curr.children.getNodes(key));
                    descendants.addAll(curr.children.getNodes(key));
                }
            }
        }
        return visited;
    }

    private List<String> getAllParents(Node node){
        Set<Node> visited = new LinkedHashSet<>();
        List<Node> parents = new ArrayList<>();
        parents.add(node);
        while(!parents.isEmpty()){
            Node curr = parents.remove(0);
            for(Integer key : curr.parents.getKeyset()){
                if(!visited.contains(curr.parents.getNodes(key))){
                    visited.addAll(curr.parents.getNodes(key));
                    parents.addAll(curr.parents.getNodes(key));
                }
            }
        }
        List<String> res = new ArrayList<>();
        for(Node n : visited)
            res.add(n.name);
        return res;
    }
}
