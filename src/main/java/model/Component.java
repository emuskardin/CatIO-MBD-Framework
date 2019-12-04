package model;

import lombok.Data;

import java.io.Serializable;

/**
 * Component class represents each value which can be read or written from and to modelica simulation
 * Each component consist of name string, type from enum TYPE and value object which is fetched form simulation
 */
@Data
public class Component implements Serializable {
    private String name;
    private Type type;
    private Object value;

    public Component(String name, Type type){
        this.name = name;
        this.type = type;
    }

    public Component(String name, Type type, Object value){
        this.name = name;
        this.type = type;
        this.value = value;
    }

}
