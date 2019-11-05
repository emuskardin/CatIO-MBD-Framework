package abductive.combinatorial;

import com.google.common.collect.Sets;
import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.service.engine.IpoEngine;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Data
public class MLCA {
    SUT sut;
    IpoEngine engine;
    ArrayList<Parameter> inputs;
    ArrayList<Parameter> params;
    ArrayList<Parameter> components;

    public MLCA(ModelData modelData){
        sut = new SUT(modelData.getPathToFmi());
        engine = new IpoEngine(sut);
        components = addParam(modelData.getComponents());
        inputs = addParam(modelData.getInputs());
        params = addParam(modelData.getParam());
    }

    public void createTestSuite(String filename){
        engine.build();
        TestSet ts = engine.getTestSet();
        TestSetWrapper wrapper = new TestSetWrapper(ts, sut);
        wrapper.outputInCSVFormat(filename);
    }

    public void addRelationToGroup(List<Parameter> comps, Integer relStrength){
        Relation relation = new Relation(relStrength);
        comps.forEach(relation::addParam);
        sut.addRelation(relation);
    }

    private ArrayList<Parameter> addParam(List<CombCompData> components){
        if(components == null)
            return null;
        ArrayList<Parameter> params = new ArrayList<>();
        for(CombCompData compIter : components){
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
        sut.addConstraint(new Constraint(correctCompConstaintBuilder(components, num), components));
    }

    public void numberOfCorrectComps(List<Integer> nums){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.size(); i++) {
            if(i > 0)
                sb.append(" || ");
            sb.append(correctCompConstaintBuilder(components, nums.get(i)));
        }
        sut.addConstraint(new Constraint(sb.toString(), components));
    }

    private String correctCompConstaintBuilder(ArrayList<Parameter> params, Integer correctNum){
        StringBuilder sb = new StringBuilder();
        Sets.combinations(new HashSet<>(params), correctNum).forEach(comb -> {
                sb.append("(");
                int index = 0;
                for(Iterator<Parameter> it = comb.iterator(); it.hasNext(); index++){
                    if (index > 0)
                        sb.append(" && ");
                    sb.append(it.next().getName()).append("=\"ok\"");
                    }
                sb.append(") ||");
            }
        );
        sb.setLength(sb.length() - 3);
        return sb.toString();
    }
}
