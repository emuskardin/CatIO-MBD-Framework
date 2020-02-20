package abductive;

import model.*;
import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.service.engine.IpoEngine;
import lombok.Data;

import java.io.*;
import java.util.*;

@Data
public class MCA {
    SUT sut;
    IpoEngine engine;
    ArrayList<Parameter> inputs;
    ArrayList<Parameter> params;
    ArrayList<Parameter> modeAssigments;
    ModelData modelData;
    Map<String, String> tmpNames = new HashMap<>();
    String mlcaCSVFile;

    public MCA(ModelData modelData){
        sut = new SUT();
        engine = new IpoEngine(sut);
        modeAssigments = addParam(modelData.getModeAssigmentVars());
        inputs = addParam(modelData.getInputs());
        params = addParam(modelData.getParam());
        this.modelData = modelData;
    }

    public void createTestSuite(String filename){
        engine.build();
        TestSet ts = engine.getTestSet();
        TestSetWrapper wrapper = new TestSetWrapper(ts, sut);
        wrapper.outputInCSVFormat(filename);
        fixNames(filename);
        mlcaCSVFile = filename;
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
            String compName = compIter.getName();
            String tmpName = compName.replace(".", "_");
            tmpNames.put(tmpName, compName);
            Parameter comp = sut.addParam(tmpName);
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
        sut.addConstraint(new Constraint(correctCompConstraintBuilder(modeAssigments, num), modeAssigments));
    }

    public void numberOfCorrectComps(Integer... nums){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.length; i++) {
            if(i > 0)
                sb.append(" || ");
            sb.append(correctCompConstraintBuilder(modeAssigments, nums[i]));
        }
        sut.addConstraint(new Constraint(sb.toString(), modeAssigments));
    }

    public List<Scenario> scenariosFromMLCA(Integer faultInjectionStep){
        List<Scenario> suite = new ArrayList<>();
        Integer testCounter = 1;
        try {
            for(List<Component> test : suitToSimulationInput(mlcaCSVFile)){
                Scenario scen = new Scenario("MLCA Row " + testCounter);
                scen.addToMap(0, modelData.getAllOkStates());
                scen.addToMap(faultInjectionStep, test);
                suite.add(scen);
                testCounter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suite;
    }

    public void addConstraint(String constraint){
        sut.addConstraint(new Constraint(constraint, sut.getParameters()));
    }

    private String correctCompConstraintBuilder(ArrayList<Parameter> params, Integer correctNum){
        StringBuilder sb = new StringBuilder();
        List<int[]> combinations = generateCombinations(params.size(), correctNum);
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
            }else {
                List<String> test = Arrays.asList(line.split(","));
                List<Component> rowInput = new ArrayList<>();
                for(int i = 0; i < test.size(); i++) {
                    Type type = modelData.getType(nameList.get(i));
                    Component comp = new Component(nameList.get(i), type);
                    Integer compValue = modelData.getFaultIndex(test.get(i));
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

    private void fixNames(String filename) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(filename));
            StringBuilder inputBuffer = new StringBuilder();
            String line;

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();
            String inputStr = inputBuffer.toString();
            for(String name: tmpNames.keySet())
                inputStr = inputStr.replace(name, tmpNames.get(name));

            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    private List<int[]> generateCombinations(int n, int r) {
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
