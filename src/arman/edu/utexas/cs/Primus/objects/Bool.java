package arman.edu.utexas.cs.Primus.objects;

public class Bool extends PrimusObject {

    private boolean bool;

    public Bool(String id, String value) {
        super(id, value);
        setValue(parseValue(value));
        this.bool = Boolean.parseBoolean(value);
    }

    public Bool(PrimusObject clone) {
        super(clone);
        this.bool = Boolean.parseBoolean(clone.getValue());
    }

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public void negate(){
        this.bool = !bool;
    }

    public Bool negatedBool(){
        negate();
        return this;
    }

    public String getValue(){
        return parseValue(bool);
    }

    private String parseValue(boolean b){
        return b ? "T" : "F";
    }

    private String parseValue(String value){
        boolean b = Boolean.parseBoolean(value);
        return b ? "T" : "F";
    }
}
