package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

import java.util.HashSet;
import java.util.Set;

public class TeamTank extends Tank implements ITeamed {

    protected String team;
    protected final Set<String> previousTeams;

    public TeamTank(String player, String team, Position position, int actions, int gold, int durability, int range, int bounty, boolean dead) {
        super(player, position, actions, gold, durability, range, bounty, dead);
        this.team = team;
        this.previousTeams = new HashSet<>();
    }

    @Override
    public String getTeam() {
        return team;
    }

    @Override
    public void setTeam(String team) {
        this.previousTeams.add(this.team);
        this.team = team;
    }

    public Set<String> getPreviousTeams() {
        return previousTeams;
    }

    @Override
    public String toString() {
        if (dead) {
            return String.format("[%s, %s, %s, HP: %d]", player, team, position.toString(), durability);
        } else {
            return String.format("[%s, %s, %s, AP: %d HP: %d R: %d G: %d]", player, team, position.toString(), actions, durability, range, gold);
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("team", team);
        return output;
    }

    @Override
    public JSONObject toShortJson() {
        JSONObject output = new JSONObject();
        output.put("type", "tank");
        output.put("name", this.player);
        output.put("team", team);
        return output;
    }
}
