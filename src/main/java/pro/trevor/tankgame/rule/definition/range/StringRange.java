package pro.trevor.tankgame.rule.definition.range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;

public class StringRange extends FunctionVariableRange<GenericElement, String> {

    public static StringRange GetOtherTeamsRange(List<String> teamNames) {
        return new StringRange("team", (state, tank) -> GetOtherTeams(Attribute.TEAM.from(tank).orElse(""), teamNames));
    }

    private StringRange(String name, BiFunction<State, GenericElement, Set<String>> generator) {
        super(name, generator);
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
        return "string";
    }
    
}
