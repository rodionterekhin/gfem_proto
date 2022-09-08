package ru.gazpromneft.gfemproto.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputData implements Serializable {
    private String name;
    private final Map<String, Object> data;
    private ExcelModel attachedModel;
    private OutputData calculationResult;
    public InputData(String name, HashMap<String, Object> data) {
        this.name = name;
        this.data = data;
        this.attachedModel = null;
    }

    public HashMap<String, Class<?>> getDescriptor() {
        HashMap<String, Class<?>> typeHashMap = new HashMap<>();
        data.forEach((String k, Object v) -> typeHashMap.put(k, v.getClass()));
        return typeHashMap;
    }

    public void attachModel(ExcelModel model) {
        if (model != null && model.equals(this.attachedModel))
            return;
        if (Objects.isNull(model) && Objects.isNull(attachedModel))
            return;
        String msg;
        if (Objects.isNull(model))
            msg = "InputData " + this.name + ": model cleared";
        else
            msg = "InputData " + this.name + ": model changed to " + model.name;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, msg);
        attachedModel = model;
    }

    public ExcelModel getAttachedModel() {
        return attachedModel;
    }

    public void release() {
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
