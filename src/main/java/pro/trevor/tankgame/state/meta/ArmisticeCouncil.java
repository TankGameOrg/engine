package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;

public class ArmisticeCouncil extends Council {

    private final int armisticeVoteCap;
    private int armisticeVoteCount;

    public ArmisticeCouncil(int armisticeVoteCap, int armisticeVoteCount) {
        super();
        this.armisticeVoteCap = armisticeVoteCap;
        this.armisticeVoteCount = armisticeVoteCount;
    }

    public int getArmisticeVoteCap() {
        return armisticeVoteCap;
    }

    public int getArmisticeVoteCount() {
        return armisticeVoteCount;
    }

    public void setArmisticeVoteCount(int armisticeVoteCount) {
        this.armisticeVoteCount = armisticeVoteCount;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("armistice_vote_cap", armisticeVoteCap);
        output.put("armistice_vote_count", armisticeVoteCount);
        return output;
    }
}
