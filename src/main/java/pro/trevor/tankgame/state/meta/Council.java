package pro.trevor.tankgame.state.meta;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.util.Util;

import java.util.HashSet;
import java.util.Set;

public class Council implements IPlayerElement, IMetaElement {

    private int coffer;
    private final Set<String> councillors;
    private final Set<String> senators;
    private boolean canBounty;

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

    public boolean canBounty() {
        return canBounty;
    }

    public void setCanBounty(boolean canBounty) {
        this.canBounty = canBounty;
    }

    @Override
    public Player getPlayer() {
        return new Player("Council");
    }

    @Override
    public String toString() {
        return "council: [\n  coffer: " + coffer +
                "\n  councillors: " + Util.toString(councillors, 4) +
                "  senators: " + Util.toString(senators, 4);
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "council");
        output.put("council", new JSONArray(councillors));
        output.put("senate", new JSONArray(senators));
        output.put("coffer", coffer);
        output.put("can_bounty", canBounty);
        return output;
    }
}
