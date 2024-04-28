package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TeamTank<E extends Enum<E> & IAttribute> extends GenericTank<E> implements ITeamed {

    protected String team;
    protected final Set<String> previousTeams;

    public TeamTank(String player, Position position, Map<E, Object> defaults, String team) {
        super(player, position, defaults);
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
