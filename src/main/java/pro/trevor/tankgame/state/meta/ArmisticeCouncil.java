package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;

public class ArmisticeCouncil extends Council {

    private int armisticeVotesRemaining;

    public ArmisticeCouncil(int armisticeVotesRemaining) {
        super();
        this.armisticeVotesRemaining = armisticeVotesRemaining;
    }

    public int getArmisticeVotesRemaining() {
        return armisticeVotesRemaining;
    }

    public void setArmisticeVotesRemaining(int armisticeVotesRemaining) {
        this.armisticeVotesRemaining = Math.max(0, armisticeVotesRemaining);
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("armistice_votes_remaining", armisticeVotesRemaining);
        return output;
    }
}
