package abductive.combinatorial;

import FmiConnector.Component;
import FmiConnector.Type;
import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.service.engine.IpoEngine;
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Data
public class MLCA {
    SUT sut;
    IpoEngine engine;
    ArrayList<Parameter> inputs;
    ArrayList<Parameter> params;
    ArrayList<Parameter> components;
    ModelData md;

    public MLCA(ModelData modelData){
        sut = new SUT();
        engine = new IpoEngine(sut);
        components = addParam(modelData.getComponents());
        inputs = addParam(modelData.getInputs());
        params = addParam(modelData.getParam());
        md = modelData;
    }

    public void createTestSuite(String filename){
        engine.build();
        TestSet ts = engine.getTestSet();
        TestSetWrapper wrapper = new TestSetWrapper(ts, sut);
        wrapper.outputInCSVFormat(filename);
    }

    public List<List<Component>> suitToSimulationInput(String filepath) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(filepath));
        List<List<Component>> testSuite = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();

        String line = fileReader.readLine();
        while (line != null){
            if(line.charAt(0) == '#'){
                line = fileReader.readLine();
                continue;
            } else if(nameList.isEmpty()){
                nameList.addAll(Arrays.asList(line.split(",")));
                for (int i = 0; i < nameList.size(); i++)
                    nameList.set(i, getOriginalName(nameList.get(i)));
            }else {
                List<String> test = Arrays.asList(line.split(","));
                List<Component> rowInput = new ArrayList<>();
                for(int i = 0; i < test.size(); i++) {
                    Type type = getType(nameList.get(i));
                    Component comp = new Component(nameList.get(i), type);
                    Integer compValue = componentIndexNum(test.get(i));
                    if(compValue != null)
                        comp.setValue(compValue);
                    else
                        comp.setValue(test.get(i));
                    rowInput.add(comp);
                }
                testSuite.add(rowInput);
            }
            line = fileReader.readLine();
        }
        return testSuite;
    }

    public List<Component> getAllOkStates(){
        List<Component> res = new ArrayList<>();
        for(ModelInput mid : md.getComponents()){
            res.add(new Component(mid.getOriginalName(), mid.getValues().indexOf("ok") + 1));
        }
        return res;
    }

    public void addRelationToGroup(List<Parameter> comps, Integer relStrength){
        Relation relation = new Relation(relStrength);
        comps.forEach(relation::addParam);
        sut.addRelation(relation);
    }

    private ArrayList<Parameter> addParam(List<ModelInput> components){
        if(components == null)
            return null;
        ArrayList<Parameter> params = new ArrayList<>();
        for(ModelInput compIter : components){
            Parameter comp = sut.addParam(compIter.getName());
            compIter.getValues().forEach( val -> comp.addValue(val.toString()));
            switch (compIter.getType()){
                case ENUM:
                case DOUBLE:
                case STRING: {
                    comp.setType(Parameter.PARAM_TYPE_ENUM);
                    break;
                }
                case BOOLEAN:{
                    comp.setType(Parameter.PARAM_TYPE_BOOL);
                    break;
                }
                case INTEGER:{
                    comp.setType(Parameter.PARAM_TYPE_INT);
                    break;
                }
            }
            params.add(comp);
        }
        return params;
    }

    public void numberOfCorrectComps(Integer num){
        sut.addConstraint(new Constraint(correctCompConstraintBuilder(components, num), components));
    }

    public void numberOfCorrectComps(List<Integer> nums){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.size(); i++) {
            if(i > 0)
                sb.append(" || ");
            sb.append(correctCompConstraintBuilder(components, nums.get(i)));
        }
        sut.addConstraint(new Constraint(sb.toString(), components));
    }

    public void addConstraint(String constraint){
        sut.addConstraint(new Constraint(constraint, sut.getParameters()));
    }

    private String correctCompConstraintBuilder(ArrayList<Parameter> params, Integer correctNum){
        StringBuilder sb = new StringBuilder();
        List<int[]> combinations = generate(params.size(), correctNum);
        for(int[] comb: combinations){
            sb.append("(");
            int index = 0;
            for (int value : comb) {
                if (index > 0)
                    sb.append(" && ");
                sb.append(params.get(value).getName()).append("=\"ok\"");
                index++;
            }
            sb.append(") ||");
        }

        sb.setLength(sb.length() - 3);
        return sb.toString();
    }

    // Boilerplate
    private Integer componentIndexNum(String valueName){
        if(md.getComponents() != null) {
            for (ModelInput mid : md.getComponents()) {
                if (mid.getValues().contains(valueName))
                    return mid.getValues().indexOf(valueName) + 1;
            }
        }
        return null;
    }
    private String getOriginalName(String name){
        if(md.getComponents() != null) {
            for (ModelInput mid : md.getComponents()) {
                if (mid.getName().equals(name))
                    return mid.getOriginalName();
            }
        }
        if(md.getParam() != null) {
            for (ModelInput mid : md.getParam()) {
                if (mid.getName().equals(name))
                    return mid.getOriginalName();
            }
        }
        if(md.getInputs() != null) {
            for (ModelInput mid : md.getInputs()) {
                if (mid.getName().equals(name))
                    return mid.getOriginalName();
            }
        }
        System.err.println("Name not found in provided model data");
        System.exit(1);
        return null;
    }
    private Type getType(String name){
        if(md.getComponents() != null) {
            for (ModelInput mid : md.getComponents()) {
                if (mid.getOriginalName().equals(name))
                    return mid.getType();
            }
        }
        if(md.getParam() != null) {
            for (ModelInput mid : md.getParam()) {
                if (mid.getOriginalName().equals(name))
                    return mid.getType();
            }
        }
        if(md.getInputs() != null) {
            for (ModelInput mid : md.getInputs()) {
                if (mid.getOriginalName().equals(name))
                    return mid.getType();
            }
        }
        System.err.println("Type not found in provided model data");
        System.exit(1);
        return null;
    }

    private List<int[]> generate(int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        int[] combination = new int[r];

        for (int i = 0; i < r; i++) {
            combination[i] = i;
        }

        while (combination[r - 1] < n) {
            combinations.add(combination.clone());

            int t = r - 1;
            while (t != 0 && combination[t] == n - r + t) {
                t--;
            }
            combination[t]++;
            for (int i = t + 1; i < r; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }

        return combinations;
    }
}
