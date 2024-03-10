package state.meta;

import java.util.HashSet;
import java.util.Set;

public class Council {

    private int coffer;
    private final Set<Player> councillors;
    private final Set<Player> senators;

    public Council(int coffer) {
        this.coffer = coffer;
        this.councillors = new HashSet<>();
        this.senators = new HashSet<>();
    }

    public Council() {
        this(0);
    }

    public int getCoffer() {
        return coffer;
    }

    public void setCoffer(int coffer) {
        this.coffer = coffer;
    }

    public Set<Player> getCouncillors() {
        return councillors;
    }

    public Set<Player> getSenators() {
        return senators;
    }
}
