package pro.trevor.tankgame.state.meta;

import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.IPlayerElement;

import java.util.HashSet;
import java.util.Set;

public class Council implements IPlayerElement, IMetaElement {

    private int coffer;
    private final Set<String> councillors;
    private final Set<String> senators;

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

    public Set<String> getCouncillors() {
        return councillors;
    }

    public Set<String> getSenators() {
        return senators;
    }

    @Override
    public String getPlayer() {
        return "Council";
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
