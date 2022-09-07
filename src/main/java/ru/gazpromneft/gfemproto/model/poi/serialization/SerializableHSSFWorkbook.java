package ru.gazpromneft.gfemproto.model.poi.serialization;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;

public class SerializableHSSFWorkbook extends SerializableWorkbook implements Serializable {

    public SerializableHSSFWorkbook(HSSFWorkbook workbook) {
        super(workbook);
    }
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        out.write(baos.toByteArray());
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        workbook = new HSSFWorkbook(in);
    }
}
