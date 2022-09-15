package ru.gazpromneft.gfemproto.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InputData implements Serializable {
    private String name;
    private final Map<String, Object> data;
    public InputData(String name, HashMap<String, Object> data) {
        this.name = name;
        this.data = data;
    }

    public HashMap<String, Class<?>> getDescriptor() {
        HashMap<String, Class<?>> typeHashMap = new HashMap<>();
        data.forEach((String k, Object v) -> typeHashMap.put(k, v.getClass()));
        return typeHashMap;
    }

//    public InputData (HashMap<> values) {
//
//    }
//

    @Override
    public String toString() {
        return name;
    }

    public Map<String, Object> asMap() {
        return data;
    }

    public void changeName(String name) {
        this.name = name;
    }

}
