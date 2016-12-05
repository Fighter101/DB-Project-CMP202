package sample;

/**
 * Created by Hassan on 12/5/2016.
 */
public class Pair {
    String fieldName;
    String value;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setValue(String value) {
        this.value = value;
    }

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
