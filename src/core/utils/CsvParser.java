package core.utils;

public class CsvParser<T> {

    private final String PATH;
    private final String DELIMITER;

    enum Delimiter {
        COMMA(","), SEMICOLON(";"), WHITESPACE(" ");

        String delimiter;

        Delimiter(String del){
            this.delimiter = del;
        }

        public String getDelimiter() {
            return delimiter;
        }
    }

    public CsvParser(String file_path, String delimiter){
        this.PATH = file_path;
        this.DELIMITER = delimiter;
    }

    public CsvParser(String file_Path){
        this(file_Path, Delimiter.SEMICOLON.getDelimiter());
    }

    public CsvParser(String file_path, Delimiter delimiter){
        this(file_path, delimiter.getDelimiter());
    }

}
