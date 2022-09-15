package ru.gazpromneft.gfemproto.model;

import java.io.Serializable;
import java.util.HashMap;

public class OutputData implements Serializable {


    private String textReport;
    private final HashMap<String, Object> map;

    protected OutputData(HashMap<String, Object> map) {
        this.map = map;
    }

    protected void setTextReport(String textReport) {
        this.textReport = textReport;
    }

    public String getTextReport() {
        return textReport;
    }

    public HashMap<String, Object> asMap() {
        return map;
    }
}
