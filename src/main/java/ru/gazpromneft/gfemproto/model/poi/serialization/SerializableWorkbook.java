package ru.gazpromneft.gfemproto.model.poi.serialization;

import org.apache.poi.ss.usermodel.Workbook;

public class SerializableWorkbook {
    protected transient Workbook workbook;

    // No-argument constructor for deserialization
    protected SerializableWorkbook() {
        this(null);
    }
    protected SerializableWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }
    public Workbook get() {
        return workbook;
    }
}
