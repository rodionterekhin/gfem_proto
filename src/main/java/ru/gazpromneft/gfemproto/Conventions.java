package ru.gazpromneft.gfemproto;

// Файл описывает соглашения, принятые для работы программы
public class Conventions {
    public static final String STATE_FILE_NAME = "data.bin";

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
