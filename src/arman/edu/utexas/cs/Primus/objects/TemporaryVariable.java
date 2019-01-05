package arman.edu.utexas.cs.Primus.objects;

import java.math.BigDecimal;

/**
 * An extension of the Primus Variable class that is intended to last
 * for a temporary time period. This TemporaryVariable class has the
 * ability to "expire."
 */
public class TemporaryVariable extends Variable {

    // Whether or not this TemporaryVariable is expired.
    private boolean expired;

    /**
     * Constructs a Temporary Variable object.
     *
     * @param id    - PrimusObject id.
     * @param value - Variable value.
     */
    public TemporaryVariable(String id, String value) {
        super(id, value);
        this.expired = false;
    }

    /**
     * Constructs a Temporary Variable object.
     *
     * @param id    - PrimusObject id.
     * @param value - Variable value as a BigDecimal object.
     */
    public TemporaryVariable(String id, BigDecimal value) {
        super(id, value);
        this.expired = false;
    }

    /**
     * Constructs a clone of another Temporary Variable.
     *
     * @param clone - Temporary Variable to clone from.
     */
    public TemporaryVariable(TemporaryVariable clone) {
        super(clone);
        this.expired = clone.isExpired();
    }

    /**
     * @return if this Temporary Variable is expired.
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * Sets the expiration boolean of this Temporary Variable.
     *
     * @param expired - The intended expiration value.
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
