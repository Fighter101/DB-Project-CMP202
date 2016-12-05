package sample;

/**
 * Created by Hassan on 12/5/2016.
 */
public class Pair {
    String fieldName;
    String value;

    public Pair(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldName() {

        return fieldName;
    }

    public String getValue() {
        return value;
    }
}
