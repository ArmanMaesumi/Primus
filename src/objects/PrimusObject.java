package objects;

import java.io.Serializable;

public class PrimusObject implements Serializable {

    private String id;
    private String value;

    public PrimusObject(String id, String value) {
        //value = value.trim();
        this.id = id;
        this.value = value;
    }

    public PrimusObject(PrimusObject clone) {
        this.id = clone.id;
        this.value = clone.value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof PrimusObject)) return false;
        return this.id.equals(((PrimusObject) o).id);
    }
}
