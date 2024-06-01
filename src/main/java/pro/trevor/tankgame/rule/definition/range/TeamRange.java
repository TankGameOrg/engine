package pro.trevor.tankgame.rule.definition.range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;

public class TeamRange extends FunctionVariableRange<GenericElement, String> {

    public TeamRange(List<String> teamNames) {
        super("team", (state, tank) -> GetOtherTeams(Attribute.TEAM.from(tank).orElse(""), teamNames));
    }

    private static Set<String> GetOtherTeams(String currentTeam, List<String> allTeamNames)
    {
        Set<String> output = new HashSet<>();
        for (String team : allTeamNames)
        {
            if (!team.equals(currentTeam)) output.add(team);
        }
        return output;
    }

    @Override
    public Class<String> getBoundClass() {
        return String.class;
    }

    @Override
    public String getJsonDataType() {
        return "team";
    }
    
}
