package util;

import model.Component;
import model.ModelInput;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExtractedData {
    List<Component> componentsToRead = new ArrayList<>();
    List<ModelInput> modelInputList = new ArrayList<>();

    public ExtractedData(List<Object> deserializedData){
        for(Object data : deserializedData){
            if(data instanceof List){
                List<Object> listOb = (List<Object>) data;
                if(!listOb.isEmpty() && listOb.get(0) instanceof Component){
                    for(Object ob : listOb)
                        componentsToRead.add((Component) ob);
                }
                else
                    for(Object ob : listOb)
                        modelInputList.add((ModelInput) ob);
            }
        }
    }
}
