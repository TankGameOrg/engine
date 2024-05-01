package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatusDecoder;

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

    public TeamTank(JSONObject json, Position position, IAttributeDecoder<E> attributeDecoder, IStatusDecoder statusDecoder) {
        super(json, position, attributeDecoder, statusDecoder);
        this.team = json.getString("team");
        this.previousTeams = new HashSet<>();
        JSONArray previousTeamsJson = json.getJSONArray("previous_teams");
        for (int i = 0; i < previousTeamsJson.length(); ++i) {
            this.previousTeams.add(previousTeamsJson.getString(i));
        }
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
        JSONArray previousTeamsJson = new JSONArray();

        for(String team : previousTeams) {
            previousTeamsJson.put(team);
        }

        output.put("previous_teams", previousTeamsJson);
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
