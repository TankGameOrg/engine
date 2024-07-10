package pro.trevor.tankgame.rule.impl.util;

import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.impl.IRulesetRegister;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.List;

/**
 * This is a basic empty ruleset so that versions need not implement functions that will be empty.
 */
public abstract class BaseRulesetRegister implements IRulesetRegister {
    @Override
    public void registerEnforcerRules(Ruleset ruleset) {

    }

    @Override
    public void registerTickRules(Ruleset ruleset) {

    }

    @Override
    public void registerConditionalRules(Ruleset ruleset) {

    }

    @Override
    public void registerPlayerRules(Ruleset ruleset) {

    }

    public static Object getOnePlayerObject(State state, PlayerRef playerRef) {
        List<IElement> elements = state.getBoard().gatherAllElements().stream()
                .filter(e -> e instanceof IPlayerElement)
                .filter(t -> ((IPlayerElement) t).getPlayerRef().equals(playerRef)).toList();
        if (elements.isEmpty()) {
            return state.getCouncil();
        } else if (elements.size() != 1)  {
            throw new Error("Invalid state: found multiple elements for " + playerRef);
        } else {
            return elements.getFirst();
        }
    }
}
