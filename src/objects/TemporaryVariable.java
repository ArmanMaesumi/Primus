package objects;

import java.math.BigDecimal;

public class TemporaryVariable extends Variable {

    private boolean expired;

    public TemporaryVariable(String id, String value) {
        super(id, value);
        this.expired = false;
    }

    public TemporaryVariable(String id, BigDecimal value) {
        super(id, value);
        this.expired = false;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
