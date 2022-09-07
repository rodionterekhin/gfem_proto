package ru.gazpromneft.gfemproto.model.poi.serialization;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.logging.Logger;

public class SerializableXSSFWorkbook extends SerializableWorkbook implements Serializable {

    protected SerializableXSSFWorkbook(Workbook workbook) {
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
        Logger.getAnonymousLogger().info("Deserializing workbook");
        workbook = WorkbookFactory.create(in);
    }
}
