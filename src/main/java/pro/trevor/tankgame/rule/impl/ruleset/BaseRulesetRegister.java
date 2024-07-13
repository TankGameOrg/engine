package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
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
}
