package ru.gazpromneft.gfemproto;

// Файл описывает соглашения, принятые для работы программы
public class Conventions {
    public static final String STATE_FILE_NAME = "data.bin";
    public static final String EMPTY_MODEL = "(без модели)";
    public static final String ABOUT_MESSAGE = "Apache POI Demo для ГФЭМ\nАвтор - Терехин Родион\nПАО \"Газпром нефть\", 2022";

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
