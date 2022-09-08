package ru.gazpromneft.gfemproto.model;

import java.io.Serializable;

public class OutputData implements Serializable {


    private String textReport;

    protected OutputData() {

    }

    protected void setTextReport(String textReport) {
        this.textReport = textReport;
    }

    public String getTextReport() {
        return textReport;
    }
}
