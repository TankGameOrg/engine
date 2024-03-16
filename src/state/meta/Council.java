package state.meta;

import rule.type.IMetaElement;
import rule.type.IPlayerElement;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Council implements IPlayerElement, IMetaElement {

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

    @Override
    public Player[] getPlayers() {
        return Stream.concat(councillors.stream(), senators.stream()).toList().toArray(new Player[0]);
    }

    @Override
    public String toString() {
        return "Council(" +
                "coffer=" + coffer +
                ", councillors=" + councillors +
                ", senators=" + senators +
                ')';
    }
}
