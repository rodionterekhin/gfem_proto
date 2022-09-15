package ru.gazpromneft.gfemproto;

import java.util.ResourceBundle;

// Файл описывает соглашения, принятые для работы программы
public class Conventions {
    public static final String STATE_FILE_NAME = "data.bin";
    public static final String EMPTY_MODEL = App.getStrings().getString("empty.model");
    public static final double RELATIVE_TOLERANCE = 1e-9;
    public static final String ICON_PATH = "/icon.png";

    public enum VariableType {
        NUMERIC("Число"),
        ARRAY("Вектор"),
        INDEX("Индекс");

        private final String keyText;

        VariableType(String keyText) {
            this.keyText = keyText;
        }

        public static VariableType fromText(String text) {
            for (VariableType b : VariableType.values()) {
                if (b.keyText.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }


    }
}
