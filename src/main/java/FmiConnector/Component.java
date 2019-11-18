package FmiConnector;

import lombok.Data;

@Data
public class Component {
    private String name;
    private TYPE type;
    private Object value;

    public Component(String name, TYPE type){
        this.name = name;
        this.type = type;
    }

    public Component(String name, Object value){
        this.name = name;
        this.value = value;
    }

}
