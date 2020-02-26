package consistency.mhsAlgs;

import consistency.SatSolver.PicoSAT;
import consistency.CbModel;

import java.io.IOException;
import java.util.*;

class RcNode {
    Set<Integer> pathFromRoot = new LinkedHashSet<>();
    Set<Integer> filter = new HashSet<>();
    Map<Integer, RcNode> children = new LinkedHashMap<>();
    List<Integer> label = new ArrayList<>();
}

class RcProcessingQueue {
    private Map<Integer, ArrayList<RcNode>> processingQueue = new TreeMap<>();

    void addToQueue(RcNode node){
        int nodePFRLen = node.pathFromRoot.size();
        ArrayList<RcNode> depthList = processingQueue.computeIfAbsent(nodePFRLen, k -> new ArrayList<>());
        depthList.add(node);
    }

    RcNode getNextToProcess(){
        for(int i : processingQueue.keySet()){
            if(processingQueue.get(i) != null && !processingQueue.get(i).isEmpty()){
                return processingQueue.get(i).remove(0);
            }
        }
        return null;
    }
}

public class RcTree {
    private RcProcessingQueue processingQueqe = new RcProcessingQueue();
    private RcNode rootNode = new RcNode();
    private List<RcNode> mhs = new ArrayList<>();
    private CbModel model;
    private List<Integer> observation;
    private List<List<Integer>> conflicts = new ArrayList<>();

    public RcTree(CbModel cbModel, List<Integer> obs) {
        this.model = cbModel;
        this.observation = obs;
    }

    public List<List<Integer>> getDiagnosis() {
        processingQueqe.addToQueue(rootNode);
        RcNode nextToProcess = processingQueqe.getNextToProcess();
        do{
            processNode(nextToProcess);
            nextToProcess = processingQueqe.getNextToProcess();
        }while (nextToProcess != null);

        return getMHS();
    }

    private void processNode(RcNode node) {
        if(closingCheck(node))
            return;

        List<Integer> label = getLabel(node);
        // TODO PRUNING
        if(label.isEmpty()) {
            mhs.add(node);
            return;
        }
        conflicts.add(label);

        node.label = label;
        node.label.forEach( edge -> createChild(node, edge));
    }

    private void createChild(RcNode parent, Integer edge){
        if(parent.filter.contains(edge))
            return;

        RcNode child = new RcNode();
        child.pathFromRoot.addAll(parent.pathFromRoot);
        child.pathFromRoot.add(edge);

        child.filter.addAll(parent.filter);
        child.filter.addAll(parent.children.keySet());

        parent.children.put(edge, child);
        processingQueqe.addToQueue(child);
    }


    private List<Integer> getLabel(RcNode node) {
        try {
            PicoSAT picoSAT = new PicoSAT("formula.txt");
            picoSAT.writeModelAndObsToFile(model.modelWithAb(node.pathFromRoot), observation);
            return picoSAT.getMUS();
        }catch (IOException e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private boolean closingCheck(RcNode node){
        for(RcNode checkedNode : mhs){
            if (isSuperset(node.pathFromRoot, checkedNode.pathFromRoot))
                return true;
        }
        return false;
    }

    private List<List<Integer>> getMHS(){
        List<List<Integer>> diagnosis = new ArrayList<>();
        mhs.forEach( node -> diagnosis.add(new ArrayList<>(node.pathFromRoot)));
        return diagnosis;
    }

    private void attemptPruning(){
        // TODO
    }

    private void updateFilters(){
        // TODO
    }

    private boolean isSuperset(Collection<Integer> x, Collection<Integer> y){
        return x.size() >= y.size() && x.containsAll(y);
    }

    public List<List<Integer>> getConflicts(){
        return conflicts;
    }

}
