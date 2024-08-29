package pro.trevor.tankgame.rule.impl.ruleset;

import pro.trevor.tankgame.rule.definition.Ruleset;

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
